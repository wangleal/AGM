#include <jni.h>

#ifndef _Included_FFmpeg_Cmd
#define _Included_FFmpeg_Cmd
#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint JNICALL Java_wang_leal_agm_ffmpeg_FFmpegCMD_execute(JNIEnv *env, jclass, jobjectArray commands);

#ifdef __cplusplus
}
#endif
#endif

void updateProgress(float progress);