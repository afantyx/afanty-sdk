package com.afanty.video;

public interface MediaError {

    // MediaPlayer error
    String REASON_ERROR_IO = "error_io";
    String REASON_ERROR_NETWORK = "error_network";
    String REASON_ERROR_MALFORMED = "error_malformed";
    String REASON_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = "error_not_valid_for_progressive_playback";
    String REASON_ERROR_SERVER_DIED = "error_server_died";
    String REASON_ERROR_TIMED_OUT = "error_timed_out";
    String REASON_ERROR_UNSUPPORTED = "error_unsupported";
    String REASON_ERROR_UNKNOWN = "error_unknown";

    // Self define error
    String REASON_FILE_LENGTH_ZRAEO = "file_length_zero";
    String REASON_FILE_NOT_EXISTS = "file_not_exist";
    String REASON_FILE_LENGTH_ZERO = "file_length_zero";
    String REASON_FILE_PATH = "file_path_null";
    String REASON_INVALID_VIDEO_SIZE = "invalid_video_size";
    String REASON_SET_DATA_SOURCE_FAILED = "set_data_source_failed";
    String REASON_PREPARE_FAILED = "prepare_failed";

    String REASON_LOAD_ERROR = "load_media_error";
    String REASON_START_ERROR = "start_media_error";
    String ERROR_CODE_OPEN_FAILED = "error_open_failed";

}