#include <jni.h>
#include <android/log.h>
#include "ffmpeg.h"
#include "libavcodec/jni.h"

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, "ffmpeg_4.1.3", __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "ffmpeg_4.1.3", __VA_ARGS__)

/*
 * Class:     wang_leal_agm_ffmpeg_FFmpeg
 * Method:    execute
 * Signature: ([Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_wang_leal_agm_ffmpeg_FFmpeg_execute
        (JNIEnv *env, jclass type, jobjectArray commands){
//    JavaVM *jvm = NULL;
//    LOGE("argCmd=%s","step 1");
//    env->GetJavaVM(&jvm);
//    LOGE("argCmd=%s","step 2");
//    av_jni_set_java_vm(jvm, NULL);
//    LOGE("argCmd=%s","step 3");
//    int cmdLen = env->GetArrayLength(commands);
//    char *argCmd[cmdLen] ;
//    jstring buf[cmdLen];
//    LOGE("argCmd=%s,length:%d","step 4",cmdLen);
//    for (int i = 0; i < cmdLen; ++i) {
//        buf[i] = static_cast<jstring>(env->GetObjectArrayElement(commands, i));
//        char *string = const_cast<char *>(env->GetStringUTFChars(buf[i], JNI_FALSE));
//        argCmd[i] = string;
//        LOGE("argCmd=%s",argCmd[i]);
//    }
//    LOGE("argCmd=%s","step 5");
//    int retCode = execute(cmdLen, argCmd);
//    LOGE("ffmpeg-invoke: retCode=%d",retCode);
//    LOGE("argCmd=%s","step 6");
//    return retCode;
    int argc = (*env)->GetArrayLength(env,commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (*env)->GetObjectArrayElement(env, commands, i);
        argv[i] = (char*) (*env)->GetStringUTFChars(env, js, 0);
        LOGE("命令行argCmd=%s",argv[i]);
    }
    return execute(argc, argv);
}