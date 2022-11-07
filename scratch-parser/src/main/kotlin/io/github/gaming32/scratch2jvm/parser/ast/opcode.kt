package io.github.gaming32.scratch2jvm.parser.ast

public enum class ScratchOpcodes(public val id: String) {
    MOTION_MOVESTEPS("motion_movesteps"),
    MOTION_GOTO("motion_goto"),
    MOTION_GOTO_MENU("motion_goto_menu"),
    MOTION_GOTOXY("motion_gotoxy"),
    MOTION_GLIDETO("motion_glideto"),
    MOTION_GLIDETO_MENU("motion_glideto_menu"),
    MOTION_GLIDESECSTOXY("motion_glidesecstoxy"),
    MOTION_POINTINDIRECTION("motion_pointindirection"),
    MOTION_POINTTOWARDS("motion_pointtowards"),
    MOTION_POINTTOWARDS_MENU("motion_pointtowards_menu"),
    MOTION_CHANGEXBY("motion_changexby"),
    MOTION_SETX("motion_setx"),
    MOTION_CHANGEYBY("motion_changeyby"),
    MOTION_SETY("motion_sety"),
    MOTION_IFONEDGEBOUNCE("motion_ifonedgebounce"),
    MOTION_SETROTATIONSTYLE("motion_setrotationstyle"),
    MOTION_XPOSITION("motion_xposition"),
    MOTION_YPOSITION("motion_yposition"),

    LOOKS_SAY("looks_say"),

    EVENT_WHENFLAGCLICKED("event_whenflagclicked"),

    CONTROL_WAIT("control_wait"),
    CONTROL_REPEAT("control_repeat"),
    CONTROL_FOREVER("control_forever"),
    CONTROL_IF("control_if"),
    CONTROL_IF_ELSE("control_if_else"),
    CONTROL_WAIT_UNTIL("control_wait_until"),
    CONTROL_STOP("control_stop"),

    SENSING_TOUCHINGOBJECT("sensing_touchingobject"),
    SENSING_TOUCHINGOBJECTMENU("sensing_touchingobjectmenu"),
    SENSING_KEYPRESSED("sensing_keypressed"),
    SENSING_KEYOPTIONS("sensing_keyoptions"),
    SENSING_MOUSEDOWN("sensing_mousedown"),
    SENSING_TIMER("sensing_timer"),

    OPERATOR_ADD("operator_add"),
    OPERATOR_SUBTRACT("operator_subtract"),
    OPERATOR_MULTIPLY("operator_multiply"),
    OPERATOR_DIVIDE("operator_divide"),
    OPERATOR_RANDOM("operator_random"),
    OPERATOR_GT("operator_gt"),
    OPERATOR_LT("operator_lt"),
    OPERATOR_EQUALS("operator_equals"),
    OPERATOR_AND("operator_and"),
    OPERATOR_OR("operator_or"),
    OPERATOR_NOT("operator_not"),
    OPERATOR_JOIN("operator_join"),
    OPERATOR_LETTER_OF("operator_letter_of"),
    OPERATOR_LENGTH("operator_length"),
    OPERATOR_MOD("operator_mod"),
    OPERATOR_MATHOP("operator_mathop"),

    DATA_SETVARIABLETO("data_setvariableto"),
    DATA_CHANGEVARIABLEBY("data_changevariableby"),
    DATA_ADDTOLIST("data_addtolist"),
    DATA_DELETEOFLIST("data_deleteoflist"),
    DATA_DELETEALLOFLIST("data_deletealloflist"),
    DATA_INSERTATLIST("data_insertatlist"),
    DATA_REPLACEITEMOFLIST("data_replaceitemoflist"),
    DATA_ITEMOFLIST("data_itemoflist"),
    DATA_ITEMNUMOFLIST("data_itemnumoflist"),
    DATA_LENGTHOFLIST("data_lengthoflist"),

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
    public val MOVE_STEPS: ScratchOpcodes = ScratchOpcodes.MOTION_MOVESTEPS
    public val GOTO: ScratchOpcodes = ScratchOpcodes.MOTION_GOTO
    public val GOTO_X_Y: ScratchOpcodes = ScratchOpcodes.MOTION_GOTOXY
    public val GLIDE_TO: ScratchOpcodes = ScratchOpcodes.MOTION_GLIDETO
    public val GLIDE_SECS_TO_X_Y: ScratchOpcodes = ScratchOpcodes.MOTION_GLIDESECSTOXY
    public val POINT_IN_DIRECTION: ScratchOpcodes = ScratchOpcodes.MOTION_POINTINDIRECTION
    public val POINT_TOWARDS: ScratchOpcodes = ScratchOpcodes.MOTION_POINTTOWARDS
    public val CHANGE_X_BY: ScratchOpcodes = ScratchOpcodes.MOTION_CHANGEXBY
    public val SET_X: ScratchOpcodes = ScratchOpcodes.MOTION_SETX
    public val CHANGE_Y_BY: ScratchOpcodes = ScratchOpcodes.MOTION_CHANGEYBY
    public val SET_Y: ScratchOpcodes = ScratchOpcodes.MOTION_SETY
    public val IF_ON_EDGE_BOUNCE: ScratchOpcodes = ScratchOpcodes.MOTION_IFONEDGEBOUNCE
    public val SET_ROTATION_STYLE: ScratchOpcodes = ScratchOpcodes.MOTION_SETROTATIONSTYLE
    public val X_POSITION: ScratchOpcodes = ScratchOpcodes.MOTION_XPOSITION
    public val Y_POSITION: ScratchOpcodes = ScratchOpcodes.MOTION_YPOSITION
}

public object LooksOpcodes {
    public val SAY: ScratchOpcodes = ScratchOpcodes.LOOKS_SAY
}

public object EventsOpcodes {
    public val WHEN_FLAG_CLICKED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENFLAGCLICKED
}

public object ControlOpcodes {
    public val WAIT: ScratchOpcodes = ScratchOpcodes.CONTROL_WAIT
    public val REPEAT: ScratchOpcodes = ScratchOpcodes.CONTROL_REPEAT
    public val FOREVER: ScratchOpcodes = ScratchOpcodes.CONTROL_FOREVER
    public val IF: ScratchOpcodes = ScratchOpcodes.CONTROL_IF
    public val STOP: ScratchOpcodes = ScratchOpcodes.CONTROL_STOP
}

public object SensingOpcodes {
    public val TOUCHING_OBJECT: ScratchOpcodes = ScratchOpcodes.SENSING_TOUCHINGOBJECT
    public val KEY_PRESSED: ScratchOpcodes = ScratchOpcodes.SENSING_KEYPRESSED
    public val MOUSE_DOWN: ScratchOpcodes = ScratchOpcodes.SENSING_MOUSEDOWN
    public val TIMER: ScratchOpcodes = ScratchOpcodes.SENSING_TIMER
}

public object OperatorsOpcodes {
    public val ADD: ScratchOpcodes = ScratchOpcodes.OPERATOR_ADD
    public val SUBTRACT: ScratchOpcodes = ScratchOpcodes.OPERATOR_SUBTRACT
    public val MULTIPLY: ScratchOpcodes = ScratchOpcodes.OPERATOR_MULTIPLY
    public val DIVIDE: ScratchOpcodes = ScratchOpcodes.OPERATOR_DIVIDE
    public val RANDOM: ScratchOpcodes = ScratchOpcodes.OPERATOR_RANDOM
    public val GT: ScratchOpcodes = ScratchOpcodes.OPERATOR_GT
    public val LT: ScratchOpcodes = ScratchOpcodes.OPERATOR_LT
    public val EQUALS: ScratchOpcodes = ScratchOpcodes.OPERATOR_EQUALS
    public val AND: ScratchOpcodes = ScratchOpcodes.OPERATOR_AND
    public val OR: ScratchOpcodes = ScratchOpcodes.OPERATOR_OR
    public val NOT: ScratchOpcodes = ScratchOpcodes.OPERATOR_NOT
    public val JOIN: ScratchOpcodes = ScratchOpcodes.OPERATOR_JOIN
    public val LETTER_OF: ScratchOpcodes = ScratchOpcodes.OPERATOR_LETTER_OF
    public val LENGTH: ScratchOpcodes = ScratchOpcodes.OPERATOR_LENGTH
    public val MOD: ScratchOpcodes = ScratchOpcodes.OPERATOR_MOD
    public val MATH_OP: ScratchOpcodes = ScratchOpcodes.OPERATOR_MATHOP
}

public object VariablesOpcodes {
    public val SET_VARIABLE_TO: ScratchOpcodes = ScratchOpcodes.DATA_SETVARIABLETO
    public val CHANGE_VARIABLE_BY: ScratchOpcodes = ScratchOpcodes.DATA_CHANGEVARIABLEBY
    public val ADD_TO_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ADDTOLIST
    public val DELETE_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_DELETEOFLIST
    public val DELETE_ALL_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_DELETEALLOFLIST
    public val INSERT_AT_LIST: ScratchOpcodes = ScratchOpcodes.DATA_INSERTATLIST
    public val REPLACE_ITEM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_REPLACEITEMOFLIST
    public val ITEM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ITEMOFLIST
    public val ITEM_NUM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ITEMNUMOFLIST
    public val LENGTH_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_LENGTHOFLIST
}

public object MyBlocksOpcodes {
    public val DEFINITION: ScratchOpcodes = ScratchOpcodes.PROCEDURES_DEFINITION
    public val PROTOTYPE: ScratchOpcodes = ScratchOpcodes.PROCEDURES_PROTOTYPE
    public val CALL: ScratchOpcodes = ScratchOpcodes.PROCEDURES_CALL

    public object ArgumentTypes {
        public val REPORTER_STRING_NUMBER: ScratchOpcodes = ScratchOpcodes.ARGUMENT_REPORTER_STRING_NUMBER
    }
}
