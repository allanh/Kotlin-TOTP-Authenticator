#include <string.h>
#include <jni.h>

JNIEXPORT jstring JNICALL
Java_com_udnshopping_udnsauthorizer_utility_ThreeDESUtil_getPassword( JNIEnv* env, jobject thiz )
{
    return (*env)->NewStringUTF(env, "lyAOvVCxkYvyTzSeEyRwkfzX");
}

JNIEXPORT jstring JNICALL
Java_com_udnshopping_udnsauthorizer_repository_QRCodeRepository_getTotpApi( JNIEnv* env, jobject thiz )
{
    return (*env)->NewStringUTF(env, "https://uat-shopping56.udn.com/spm/TOTPGenQrcodeMail.do?source=");
}