/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
#include <errno.h>
#include <vector>
#include <EGL/egl.h>
#include <GLES/gl.h>
#include <android/sensor.h>
#include <android/log.h>
#include <android_native_app_glue.h>
#include <android/native_window_jni.h>
#include <cpu-features.h>

#include "NDKHelper.h"
#include "android/log.h"
/* Header for class peacemaker_threedimentions_DrawUtil */
#define CLASS_NAME "android/app/NativeActivity"
//#define APPLICATION_CLASS_NAME "peacemaker/ndkdemo/teapot/TeapotApplication"

#ifndef _Included_peacemaker_threedimentions_DisplayNativeActivity
#define _Included_peacemaker_threedimentions_DisplayNativeActivity
#define HELPER_CLASS_NAME "peacemaker/threedimentions/NDKHelper"
#define BUFFER_OFFSET(i) ((char *)NULL + (i))
#ifdef __cplusplus
extern "C" {
#endif
int32_t num_indices_;
int32_t num_vertices_;
GLuint ibo_;
GLuint vbo_;
struct SHADER_PARAMS
{
    GLuint program_;
    GLuint light0_;
    GLuint material_diffuse_;
    GLuint material_ambient_;
    GLuint material_specular_;

    GLuint matrix_projection_;
    GLuint matrix_view_;
};
SHADER_PARAMS shader_param_;
ndk_helper::DoubletapDetector doubletap_detector_;
ndk_helper::PinchDetector pinch_detector_;
ndk_helper::DragDetector drag_detector_;
ndk_helper::PerfMonitor monitor_;

ndk_helper::TapCamera tap_camera_;

android_app* app_;

ASensorManager* sensor_manager_;
const ASensor* accelerometer_sensor_;
ASensorEventQueue* sensor_event_queue_;
ndk_helper::GLContext* gl_context_;

bool initialized_resources_;
bool has_focus_;
enum SHADER_ATTRIBUTES
{
  ATTRIB_VERTEX, ATTRIB_NORMAL, ATTRIB_UV,
};
struct TEAPOT_MATERIALS
{
  float diffuse_color[3];
  float specular_color[4];
  float ambient_color[3];
};
bool LoadShaders( SHADER_PARAMS* params, const char* strVsh, const char* strFsh );

//操作
ndk_helper::Mat4 mat_projection_;
//视图
ndk_helper::Mat4 mat_view_;
//模型
ndk_helper::Mat4 mat_model_;
//ndk_helper::GLContext* gl_context_;

ndk_helper::TapCamera* camera_;
void Init();
void Render();
void Update( float dTime );
bool Bind( ndk_helper::TapCamera* camera );
void Unload();
void UpdateViewport();
int32_t num_indices;
int32_t num_verticles;
struct TEAPOT_VERTEX
{
  float pos[3];
  float normal[3];
};


/*
 * Class:     peacemaker_threedimentions_DrawUtil
 * Method:    drawTri
 * Signature: ([I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_peacemaker_threedimentions_DrawUtil_drawTri(JNIEnv *, jobject, jshortArray);
/*
 * Class:     peacemaker_threedimentions_DrawUtil
 * Method:    scroll
 * Signature: ([I)Ljava/void;
 */
JNIEXPORT void JNICALL Java_peacemaker_threedimentions_DrawUtil_scroll
        (JNIEnv *, jobject, jfloat, jfloat, jfloat, jfloat);
/*
 * Class:     peacemaker_threedimentions_DrawUtil
 * Method:    init
 * Signature: ([I)Ljava/void;
 */
JNIEXPORT void JNICALL Java_peacemaker_threedimentions_DrawUtil_init
        (JNIEnv *, jobject);
#ifdef __cplusplus
}
#endif
#endif
