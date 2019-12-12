#include <jni.h>
#include <android/log.h>

/* Header for class wang_leal_agm_ffmpeg_FFmpeg */

extern "C" {
    #include "../jniLibs/include/libavformat/avformat.h"
    #include "../jniLibs/include/libavcodec/avcodec.h"
}

/*
 * Class:     wang_leal_agm_ffmpeg_FFmpeg
 * Method:    execute
 * Signature: ([Ljava/lang/String;)I
 */
extern "C" JNIEXPORT
jstring JNICALL Java_wang_leal_agm_ffmpeg_FFmpeg_execute
        (JNIEnv *env, jclass, jobjectArray commands){
    char info[40000] = { 0 };

    av_register_all();

    AVInputFormat *if_temp = av_iformat_next(NULL);
    AVOutputFormat *of_temp = av_oformat_next(NULL);
    //Input
    while(if_temp!=NULL){
        sprintf(info, "%s[In ][%10s]\n", info, if_temp->name);
        if_temp=if_temp->next;
    }

    //Output
    while (of_temp != NULL){
        sprintf(info, "%s[Out][%10s]\n", info, of_temp->name);
        of_temp = of_temp->next;
    }
    //LOGE("%s", info);
    return (env)->NewStringUTF(info);
}