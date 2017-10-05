plugins {
    application
    kotlin("jvm") version "1.1.51"
}

application {
    mainClassName = "compiler.Main"
}

dependencies {
    compile(project(":api"))
    compile("org.slf4j:slf4j-api:1.7.10")
    compile("org.slf4j:slf4j-simple:1.7.10")
    compile(kotlin("compiler-embeddable"))
    compile(kotlin("sam-with-receiver-compiler-plugin") as String) {
        isTransitive = false
    }
    compile(kotlin("stdlib"))
    compile(kotlin("reflect"))
}
