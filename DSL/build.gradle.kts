// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    kotlin("jvm") version "1.5.31" apply false
    id("org.xtext.xtend") version "2.1.0" apply false
    id("com.github.node-gradle.node") version "3.1.0" apply false
    idea
}

idea {
    module {
        excludeDirs.add(file("gradle"))
    }
}

// Variables -----------------------------------------------------------------------------------------------------------

val javaSourceVersion by extra(JavaVersion.VERSION_11)
val javaTargetVersion by extra(JavaVersion.VERSION_11)
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

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = javaTargetVersion.majorVersion
        }
    }
}
