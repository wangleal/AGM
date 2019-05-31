cmake_minimum_required(VERSION 3.4.1)

find_library(log-lib log)
find_library(android-lib android)

add_library(ffmpeg
            SHARED
            src/main/jni/ffmpeg.cpp )

set(JNI_LIBS_DIR ${CMAKE_SOURCE_DIR}/src/main/jniLibs)

add_library(avutil
            SHARED
            IMPORTED )
set_target_properties(avutil
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libavutil.so )

add_library(swresample
            SHARED
            IMPORTED )
set_target_properties(swresample
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libswresample.so )

add_library(swscale
            SHARED
            IMPORTED )
set_target_properties(swscale
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libswscale.so )

add_library(avcodec
            SHARED
            IMPORTED )
set_target_properties(avcodec
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libavcodec.so )

add_library(avformat
            SHARED
            IMPORTED )
set_target_properties(avformat
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libavformat.so )

add_library(avfilter
            SHARED
            IMPORTED )
set_target_properties(avfilter
                      PROPERTIES IMPORTED_LOCATION
                      ${JNI_LIBS_DIR}/armeabi-v7a/libavfilter.so )

include_directories(${JNI_LIBS_DIR}/include)

target_link_libraries(ffmpeg
                      avutil swresample swscale avcodec avformat avfilter
                      ${log-lib} ${android-lib} )