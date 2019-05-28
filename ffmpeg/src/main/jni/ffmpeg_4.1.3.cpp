#include <jni.h>
#include <string>
#include <android/log.h>

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "ffmpeg_4.1.3", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "ffmpeg_4.1.3", __VA_ARGS__)

extern "C"{
#include "ffmpeg.h"
#include "libavcodec/jni.h"
}

extern "C"
/*
 * Class:     wang_leal_agm_ffmpeg_FFmpeg
 * Method:    execute
 * Signature: ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_wang_leal_agm_ffmpeg_FFmpeg_execute
        (JNIEnv *env, jclass type, jobjectArray commands){
    JavaVM *jvm = NULL;
    LOGE("argCmd=%s","step 1");
    env->GetJavaVM(&jvm);
    LOGE("argCmd=%s","step 2");
    av_jni_set_java_vm(jvm, NULL);
    LOGE("argCmd=%s","step 3");
    int cmdLen = env->GetArrayLength(commands);
    char *argCmd[cmdLen] ;
    jstring buf[cmdLen];
    LOGE("argCmd=%s","step 4");
    for (int i = 0; i < cmdLen; ++i) {
        buf[i] = static_cast<jstring>(env->GetObjectArrayElement(commands, i));
        char *string = const_cast<char *>(env->GetStringUTFChars(buf[i], JNI_FALSE));
        argCmd[i] = string;
        LOGE("argCmd=%s",argCmd[i]);
    }
    LOGE("argCmd=%s ,length:%d,cmd:%s","step 5",cmdLen,argCmd);
    int retCode = execute(cmdLen, argCmd);
    LOGE("ffmpeg-invoke: retCode=%d",retCode);
    LOGE("argCmd=%s","step 6");
    return retCode;
}