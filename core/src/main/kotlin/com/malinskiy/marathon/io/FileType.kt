package com.malinskiy.marathon.io


enum class FileType(val dir: String, val suffix: String) {
    TEST("tests_raw", "xml"),
    TEST_FINAL("tests", "xml"),
    TEST_RESULT("test_result", "json"),
    LOG("logs", "log"),
    FULL_LOG("full_logs", "log"),
    DEVICE_INFO("devices", "json"),
    VIDEO("video", "mp4"),
    SCREENSHOT("screenshot", "gif")

}
