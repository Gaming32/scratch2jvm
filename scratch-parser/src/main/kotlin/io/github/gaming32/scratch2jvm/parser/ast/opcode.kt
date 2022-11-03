package io.github.gaming32.scratch2jvm.parser.ast

public enum class ScratchOpcodes(public val id: String) {
    MOTION_SETX("motion_setx"),

    LOOKS_SAY("looks_say"),

    EVENT_WHENFLAGCLICKED("event_whenflagclicked"),

    CONTROL_FOREVER("control_forever"),
    CONTROL_IF("control_if"),

    OPERATOR_SUBTRACT("operator_subtract"),
    OPERATOR_DIVIDE("operator_divide"),
    OPERATOR_GT("operator_gt"),
    OPERATOR_JOIN("operator_join"),

    DATA_SETVARIABLETO("data_setvariableto"),
    DATA_CHANGEVARIABLEBY("data_changevariableby"),

    PROCEDURES_DEFINITION("procedures_definition"),
    PROCEDURES_PROTOTYPE("procedures_prototype"),
    PROCEDURES_CALL("procedures_call"),

    ARGUMENT_REPORTER_STRING_NUMBER("argument_reporter_string_number"),
    ;

    public companion object {
        private val ID_TO_OPCODE = ScratchOpcodes.values().associateBy { it.id }

        @JvmStatic
        public fun fromId(id: String): ScratchOpcodes =
            ID_TO_OPCODE[id] ?: throw IllegalArgumentException("Unknown opcode $id")
    }
}

public object MotionOpcodes {
    public val SET_X: ScratchOpcodes = ScratchOpcodes.MOTION_SETX
}

public object LooksOpcodes {
    public val SAY: ScratchOpcodes = ScratchOpcodes.LOOKS_SAY
}

public object EventsOpcodes {
    public val WHEN_FLAG_CLICKED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENFLAGCLICKED
}

public object ControlOpcodes {
    public val FOREVER: ScratchOpcodes = ScratchOpcodes.CONTROL_FOREVER
    public val IF: ScratchOpcodes = ScratchOpcodes.CONTROL_IF
}

public object OperatorsOpcodes {
    public val SUBTRACT: ScratchOpcodes = ScratchOpcodes.OPERATOR_SUBTRACT
    public val DIVIDE: ScratchOpcodes = ScratchOpcodes.OPERATOR_DIVIDE
    public val GT: ScratchOpcodes = ScratchOpcodes.OPERATOR_GT
    public val JOIN: ScratchOpcodes = ScratchOpcodes.OPERATOR_JOIN
}

public object VariablesOpcodes {
    public val SET_VARIABLE_TO: ScratchOpcodes = ScratchOpcodes.DATA_SETVARIABLETO
    public val CHANGE_VARIABLE_BY: ScratchOpcodes = ScratchOpcodes.DATA_CHANGEVARIABLEBY
}

public object MyBlocksOpcodes {
    public val DEFINITION: ScratchOpcodes = ScratchOpcodes.PROCEDURES_DEFINITION
    public val PROTOTYPE: ScratchOpcodes = ScratchOpcodes.PROCEDURES_PROTOTYPE
    public val CALL: ScratchOpcodes = ScratchOpcodes.PROCEDURES_CALL

    public object ArgumentTypes {
        public val REPORTER_STRING_NUMBER: ScratchOpcodes = ScratchOpcodes.ARGUMENT_REPORTER_STRING_NUMBER
    }
}
