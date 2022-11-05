package io.github.gaming32.scratch2jvm.runtime.async;

import io.github.gaming32.scratch2jvm.runtime.target.Target;

import java.util.*;

@SuppressWarnings("unused")
public final class AsyncScheduler {
    private static final int SUSPEND_NO_RESCHEDULE = -1;
    private static final int SUSPEND_CANCEL_ALL = -2;

    private static final int EVENT_FLAG_CLICKED = 0;
    private static final int EVENT_COUNT = EVENT_FLAG_CLICKED + 1;

    private final List<Target> targets = new ArrayList<>();
    private final Map<Target, List<ScheduledJob>> jobs = new IdentityHashMap<>();
    private final Map<Target, List<AsyncHandler>[]> eventHandlers = new IdentityHashMap<>();

    public void addTarget(Target target) {
        targets.add(target);
        addTarget0(target);
    }

    public void addTarget(Target target, Target after) {
        final int index = targets.indexOf(after) + 1;
        targets.add(index, target);
        for (int i = index, n = targets.size(); i < n; i++) {
            targets.get(i).layerOrder = i;
        }
        addTarget0(target);
    }

    @SuppressWarnings("unchecked")
    private void addTarget0(Target target) {
        jobs.put(target, new ArrayList<>());
        eventHandlers.put(target, new List[EVENT_COUNT]);
        target.registerEvents(this);
    }

    public void removeTarget(Target target) {
        final int index = targets.indexOf(target);
        targets.remove(index);
        for (int i = index, n = targets.size(); i < n; i++) {
            targets.get(i).layerOrder = i;
        }
    }

    public void scheduleJob(Target target, AsyncHandler function) {
        jobs.get(target).add(new ScheduledJob(function));
    }

    public void registerEventHandler(Target target, int event, AsyncHandler handler) {
        final List<AsyncHandler>[] allHandlers = eventHandlers.get(target);
        List<AsyncHandler> handlers;
        if ((handlers = allHandlers[event]) == null) {
            handlers = allHandlers[event] = new ArrayList<>();
        }
        handlers.add(handler);
    }

    public void scheduleEvent(int event) {
        for (final Target target : eventHandlers.keySet()) {
            scheduleEvent(event, target);
        }
    }

    public void scheduleEvent(int event, Target target) {
        final List<AsyncHandler> handlers = eventHandlers.get(target)[event];
        if (handlers == null) return;
        final List<ScheduledJob> targetJobs = jobs.get(target);
        for (final AsyncHandler handler : handlers) {
            targetJobs.add(new ScheduledJob(handler));
        }
    }

    public Set<ScheduledJob> scheduleEventAndList(int event) {
        final Set<ScheduledJob> scheduled = new HashSet<>();
        for (final Map.Entry<Target, List<AsyncHandler>[]> entry : eventHandlers.entrySet()) {
            final List<AsyncHandler> handlers = entry.getValue()[event];
            if (handlers == null) continue;
            final List<ScheduledJob> targetJobs = jobs.get(entry.getKey());
            for (final AsyncHandler handler : handlers) {
                final ScheduledJob job = new ScheduledJob(handler);
                targetJobs.add(job);
                scheduled.add(job);
            }
        }
        return scheduled;
    }

    public void cancelJobs(Target target, ScheduledJob excluding) {
        final Iterator<ScheduledJob> iter = jobs.get(target).iterator();
        while (iter.hasNext()) {
            final ScheduledJob job = iter.next();
            if (job == excluding) continue;
            job.finished = true;
            iter.remove();
        }
    }

    public void runUntilComplete() {
        boolean hasJobs;
        do {
            hasJobs = false;
            targetsIter:
            for (int i = targets.size() - 1; i >= 0; i--) {
                final Target target = targets.get(i);
                final List<ScheduledJob> targetJobs = jobs.get(target);
                hasJobs |= !targetJobs.isEmpty();
                final Iterator<ScheduledJob> iter = targetJobs.iterator();
                while (iter.hasNext()) {
                    ScheduledJob job = iter.next();
                    ScheduledJob parentJob = null;
                    while (job.awaiting != null && !job.awaiting.finished) {
                        parentJob = job;
                        job = job.awaiting;
                    }
                    if (job.awaiting != null) {
                        job.awaiting = null;
                    }
                    final int state = job.handler.handle(target, job);
                    switch (state) {
                        case SUSPEND_NO_RESCHEDULE:
                            job.finished = true;
                            if (parentJob == null) {
                                iter.remove();
                                if (targetJobs.isEmpty()) continue targetsIter;
                            } else {
                                parentJob.awaiting = null;
                            }
                            continue;
                        case SUSPEND_CANCEL_ALL:
                            job.finished = true;
                            return;
                    }
                    job.label = state;
                    if (targetJobs.size() == 1) continue targetsIter;
                }
            }
        } while (hasJobs);
    }
}
