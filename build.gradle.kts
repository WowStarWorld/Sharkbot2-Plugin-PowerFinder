@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.nio.file.Path

buildscript {
    dependencies {
        classpath("xerces:xercesImpl:2.12.2")
    }
}

plugins {
    kotlin("jvm") version "1.9.22"
    id("org.springframework.boot") version "3.3.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "1.9.22"
    application
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenCentral()
    gradlePluginPortal()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

open class Shark {
    private val cacheDir: File = Path.of(buildDir.path, "cache").toFile().also { it.mkdirs() }
    fun cache(name: String, url: String): File = Path.of(cacheDir.path, name).toFile().also { it.parentFile.mkdirs() }.also { file -> if (!file.exists()) { file.createNewFile(); file.writeBytes(uri(url).toURL().readBytes()) } }
    fun sharkPom(file: File) {
        val pomDependencies = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file).documentElement.getElementsByTagName("dependency")
        for (i in 0 until pomDependencies.length) { val dependency = pomDependencies.item(i); val children = Array(dependency.childNodes.length, dependency.childNodes::item)
            runCatching {
                val first: (name: String) -> String? = { name -> children.firstOrNull { it.nodeName == name }?.textContent }
                val groupId = first("groupId")!!; val artifactId = first("artifactId")!!; val version = first("version")
                dependencies.api("$groupId:$artifactId" + if (version != null) ":$version" else "")
            }
        }
    }
}
val shark = Shark()
fun sharkDependencies(version: String): Unit = shark.sharkPom(shark.cache("shark-$version.pom", "https://jitpack.io/com/github/StarWorldTeam/SharkBot-2/$version/SharkBot-2-$version.pom"))

tasks.test {
    useJUnitPlatform()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = java.targetCompatibility.toString()
}

dependencies {
    testImplementation(kotlin("test"))
    sharkDependencies(properties["shark.version"].toString())
    implementation("com.github.StarWorldTeam:SharkBot-2:${properties["shark.version"]}")
}

tasks.withType<BootJar> {
    enabled = false
}

tasks.withType<ProcessResources> {
    val resourceTargets = listOf("META-INF/plugin.yml")
    val replaceProperties = mapOf(
        Pair(
            "gradle",
            mapOf(
                Pair("gradle", gradle),
                Pair("project", project)
            )
        )
    )
    filesMatching(resourceTargets) {
        expand(replaceProperties)
    }
}
