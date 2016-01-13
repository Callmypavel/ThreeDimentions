//
// Created by 请叫我保尔 on 2016/1/3.
//
#include "draw_util.h"
#include <jni.h>
#include <android/log.h>


JNIEXPORT jstring JNICALL Java_peacemaker_threedimentions_DrawUtil_drawTri(JNIEnv *env, jobject obj,jshortArray points){
//    //获得数组长度
//    GLint size = (GLint)(sizeof(points)/ sizeof(points[0]));
//    //GLshort vertexs[size];
//    GLint tri_number = size/3;
//    __android_log_print(ANDROID_LOG_INFO,"DrawUtil","haha");
//    glVertexPointer(size,GL_SHORT,0,points);
//    for(int i=0;i<tri_number;i++){
//        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
//        glLoadIdentity();
//        glDrawArrays(GL_TRIANGLES,0,size);
//    }
    LOGI("窝页到辣,蛤");
}
JNIEXPORT void JNICALL Java_peacemaker_threedimentions_DrawUtil_scroll(JNIEnv *env, jobject obj, jfloat x1, jfloat y1, jfloat x2, jfloat y2){
    LOGI("窝到辣,蛤蛤");
    LOGI("怒刷存在感,蛤蛤");

}

