package io.github.gaming32.scratch2jvm.parser.ast

public enum class ScratchOpcodes(public val id: String) {
    MOTION_MOVESTEPS("motion_movesteps"),
    MOTION_TURNRIGHT("motion_turnright"),
    MOTION_TURNLEFT("motion_turnleft"),
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
    MOTION_DIRECTION("motion_direction"),

    LOOKS_SAYFORSECS("looks_sayforsecs"),
    LOOKS_SAY("looks_say"),
    LOOKS_SWITCHCOSTUMETO("looks_switchcostumeto"),
    LOOKS_COSTUME("looks_costume"),
    LOOKS_CHANGESIZEBY("looks_changesizeby"),
    LOOKS_SETSIZETO("looks_setsizeto"),
    LOOKS_CHANGEEFFECTBY("looks_changeeffectby"),
    LOOKS_SETEFFECTTO("looks_seteffectto"),
    LOOKS_SHOW("looks_show"),
    LOOKS_HIDE("looks_hide"),
    LOOKS_GOTOFRONTBACK("looks_gotofrontback"),
    LOOKS_GOFORWARDBACKWARDLAYERS("looks_goforwardbackwardlayers"),
    LOOKS_COSTUMENUMBERNAME("looks_costumenumbername"),
    LOOKS_SIZE("looks_size"),

    SOUND_PLAYUNTILDONE("sound_playuntildone"),
    SOUND_PLAY("sound_play"),
    SOUND_SOUNDS_MENU("sound_sounds_menu"),
    SOUND_SETVOLUMETO("sound_setvolumeto"),
    SOUND_VOLUME("sound_volume"),

    EVENT_WHENFLAGCLICKED("event_whenflagclicked"),
    EVENT_WHENKEYPRESSED("event_whenkeypressed"),
    EVENT_WHENTHISSPRITECLICKED("event_whenthisspriteclicked"),
    EVENT_WHENBROADCASTRECEIVED("event_whenbroadcastreceived"),
    EVENT_BROADCAST("event_broadcast"),
    EVENT_BROADCASTANDWAIT("event_broadcastandwait"),

    CONTROL_WAIT("control_wait"),
    CONTROL_REPEAT("control_repeat"),
    CONTROL_FOREVER("control_forever"),
    CONTROL_IF("control_if"),
    CONTROL_IF_ELSE("control_if_else"),
    CONTROL_WAIT_UNTIL("control_wait_until"),
    CONTROL_REPEAT_UNTIL("control_repeat_until"),
    CONTROL_STOP("control_stop"),
    CONTROL_START_AS_CLONE("control_start_as_clone"),
    CONTROL_CREATE_CLONE_OF("control_create_clone_of"),
    CONTROL_CREATE_CLONE_OF_MENU("control_create_clone_of_menu"),
    CONTROL_DELETE_THIS_CLONE("control_delete_this_clone"),

    SENSING_TOUCHINGOBJECT("sensing_touchingobject"),
    SENSING_TOUCHINGOBJECTMENU("sensing_touchingobjectmenu"),
    SENSING_DISTANCETO("sensing_distanceto"),
    SENSING_DISTANCETOMENU("sensing_distancetomenu"),
    SENSING_ASKANDWAIT("sensing_askandwait"),
    SENSING_ANSWER("sensing_answer"),
    SENSING_KEYPRESSED("sensing_keypressed"),
    SENSING_KEYOPTIONS("sensing_keyoptions"),
    SENSING_MOUSEDOWN("sensing_mousedown"),
    SENSING_MOUSEX("sensing_mousex"),
    SENSING_MOUSEY("sensing_mousey"),
    SENSING_TIMER("sensing_timer"),
    SENSING_OF("sensing_of"),
    SENSING_OF_OBJECT_MENU("sensing_of_object_menu"),

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
    OPERATOR_ROUND("operator_round"),
    OPERATOR_MATHOP("operator_mathop"),

    DATA_SETVARIABLETO("data_setvariableto"),
    DATA_CHANGEVARIABLEBY("data_changevariableby"),
    DATA_SHOWVARIABLE("data_showvariable"),
    DATA_HIDEVARIABLE("data_hidevariable"),
    DATA_ADDTOLIST("data_addtolist"),
    DATA_DELETEOFLIST("data_deleteoflist"),
    DATA_DELETEALLOFLIST("data_deletealloflist"),
    DATA_INSERTATLIST("data_insertatlist"),
    DATA_REPLACEITEMOFLIST("data_replaceitemoflist"),
    DATA_ITEMOFLIST("data_itemoflist"),
    DATA_ITEMNUMOFLIST("data_itemnumoflist"),
    DATA_LENGTHOFLIST("data_lengthoflist"),
    DATA_LISTCONTAINSITEM("data_listcontainsitem"),

    PROCEDURES_DEFINITION("procedures_definition"),
    PROCEDURES_PROTOTYPE("procedures_prototype"),
    PROCEDURES_CALL("procedures_call"),

    ARGUMENT_REPORTER_STRING_NUMBER("argument_reporter_string_number"),
    ARGUMENT_REPORTER_BOOLEAN("argument_reporter_boolean"),

    PEN_CLEAR("pen_clear"),
    PEN_STAMP("pen_stamp"),
    PEN_PEN_DOWN("pen_penDown"),
    PEN_PEN_UP("pen_penUp"),
    PEN_SET_PEN_COLOR_TO_COLOR("pen_setPenColorToColor"),
    PEN_SET_PEN_COLOR_PARAM_TO("pen_setPenColorParamTo"),
    PEN_MENU_COLOR_PARAM("pen_menu_colorParam"),
    PEN_SET_PEN_SIZE_TO("pen_setPenSizeTo"),
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
    public val TURN_RIGHT: ScratchOpcodes = ScratchOpcodes.MOTION_TURNRIGHT
    public val TURN_LEFT: ScratchOpcodes = ScratchOpcodes.MOTION_TURNLEFT

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
    public val DIRECTION: ScratchOpcodes = ScratchOpcodes.MOTION_DIRECTION
}

public object LooksOpcodes {
    public val SAY_FOR_SECS: ScratchOpcodes = ScratchOpcodes.LOOKS_SAYFORSECS
    public val SAY: ScratchOpcodes = ScratchOpcodes.LOOKS_SAY

    public val SWITCH_COSTUME_TO: ScratchOpcodes = ScratchOpcodes.LOOKS_SWITCHCOSTUMETO

    public val CHANGE_SIZE_BY: ScratchOpcodes = ScratchOpcodes.LOOKS_CHANGESIZEBY
    public val SET_SIZE_TO: ScratchOpcodes = ScratchOpcodes.LOOKS_SETSIZETO

    public val CHANGE_EFFECT_BY: ScratchOpcodes = ScratchOpcodes.LOOKS_CHANGEEFFECTBY
    public val SET_EFFECT_TO: ScratchOpcodes = ScratchOpcodes.LOOKS_SETEFFECTTO

    public val SHOW: ScratchOpcodes = ScratchOpcodes.LOOKS_SHOW
    public val HIDE: ScratchOpcodes = ScratchOpcodes.LOOKS_HIDE

    public val GO_TO_FRONT_BACK: ScratchOpcodes = ScratchOpcodes.LOOKS_GOTOFRONTBACK
    public val GO_FORWARD_BACKWARD_LAYERS: ScratchOpcodes = ScratchOpcodes.LOOKS_GOFORWARDBACKWARDLAYERS
    public val COSTUME_NUMBER_NAME: ScratchOpcodes = ScratchOpcodes.LOOKS_COSTUMENUMBERNAME
    public val SIZE: ScratchOpcodes = ScratchOpcodes.LOOKS_SIZE
}

public object SoundOpcodes {
    public val PLAY_UNTIL_DONE: ScratchOpcodes = ScratchOpcodes.SOUND_PLAYUNTILDONE
    public val PLAY: ScratchOpcodes = ScratchOpcodes.SOUND_PLAY

    public val SET_VOLUME_TO: ScratchOpcodes = ScratchOpcodes.SOUND_SETVOLUMETO
    public val VOLUME: ScratchOpcodes = ScratchOpcodes.SOUND_VOLUME
}

public object EventsOpcodes {
    public val WHEN_FLAG_CLICKED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENFLAGCLICKED
    public val WHEN_KEY_PRESSED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENKEYPRESSED
    public val WHEN_THIS_SPRITE_CLICKED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENTHISSPRITECLICKED

    public val WHEN_BROADCAST_RECEIVED: ScratchOpcodes = ScratchOpcodes.EVENT_WHENBROADCASTRECEIVED
    public val BROADCAST: ScratchOpcodes = ScratchOpcodes.EVENT_BROADCAST
    public val BROADCAST_AND_WAIT: ScratchOpcodes = ScratchOpcodes.EVENT_BROADCASTANDWAIT
}

public object ControlOpcodes {
    public val WAIT: ScratchOpcodes = ScratchOpcodes.CONTROL_WAIT

    public val REPEAT: ScratchOpcodes = ScratchOpcodes.CONTROL_REPEAT
    public val FOREVER: ScratchOpcodes = ScratchOpcodes.CONTROL_FOREVER

    public val IF: ScratchOpcodes = ScratchOpcodes.CONTROL_IF
    public val IF_ELSE: ScratchOpcodes = ScratchOpcodes.CONTROL_IF_ELSE
    public val WAIT_UNTIL: ScratchOpcodes = ScratchOpcodes.CONTROL_WAIT_UNTIL
    public val REPEAT_UNTIL: ScratchOpcodes = ScratchOpcodes.CONTROL_REPEAT_UNTIL

    public val STOP: ScratchOpcodes = ScratchOpcodes.CONTROL_STOP

    public val START_AS_CLONE: ScratchOpcodes = ScratchOpcodes.CONTROL_START_AS_CLONE
    public val CREATE_CLONE_OF: ScratchOpcodes = ScratchOpcodes.CONTROL_CREATE_CLONE_OF
}

public object SensingOpcodes {
    public val TOUCHING_OBJECT: ScratchOpcodes = ScratchOpcodes.SENSING_TOUCHINGOBJECT
    public val DISTANCE_TO: ScratchOpcodes = ScratchOpcodes.SENSING_DISTANCETO

    public val ASK_AND_WAIT: ScratchOpcodes = ScratchOpcodes.SENSING_ASKANDWAIT
    public val ANSWER: ScratchOpcodes = ScratchOpcodes.SENSING_ANSWER

    public val KEY_PRESSED: ScratchOpcodes = ScratchOpcodes.SENSING_KEYPRESSED
    public val MOUSE_DOWN: ScratchOpcodes = ScratchOpcodes.SENSING_MOUSEDOWN
    public val MOUSE_X: ScratchOpcodes = ScratchOpcodes.SENSING_MOUSEX
    public val MOUSE_Y: ScratchOpcodes = ScratchOpcodes.SENSING_MOUSEY

    public val TIMER: ScratchOpcodes = ScratchOpcodes.SENSING_TIMER

    public val OF: ScratchOpcodes = ScratchOpcodes.SENSING_OF
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
    public val ROUND: ScratchOpcodes = ScratchOpcodes.OPERATOR_ROUND

    public val MATH_OP: ScratchOpcodes = ScratchOpcodes.OPERATOR_MATHOP
}

public object VariablesOpcodes {
    public val SET_VARIABLE_TO: ScratchOpcodes = ScratchOpcodes.DATA_SETVARIABLETO
    public val CHANGE_VARIABLE_BY: ScratchOpcodes = ScratchOpcodes.DATA_CHANGEVARIABLEBY
    public val SHOW_VARIABLE: ScratchOpcodes = ScratchOpcodes.DATA_SHOWVARIABLE
    public val HIDE_VARIABLE: ScratchOpcodes = ScratchOpcodes.DATA_HIDEVARIABLE

    public val ADD_TO_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ADDTOLIST

    public val DELETE_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_DELETEOFLIST
    public val DELETE_ALL_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_DELETEALLOFLIST
    public val INSERT_AT_LIST: ScratchOpcodes = ScratchOpcodes.DATA_INSERTATLIST
    public val REPLACE_ITEM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_REPLACEITEMOFLIST

    public val ITEM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ITEMOFLIST
    public val ITEM_NUM_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_ITEMNUMOFLIST
    public val LENGTH_OF_LIST: ScratchOpcodes = ScratchOpcodes.DATA_LENGTHOFLIST
    public val LIST_CONTAINS_ITEM: ScratchOpcodes = ScratchOpcodes.DATA_LISTCONTAINSITEM
}

public object MyBlocksOpcodes {
    public val DEFINITION: ScratchOpcodes = ScratchOpcodes.PROCEDURES_DEFINITION
    public val PROTOTYPE: ScratchOpcodes = ScratchOpcodes.PROCEDURES_PROTOTYPE
    public val CALL: ScratchOpcodes = ScratchOpcodes.PROCEDURES_CALL

    public object ArgumentTypes {
        public val REPORTER_STRING_NUMBER: ScratchOpcodes = ScratchOpcodes.ARGUMENT_REPORTER_STRING_NUMBER
    }
}

public object PenOpcodes {
    public val CLEAR: ScratchOpcodes = ScratchOpcodes.PEN_CLEAR
    public val STAMP: ScratchOpcodes = ScratchOpcodes.PEN_STAMP
    public val PEN_DOWN: ScratchOpcodes = ScratchOpcodes.PEN_PEN_DOWN
    public val PEN_UP: ScratchOpcodes = ScratchOpcodes.PEN_PEN_UP
    public val SET_PEN_COLOR_TO_COLOR: ScratchOpcodes = ScratchOpcodes.PEN_SET_PEN_COLOR_TO_COLOR
    public val SET_PEN_COLOR_PARAM_TO: ScratchOpcodes = ScratchOpcodes.PEN_SET_PEN_COLOR_PARAM_TO
    public val SET_PEN_SIZE_TO: ScratchOpcodes = ScratchOpcodes.PEN_SET_PEN_SIZE_TO
}
