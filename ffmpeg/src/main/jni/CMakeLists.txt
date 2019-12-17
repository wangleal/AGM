cmake_minimum_required(VERSION 3.4.1)

find_library(log-lib log)
find_library(android-lib android)

set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})
set(JNI_FFMPEG_FILES_DIR ${CMAKE_SOURCE_DIR}/ffmpeg)

add_library(ffmpeg
        SHARED
        ${JNI_FFMPEG_FILES_DIR}/FFmpegCMD.cpp
        ${JNI_FFMPEG_FILES_DIR}/ffmpeg.c
        ${JNI_FFMPEG_FILES_DIR}/cmdutils.c
        ${JNI_FFMPEG_FILES_DIR}/ffmpeg_filter.c
        ${JNI_FFMPEG_FILES_DIR}/ffmpeg_hw.c
        ${JNI_FFMPEG_FILES_DIR}/ffmpeg_opt.c)

add_library(avutil
        SHARED
        IMPORTED)
set_target_properties(avutil
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libavutil.so)

add_library(swresample
        SHARED
        IMPORTED)
set_target_properties(swresample
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libswresample.so)

add_library(swscale
        SHARED
        IMPORTED)
set_target_properties(swscale
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libswscale.so)

add_library(avcodec
        SHARED
        IMPORTED)
set_target_properties(avcodec
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libavcodec.so)

add_library(avformat
        SHARED
        IMPORTED)
set_target_properties(avformat
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libavformat.so)

add_library(avfilter
        SHARED
        IMPORTED)
set_target_properties(avfilter
        PROPERTIES IMPORTED_LOCATION
        ${JNI_LIBS_DIR}/libavfilter.so)

include_directories(${JNI_FFMPEG_FILES_DIR})

target_link_libraries(ffmpeg
        avutil swresample swscale avcodec avformat avfilter
        ${log-lib} ${android-lib})