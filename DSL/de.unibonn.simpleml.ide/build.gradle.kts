val javaVersion: Int by rootProject.extra
val xtextVersion: String by rootProject.extra

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    java
    kotlin("jvm")
    application
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

application {
    mainClass.set("de.unibonn.simpleml.ide.ServerLauncher2")
}


// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    implementation(project(":de.unibonn.simpleml"))
    implementation("org.eclipse.xtext:org.eclipse.xtext.ide:${xtextVersion}")
}


// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    main {
        java.srcDirs("src", "src-gen")
        resources.srcDirs("src-gen")
        resources.include("**/*.ISetup")
    }
}


// Tasks ---------------------------------------------------------------------------------------------------------------

tasks {
    processResources {
        val generateXtextLanguage = project(":de.unibonn.simpleml").tasks.named("generateXtextLanguage")
        dependsOn(generateXtextLanguage)
    }
}
