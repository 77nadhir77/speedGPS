#include <jni.h>
#include <string>
#include<opencv2/core.hpp>

extern "C" JNIEXPORT jstring

JNICALL
Java_com_example_kalmanfiltersensorfusion_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from Nadir";
    return env->NewStringUTF(hello.c_str());
}