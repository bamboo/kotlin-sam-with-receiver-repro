@file:JvmName("Main")

package compiler

import api.Action
import api.DefaultGradleProject
import api.GradleProject

import org.jetbrains.kotlin.script.KotlinScriptDefinitionFromAnnotatedTemplate

import org.slf4j.LoggerFactory

import java.io.File
import java.net.URLClassLoader

import kotlin.script.dependencies.Environment
import kotlin.script.dependencies.ScriptContents
import kotlin.script.experimental.dependencies.DependenciesResolver
import kotlin.script.experimental.dependencies.ScriptDependencies
import kotlin.script.extensions.SamWithReceiverAnnotations
import kotlin.script.templates.ScriptTemplateDefinition


@SamWithReceiverAnnotations("api.ParameterExtension")
@ScriptTemplateDefinition(resolver = Resolver::class, scriptFilePattern = """.+\.gradle\.kts""")
abstract class BuildScript(project: GradleProject) : GradleProject by project {

    fun kotlinCopySpec(spec: Action<GradleProject.CopySpec>) = copySpec(spec)
}


fun main(vararg args: String) {

    val buildscript = """
         println(copySpec { // works for API defined in Java
            from("src")
            into("build")
         })

         println(kotlinCopySpec { // does NOT work for API defined in Kotlin
            from("src")
            into("build")
         })
    """

    val outputDirectory = File("output/classes")
    val scriptFile = File("output/build.gradle.kts").apply {
        parentFile.mkdirs()
        writeText(buildscript)
    }

    val classLoader = BuildScript::class.java.classLoader
    val scriptClass = compileKotlinScriptToDirectory(
        outputDirectory,
        scriptFile,
        KotlinScriptDefinitionFromAnnotatedTemplate(BuildScript::class),
        classLoader,
        LoggerFactory.getLogger("main"))

    scriptClass.getConstructor(GradleProject::class.java).newInstance(DefaultGradleProject())
}


class Resolver : DependenciesResolver {

    override fun resolve(scriptContents: ScriptContents, environment: Environment): DependenciesResolver.ResolveResult =
        DependenciesResolver.ResolveResult.Success(
            ScriptDependencies(classpath = classPath, imports = listOf("api.*", "compiler.*")))

    private
    val classPath: List<File>
        get() = (javaClass.classLoader as URLClassLoader).urLs.map { File(it.toURI()) }.filter {
            it.isDirectory
                || it.name == "api-1.0.jar"
                || isKotlinJar(it.name)
        }

    private
    fun isKotlinJar(name: String): Boolean =
        name.startsWith("kotlin-stdlib-") || name.startsWith("kotlin-reflect-")
}


