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
        for (final Map.Entry<Target, List<AsyncHandler>[]> entry : eventHandlers.entrySet()) {
            final List<AsyncHandler> handlers = entry.getValue()[event];
            if (handlers == null) continue;
            final List<ScheduledJob> targetJobs = jobs.get(entry.getKey());
            for (final AsyncHandler handler : handlers) {
                targetJobs.add(new ScheduledJob(handler));
            }
        }
    }

    public void runUntilComplete() {
        boolean hasJobs;
        mainLoop:
        do {
            hasJobs = false;
            for (int i = targets.size() - 1; i >= 0; i--) {
                final Target target = targets.get(i);
                final List<ScheduledJob> targetJobs = jobs.get(target);
                hasJobs |= !targetJobs.isEmpty();
                final Iterator<ScheduledJob> iter = targetJobs.iterator();
                while (iter.hasNext()) {
                    final ScheduledJob job = iter.next();
                    final int state = job.handler.handle(target, job);
                    switch (state) {
                        case SUSPEND_NO_RESCHEDULE:
                            iter.remove();
                            continue;
                        case SUSPEND_CANCEL_ALL:
                            break mainLoop;
                    }
                    job.label = state;
                }
            }
        } while (hasJobs);
    }
}
