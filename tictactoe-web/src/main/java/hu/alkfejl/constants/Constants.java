package hu.alkfejl.constants;

public final class Constants {
    // SESSION CONSTANTS
    public static final String SESSION_PLAYER_ID = "playerID";
    public static final String SESSION_PLAYER_NAME = "playerName";
    public static final String SESSION_GAME_STATE = "gameState";
    public static final String SESSION_GAME_RESULT = "gameResult";
    public static final String SESSION_OPPONENT_PLAYER_ID = "opponent";
    public static final String SESSION_OPPONENT_PLAYER_NAME = "opponent_name";
    public static final String SESSION_BOARD_SIZE = "boardsize";
    public static final String SESSION_PLAYER_SYMBOL = "symbol";
    public static final String SESSION_MOVE_LIST = "moves";

    // REQUEST ATTRIBUTE CONSTANTS
    public static final String ATTRIBUTE_PLAYER_LIST = "players";
    public static final String ATTRIBUTE_PLAYER_ID_LIST = "playerIDs";
    public static final String ATTRIBUTE_PLAYER_NAME_LIST = "playerNames";
    public static final String ATTRIBUTE_PLAYER_1_LIST = "p1s";
    public static final String ATTRIBUTE_PLAYER_2_LIST = "p2s";
    public static final String ATTRIBUTE_IS_EMPTY = "is_empty";
    public static final String ATTRIBUTE_PARTY_LIST = "parties";
    public static final String ATTRIBUTE_WINNER_LIST = "winners";

    // FORM INPUT NAME CONSTANTS
    public static final String FORM_INPUT_NAME_PLAYER_ID = "player_id";
    public static final String FORM_INPUT_NAME_PLAYER_NAME = "player_name";
    public static final String FORM_INPUT_NAME_IS_AI = "ai";
    public static final String FORM_INPUT_NAME_PARTY_ID = "party_id";
    public static final String FORM_INPUT_NAME_BOARD_SIZE = "boardsize";
    public static final String FORM_INPUT_NAME_TURN_TIMER = "turntimer";
    public static final String FORM_INPUT_NAME_GAME_TIMER = "gametimer";
    public static final String FORM_INPUT_NAME_OPPONENT = "opponent";
    public static final String FORM_INPUT_NAME_COORDINATES = "coords";

    // AJAX DATA PARAMETER CONSTANTS
    public static final String AJAX_DATA_STATE = "state";
    public static final String AJAX_DATA_SKIP_TURN = "skipTurn";
    public static final String AJAX_DATA_SAVE_GAME = "save";
    public static final String AJAX_SEND_MOVES = "sendMoves";


    private Constants() throws InstantiationException {
        throw new InstantiationException("Pls no :(");
    }
}
