//
// Created by 请叫我保尔 on 2016/1/3.
//
#include "DisplayNativeActivity.h"
#include "teapot.inl"
#include <android/log.h>


//JNIEXPORT jstring JNICALL Java_peacemaker_threedimentions_DrawUtil_drawTri(JNIEnv *env, jobject obj,jshortArray points){
////    //获得数组长度
////    GLint size = (GLint)(sizeof(points)/ sizeof(points[0]));
////    //GLshort vertexs[size];
////    GLint tri_number = size/3;
////    __android_log_print(ANDROID_LOG_INFO,"DrawUtil","haha");
////    glVertexPointer(size,GL_SHORT,0,points);
////    for(int i=0;i<tri_number;i++){
////        glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);
////        glLoadIdentity();
////        glDrawArrays(GL_TRIANGLES,0,size);
////    }
//
//    LOGI("窝页到辣,蛤");
//}
void TransformPosition( ndk_helper::Vec2& vec )
{
    vec = ndk_helper::Vec2( 2.0f, 2.0f ) * vec
          / ndk_helper::Vec2( gl_context_->GetScreenWidth(), gl_context_->GetScreenHeight() )
          - ndk_helper::Vec2( 1.f, 1.f );
}

void updatea(float fTime){
    const float CAMERA_X = 0.f;
    const float CAMERA_Y = 0.f;
    const float CAMERA_Z = 700.f;
    mat_view_ = ndk_helper::Mat4::LookAt(ndk_helper::Vec3(CAMERA_X,CAMERA_Y,CAMERA_Z),
                                         ndk_helper::Vec3(0.f,0.f,0.f),ndk_helper::Vec3(0.f,1.f,0.f));
    if(camera_){
        camera_->Update();
        mat_view_ = camera_->GetTransformMatrix()*mat_view_*camera_->GetRotationMatrix()*mat_model_;
    }else{
        mat_view_ = mat_view_*mat_model_;
    }
}
void UpdateViewport(){
    LOGI("升级viewport");
    int32_t viewport[4];
    glGetIntegerv(GL_VIEWPORT,viewport);
    //获得宽高比
    float aspecetRatio = (float)viewport[2]/(float)viewport[3];
    const float CAMERA_NEAR = 5.f;
    const float CAMERA_FAR = 10000.f;
    mat_projection_ =  ndk_helper::Mat4::Perspective(aspecetRatio,1.f,CAMERA_NEAR,CAMERA_FAR);
}
void init(){
    //初始化gl
    glEnable( GL_CULL_FACE );
    glEnable( GL_DEPTH_TEST );
    glDepthFunc( GL_LEQUAL );
    LOGI("gl初始化完毕");
    glViewport( 0, 0, gl_context_->GetScreenWidth(), gl_context_->GetScreenHeight() );
    UpdateViewport();
    tap_camera_.SetFlip( 1.f, -1.f, -1.f );
    tap_camera_.SetPinchTransformFactor( 2.f, 2.f, 8.f );
    //GL_CCW 表示窗口坐标上投影多边形的顶点顺序为逆时针方向的表面为正面。
    glFrontFace( GL_CCW );
    //加载shader,暂时跳过
    LoadShaders(&shader_param_, "Shaders/VS_ShaderPlain.vsh",
               "Shaders/ShaderPlain.fsh" );
    num_indices = sizeof(teapotIndices)/ sizeof(teapotIndices[0]);
    num_verticles = sizeof(teapotPositions) / sizeof(teapotPositions[0]) / 3;
    LOGI("shader创建完毕");
    //创建ibo
    //生成缓冲数列，头指针交给ibo_
    glGenBuffers(1,&ibo_);
    //绑定缓冲数列
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ibo_);
    //初始化缓冲数列数据
    glBufferData(GL_ELEMENT_ARRAY_BUFFER, sizeof(teapotIndices), teapotIndices, GL_STATIC_DRAW);
    //暂时禁用缓冲区数据
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
    //创建vbo
    glGenBuffers(1,&vbo_);
    glBindBuffer(GL_ARRAY_BUFFER, vbo_);
    int32_t iStride = sizeof(TEAPOT_VERTEX);
    int32_t iIndex = 0;
    TEAPOT_VERTEX* p = new TEAPOT_VERTEX[num_verticles];
    for( int32_t i = 0; i < num_verticles; ++i )
    {
        p[i].pos[0] = teapotPositions[iIndex];
        p[i].pos[1] = teapotPositions[iIndex + 1];
        p[i].pos[2] = teapotPositions[iIndex + 2];

        p[i].normal[0] = teapotNormals[iIndex];
        p[i].normal[1] = teapotNormals[iIndex + 1];
        p[i].normal[2] = teapotNormals[iIndex + 2];
        iIndex += 3;
    }
    //创建顶点缓冲区对象
    glGenBuffers( 1, &vbo_ );
    //激活/绑定顶点缓冲区对象
    glBindBuffer( GL_ARRAY_BUFFER, vbo_ );
    //初始化缓冲区数据
    glBufferData( GL_ARRAY_BUFFER, iStride * num_vertices_, p, GL_STATIC_DRAW );
    glBindBuffer( GL_ARRAY_BUFFER, 0 );

    delete[] p;
    LOGI("顶点激活完毕");
    UpdateViewport();
    mat_model_ = ndk_helper::Mat4::Translation(0,0,-15.f);
    ndk_helper::Mat4 mat = ndk_helper::Mat4::RotationX( M_PI / 3 );
    mat_model_ = mat * mat_model_;
}
//JNIEXPORT void JNICALL Java_peacemaker_threedimentions_DrawUtil_scroll(JNIEnv *env, jobject obj, jfloat x1, jfloat y1, jfloat x2, jfloat y2){
////    LOGI("窝到辣,蛤蛤");
////    LOGI("怒刷存在感,蛤蛤");
//
////    ndk_helper::Vec2 v1 = transformPosition(x1,y1);
////    ndk_helper::Vec2 v2 = transformPosition(x2,y2);
////    camera_->BeginPinch(v1,v2);
//
//}

bool LoadShaders(SHADER_PARAMS* params,const char* strVsh,const char *strFsh){
    GLuint program;
    GLuint vert_shader,frag_shader;
    program = glCreateProgram();

    //编译两个shader
    if(!ndk_helper::shader::CompileShader(&vert_shader,GL_VERTEX_SHADER,strVsh)){
        LOGI("顶点Shader编译失败");
        glDeleteProgram(program);
        return false;
    }
    if(!ndk_helper::shader::CompileShader(&frag_shader,GL_FRAGMENT_SHADER,strFsh)){
        LOGI("碎片Shader编译失败");
        glDeleteProgram(program);
        return false;
    }
    //绑定program与shader
    glAttachShader(program,vert_shader);
    glAttachShader(program,frag_shader);
    //绑定Attribe位置
    //必须在linking之前完成
    glBindAttribLocation(program,ATTRIB_NORMAL,"myNormal");
    glBindAttribLocation(program,ATTRIB_VERTEX,"myVertex");
    glBindAttribLocation(program,ATTRIB_UV,"myUV");
    //link program
    if( !ndk_helper::shader::LinkProgram( program ) )
    {
        LOGI( "Failed to link program: %d", program );
        //如果link失败 将shader删除
        if( vert_shader )
        {
            glDeleteShader( vert_shader );
            vert_shader = 0;
        }
        if( frag_shader )
        {
            glDeleteShader( frag_shader );
            frag_shader = 0;
        }
        if( program )
        {
            glDeleteProgram( program );
        }

        return false;
    }
    // 获得uniform的位置
    params->matrix_projection_ = glGetUniformLocation( program, "uPMatrix" );
    params->matrix_view_ = glGetUniformLocation( program, "uMVMatrix" );
    params->light0_ = glGetUniformLocation( program, "vLight0" );
    params->material_diffuse_ = glGetUniformLocation( program, "vMaterialDiffuse" );
    params->material_ambient_ = glGetUniformLocation( program, "vMaterialAmbient" );
    params->material_specular_ = glGetUniformLocation( program, "vMaterialSpecular" );
    //释放shader所占内存
    if( vert_shader )
        glDeleteShader( vert_shader );
    if( frag_shader )
        glDeleteShader( frag_shader );
    params->program_ = program;
    return true;


}

//JNIEXPORT void JNICALL Java_peacemaker_threedimentions_DrawUtil_init(JNIEnv *env, jobject obj){
//    init();
//}

void render(){
    ndk_helper::Mat4 mat_vp = mat_projection_*mat_view_;
    glBindBuffer(GL_ARRAY_BUFFER,vbo_);
    int32_t iStride = sizeof(TEAPOT_VERTEX);
    glVertexAttribPointer( ATTRIB_VERTEX, 3, GL_FLOAT, GL_FALSE, iStride,
                           BUFFER_OFFSET( 0 ) );
    glEnableVertexAttribArray( ATTRIB_VERTEX );

    glVertexAttribPointer( ATTRIB_NORMAL, 3, GL_FLOAT, GL_FALSE, iStride,
                           BUFFER_OFFSET( 3 * sizeof(GLfloat) ) );
    glEnableVertexAttribArray( ATTRIB_NORMAL );
    //绑定索引缓冲区
    glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, ibo_ );

    glUseProgram( shader_param_.program_ );

    TEAPOT_MATERIALS material = { { 1.0f, 0.5f, 0.5f }, { 1.0f, 1.0f, 1.0f, 10.f }, {
                                    0.1f, 0.1f, 0.1f }, };
    //升级uniforms
    glUniform4f( shader_param_.material_diffuse_, material.diffuse_color[0],
                 material.diffuse_color[1], material.diffuse_color[2], 1.f );

    glUniform4f( shader_param_.material_specular_, material.specular_color[0],
                 material.specular_color[1], material.specular_color[2],
                 material.specular_color[3] );
    glUniform3f( shader_param_.material_ambient_, material.ambient_color[0],
                 material.ambient_color[1], material.ambient_color[2] );

    glUniformMatrix4fv( shader_param_.matrix_projection_, 1, GL_FALSE, mat_vp.Ptr() );
    glUniformMatrix4fv( shader_param_.matrix_view_, 1, GL_FALSE, mat_view_.Ptr() );
    glUniform3f( shader_param_.light0_, 100.f, -200.f, -600.f );

    glDrawElements( GL_TRIANGLES, num_indices_, GL_UNSIGNED_SHORT, BUFFER_OFFSET(0) );

    glBindBuffer( GL_ARRAY_BUFFER, 0 );
    glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, 0 );


}
void SetState(android_app* state){
    app_ = state;
    doubletap_detector_.SetConfiguration(app_->config);
    drag_detector_.SetConfiguration(app_->config);
    pinch_detector_.SetConfiguration(app_->config);

}
void InitSensors()
{
    //初始化传感器 (用于处理event)
    sensor_manager_ = ASensorManager_getInstance();
    accelerometer_sensor_ = ASensorManager_getDefaultSensor( sensor_manager_,
                                                             ASENSOR_TYPE_ACCELEROMETER );
    sensor_event_queue_ = ASensorManager_createEventQueue( sensor_manager_, app_->looper,
                                                           LOOPER_ID_USER, NULL, NULL );
}
void ProcessSensors( int32_t id )
{
    //如果传感器有数据，现在就处理
    // If a sensor has data, process it now.
    if( id == LOOPER_ID_USER )
    {
        if( accelerometer_sensor_ != NULL )
        {
            ASensorEvent event;
            while( ASensorEventQueue_getEvents( sensor_event_queue_, &event, 1 ) > 0 )
            {
            }
        }
    }
}
void SuspendSensors()
{
    //当app失去焦点，不再监视加速度计
    //避免不用时浪费电量
    // When our app loses focus, we stop monitoring the accelerometer.
    // This is to avoid consuming battery while not being used.
    if( accelerometer_sensor_ != NULL )
    {
        ASensorEventQueue_disableSensor( sensor_event_queue_, accelerometer_sensor_ );
    }
}
void ResumeSensors()
{
    //当app获得焦点，我们开始监视加速度计
    // When our app gains focus, we start monitoring the accelerometer.
    if( accelerometer_sensor_ != NULL )
    {
        ASensorEventQueue_enableSensor( sensor_event_queue_, accelerometer_sensor_ );
        //每秒接受60次事件
        // We'd like to get 60 events per second (in us).
        ASensorEventQueue_setEventRate( sensor_event_queue_, accelerometer_sensor_,
                                        (1000L / 60) * 1000 );
    }
}
/**
 * Load resources
 */
void LoadResources()
{
    init();
}
void Unload()
{
    //卸载模型
    if( vbo_ )
    {
        glDeleteBuffers( 1, &vbo_ );
        vbo_ = 0;
    }

    if( ibo_ )
    {
        glDeleteBuffers( 1, &ibo_ );
        ibo_ = 0;
    }

    if( shader_param_.program_ )
    {
        glDeleteProgram( shader_param_.program_ );
        shader_param_.program_ = 0;
    }
}
/**
 * Unload resources
 */
void UnloadResources()
{
    Unload();
}
void ShowUI()
{
    JNIEnv *jni;
    app_->activity->vm->AttachCurrentThread( &jni, NULL );

    //Default class retrieval
    //通过jni指针调用java中的showUI方法
    jclass clazz = jni->GetObjectClass( app_->activity->clazz );
    jmethodID methodID = jni->GetMethodID( clazz, "showUI", "()V" );
    jni->CallVoidMethod( app_->activity->clazz, methodID );

    app_->activity->vm->DetachCurrentThread();
    return;
}
int InitDisplay()
{
    if( !initialized_resources_ )
    {
        gl_context_->Init( app_->window );
        LoadResources();
        initialized_resources_ = true;
    }
    else
    {
        // initialize OpenGL ES and EGL
        if( EGL_SUCCESS != gl_context_->Resume( app_->window ) )
        {
            UnloadResources();
            LoadResources();
        }
    }
    ShowUI();

    // Initialize GL state.
    glEnable( GL_CULL_FACE );
    glEnable( GL_DEPTH_TEST );
    glDepthFunc( GL_LEQUAL );

    //Note that screen size might have been changed
    glViewport( 0, 0, gl_context_->GetScreenWidth(), gl_context_->GetScreenHeight() );
    UpdateViewport();

    tap_camera_.SetFlip( 1.f, -1.f, -1.f );
    tap_camera_.SetPinchTransformFactor( 2.f, 2.f, 8.f );

    return 0;
}
void TermDisplay()
{
    //中断gl显示
    gl_context_->Suspend();

}

void TrimMemory()
{
    //终结gl占用内存
    LOGI( "Trimming memory" );
    gl_context_->Invalidate();
}
void UpdateFPS( float fFPS )
{
    JNIEnv *jni;
    app_->activity->vm->AttachCurrentThread( &jni, NULL );

    //Default class retrieval
    jclass clazz = jni->GetObjectClass( app_->activity->clazz );
    jmethodID methodID = jni->GetMethodID( clazz, "updateFPS", "(F)V" );
    jni->CallVoidMethod( app_->activity->clazz, methodID, fFPS );

    app_->activity->vm->DetachCurrentThread();
    return;
}
void DrawFrame()
{
    float fFPS;
    if( monitor_.Update( fFPS ) )
    {
        UpdateFPS( fFPS );
    }
    updatea( monitor_.GetCurrentTime() );

    //用一种颜色填满屏幕
    // Just fill the screen with a color.
    glClearColor( 0.5f, 0.5f, 0.5f, 1.f );
    glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
    render();

    // Swap
    if( EGL_SUCCESS != gl_context_->Swap() )
    {
        UnloadResources();
        LoadResources();
    }
}
bool IsReady()
{
    if( has_focus_ )
        return true;

    return false;
}
/**
 * Process the next input event.
 */
int32_t HandleInput( android_app* app,
                             AInputEvent* event )
{
    if( AInputEvent_getType( event ) == AINPUT_EVENT_TYPE_MOTION )
    {
        ndk_helper::GESTURE_STATE doubleTapState = doubletap_detector_.Detect( event );
        ndk_helper::GESTURE_STATE dragState = drag_detector_.Detect( event );
        ndk_helper::GESTURE_STATE pinchState = pinch_detector_.Detect( event );

        //Double tap detector has a priority over other detectors
        if( doubleTapState == ndk_helper::GESTURE_STATE_ACTION )
        {
            LOGI("双击");
            //Detect double tap
            tap_camera_.Reset( true );
        }
        else
        {
            //Handle drag state
            if( dragState & ndk_helper::GESTURE_STATE_START )
            {
                LOGI("开始拖动");
                //Otherwise, start dragging
                ndk_helper::Vec2 v;

                drag_detector_.GetPointer( v );
                TransformPosition( v );
                tap_camera_.BeginDrag( v );
            }
            else if( dragState & ndk_helper::GESTURE_STATE_MOVE )
            {
                LOGI("拖动中");
                ndk_helper::Vec2 v;
                drag_detector_.GetPointer( v );
                TransformPosition( v );
                tap_camera_.Drag( v );
            }
            else if( dragState & ndk_helper::GESTURE_STATE_END )
            {
                LOGI("拖动结束");
                tap_camera_.EndDrag();
            }

            //Handle pinch state
            if( pinchState & ndk_helper::GESTURE_STATE_START )
            {
                //捏合手势
                //Start new pinch
                ndk_helper::Vec2 v1;
                ndk_helper::Vec2 v2;
                pinch_detector_.GetPointers( v1, v2 );
                LOGI("开始捏合");
                //__android_log_vprint("gesture");
                TransformPosition( v1 );
                TransformPosition( v2 );
//                LOGI( v1->x_+"v1x" );
//                LOGI( v1->y_+"v1y" );
//                LOGI( v2->x_+"v2x" );
//                LOGI( v2->y_+"v2y" );
                tap_camera_.BeginPinch( v1, v2 );
            }
            else if( pinchState & ndk_helper::GESTURE_STATE_MOVE )
            {
                //多点触控
                //Multi touch
                //Start new pinch
                LOGI("捏合中");
                ndk_helper::Vec2 v1;
                ndk_helper::Vec2 v2;
                pinch_detector_.GetPointers( v1, v2 );
                TransformPosition( v1 );
                TransformPosition( v2 );
                tap_camera_.Pinch( v1, v2 );
            }
        }
        return 1;
    }
    return 0;
}

/**
 * Process the next main command.
 */
void HandleCmd( struct android_app* app,
                        int32_t cmd ) {
    switch( cmd )
    {
        case APP_CMD_SAVE_STATE:
            break;
        case APP_CMD_INIT_WINDOW:
            //显式窗口
            // The window is being shown, get it ready.
            if( app->window != NULL )
            {
                InitDisplay();
                DrawFrame();
            }
            break;
        case APP_CMD_TERM_WINDOW:
            //窗口被隐藏或被关闭
            // The window is being hidden or closed, clean it up.
            TermDisplay();
            has_focus_ = false;
            break;
        case APP_CMD_STOP:
            break;
        case APP_CMD_GAINED_FOCUS:
            //获得焦点
            ResumeSensors();
            //Start animation
            has_focus_ = true;
            break;
        case APP_CMD_LOST_FOCUS:
            //失去焦点
            SuspendSensors();
            // Also stop animating.
            has_focus_ = false;
            DrawFrame();
            break;
        case APP_CMD_LOW_MEMORY:
            //内存不足,释放资源
            //Free up GL resources
            TrimMemory();
            break;
    }
}
void android_main( android_app* state )
{
    //至少需要调用一次，保证glue不被优化
    app_dummy();

    SetState( state );

    //初始化helper功能
    //Init helper functions
    ndk_helper::JNIHelper::Init( state->activity, HELPER_CLASS_NAME );

    //state->userData = &g_engine;
    state->onAppCmd = HandleCmd;
    state->onInputEvent = HandleInput;

#ifdef USE_NDK_PROFILER
    monstartup("libThreeDrawer.so");
#endif

    //准备监视加速度表
    // Prepare to monitor accelerometer
    InitSensors();

    // loop waiting for stuff to do.
    while( 1 )
    {
        // Read all pending events.
        int id;
        int events;
        android_poll_source* source;

        //如果不处于动画状态，我们会堵住这个线程等event
        //如果处于动画状态，我们一直循环直到全event被读后，然后跳出
        //画出动画下一帧
        // If not animating, we will block forever waiting for events.
        // If animating, we loop until all events are read, then continue
        // to draw the next frame of animation.
        while( (id = ALooper_pollAll( IsReady() ? 0 : -1, NULL, &events, (void**) &source ))
               >= 0 )
        {
            // Process this event.
            //处理event
            if( source != NULL )
                source->process( state, source );
            ProcessSensors( id );
            //检查是否在退出
            // Check if we are exiting.
            if( state->destroyRequested != 0 )
            {
                //如果正在退出
                TermDisplay();
                return;
            }
        }

        if( IsReady() )
        {
            //绘图根据屏幕更新率调节 故无需定会器
            // Drawing is throttled to the screen update rate, so there
            // is no need to do timing here.
            DrawFrame();
        }
    }
}




