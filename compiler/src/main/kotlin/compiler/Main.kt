@file:JvmName("Main")

package compiler

import api.DefaultGradleProject
import api.GradleProject

import org.jetbrains.kotlin.com.intellij.openapi.project.Project
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import org.jetbrains.kotlin.script.KotlinScriptExternalDependencies

import org.slf4j.LoggerFactory

import java.io.File

import java.net.URLClassLoader

import kotlin.reflect.KClass


abstract class BuildScript(project: GradleProject) : GradleProject by project


fun main(vararg args: String) {

   val buildscript = """
         println(copySpec {
            from("src")
            into("build")
         })
    """

    val classLoader = BuildScript::class.java.classLoader
    val classPath = (classLoader as URLClassLoader).urLs.map { File(it.toURI()) }.filter {
        it.isDirectory
            || it.name == "api-1.0.jar"
            || isKotlinJar(it.name)
    }
    val outputDirectory = File("output/classes")
    val scriptFile = File("output/build.gradle.kts").apply {
        parentFile.mkdirs()
        writeText(buildscript)
    }

    val scriptClass = compileKotlinScriptToDirectory(
        outputDirectory,
        scriptFile,
        scriptDefinitionFromTemplate(BuildScript::class, classPath),
        classLoader,
        LoggerFactory.getLogger("main"))

    scriptClass.getConstructor(GradleProject::class.java).newInstance(DefaultGradleProject())
}


private
fun scriptDefinitionFromTemplate(template: KClass<out Any>, classPath: List<File>) =
    object : KotlinScriptDefinition(template) {

        override fun <TF : Any> getDependenciesFor(
            file: TF,
            project: Project,
            previousDependencies: KotlinScriptExternalDependencies?): KotlinScriptExternalDependencies? =

            object : KotlinScriptExternalDependencies {
                override val imports: Iterable<String>
                    get() = listOf("api.*", "compiler.*")
                override val classpath: Iterable<File>
                    get() = classPath
            }
    }


fun isKotlinJar(name: String): Boolean =
    name.startsWith("kotlin-stdlib-")
        || name.startsWith("kotlin-reflect-")
        || name.startsWith("kotlin-runtime-")


