buildscript {
    repositories {
        gradleScriptKotlin()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin"))
    }
}

plugins {
    application
}

apply {
    plugin("kotlin")
}

configure<ApplicationPluginConvention> {
    mainClassName = "compiler.Main"
}

dependencies {
    compile(project(":api"))
    compile(kotlinModule("compiler-embeddable"))
    compile(kotlinModule("stdlib"))
    compile(kotlinModule("reflect"))
    compile("org.slf4j:slf4j-api:1.7.10")
    compile("org.slf4j:slf4j-simple:1.7.10")
}
