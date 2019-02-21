#include <jni.h>

extern "C" {

    JNIEXPORT jstring JNICALL
    Java_com_udnshopping_udnsauthorizer_utility_ThreeDESUtil_getPassword(JNIEnv *env, jobject instance) {

        return env-> NewStringUTF("lyAOvVCxkYvyTzSeEyRwkfzX");
    }

}

