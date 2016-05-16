LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := ThreeDrawer
LOCAL_SRC_FILES := TeapotNativeActivity.cpp\
TeapotRenderer.cpp \

LOCAL_C_INCLUDES := C:\android-ndk-r10e\sources\cxx-stl\stlport
LOCAL_CFLAGS :=
LOCAL_LDLIBS    := -llog -landroid -lEGL -lGLESv2
LOCAL_STATIC_LIBRARIES := cpufeatures android_native_app_glue ndk_helper
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true

ifndef NDK_ROOT
include external/stlport/libstlport.mk
endif
LOCAL_SHARED_LIBRARIES += libstlport

include $(BUILD_SHARED_LIBRARY)
$(call import-module,android/ndk_helper)
$(call import-module,android/native_app_glue)
$(call import-module,android/cpufeatures)
