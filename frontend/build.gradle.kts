// Top-level build file where you can add configuration options common to all sub-projects/modules.
// 1) 플러그인 classpath 단계에서 resolution 적용
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    // 'classpath' 구성(configuration)에 대해 강제 버전 지정
    configurations.getByName("classpath").resolutionStrategy {
        force("com.squareup:javapoet:1.13.0")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

// 모든 서브모듈(특히 app 모듈)의 의존성 해석 시
// com.squareup:javapoet을 1.13.0으로 강제 지정
subprojects {
    configurations.all {
        resolutionStrategy {
            force("com.squareup:javapoet:1.13.0")
        }
    }
}