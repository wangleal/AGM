#include <jni.h>
#include <android/log.h>

/* Header for class wang_leal_agm_ffmpeg_FFmpeg */
#define FFMPEG_TAG   "FFMPEG"
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, FFMPEG_TAG, format, ##__VA_ARGS__)

extern "C" {
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "ffmpeg.h"
#include <libavcodec/jni.h>
#include "FFmpegCMD.h"
}

static jclass ffmpegCMDClazz = NULL;//当前类(面向java)
static JNIEnv *jniEnv = NULL;

/*
 * Class:     wang_leal_agm_ffmpeg_FFmpeg
 * Method:    execute
 * Signature: ([Ljava/lang/String;)I
 */
extern "C" JNIEXPORT
jint JNICALL Java_wang_leal_agm_ffmpeg_FFmpegCMD_execute
        (JNIEnv *env, jclass clazz, jobjectArray commands) {
    if (jniEnv==NULL){
        jniEnv = env;
    }

    if (ffmpegCMDClazz==NULL){
        //获取调用此方法的java类，Android ICS之前(你可把NDK sdk版本改成低于11) 可以写m_clazz = clazz直接赋值,  然而ICS(sdk11) 后便改变了这一机制,在线程中回调java时 不能直接共用变量 必须使用NewGlobalRef创建全局对象
        ffmpegCMDClazz = static_cast<jclass>((env)->NewGlobalRef(clazz));
    }

    int argc = (env)->GetArrayLength(commands);
    char *argv[argc];
    int i;
    for (i = 0; i < argc; i++) {
        jstring js = (jstring) (env)->GetObjectArrayElement(commands, i);
        argv[i] = (char*) (env)->GetStringUTFChars(js, 0);
    }
    int result = cmd(argc, argv);
    for (i = 0; i < argc; i++) {
        free(argv[i]);
    }
//    free(argv);
    return result;
}

void updateProgress(float progress){
    if (ffmpegCMDClazz == NULL) {
        LOGE("---------------clazz isNULL---------------");
        return;
    }
    //获取方法ID (I)V指的是方法签名 通过javap -s -public FFMpegCMD 命令生成
    jmethodID methodID = (jniEnv)->GetStaticMethodID(ffmpegCMDClazz, "onProgress", "(F)V");
    if (methodID == NULL) {
        LOGE("---------------methodID isNULL---------------");
        return;
    }
    //调用该java方法
    (jniEnv)->CallStaticVoidMethod(ffmpegCMDClazz, methodID,progress);
}
