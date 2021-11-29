// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("org.xtext.xtend") version "2.1.0" apply false
    id("com.github.node-gradle.node") version "3.1.1" apply false

    idea
    id("org.jetbrains.kotlinx.kover") version "0.4.4"
}

repositories {
    mavenCentral()
}

idea {
    module {
        excludeDirs.add(file("gradle"))
    }
}

kover {
    coverageEngine.set(kotlinx.kover.api.CoverageEngine.INTELLIJ)
    generateReportOnCheck.set(true)
}

// Variables -----------------------------------------------------------------------------------------------------------

val javaVersion by extra(11)
val xtextVersion by extra("2.26.0.M2")

// Subprojects ---------------------------------------------------------------------------------------------------------

subprojects {
    group = "de.unibonn.simpleml"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }

    configurations.all {
        exclude(group = "asm")
    }
}
