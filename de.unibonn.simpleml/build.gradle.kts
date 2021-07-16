val javaSourceVersion: JavaVersion by rootProject.extra
val javaTargetVersion: JavaVersion by rootProject.extra
val xtextVersion: String by rootProject.extra


// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    `java-library`
    kotlin("jvm")
}

java {
    sourceCompatibility = javaSourceVersion
    targetCompatibility = javaTargetVersion
}


// Dependencies --------------------------------------------------------------------------------------------------------

val mwe2: Configuration by configurations.creating {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    api(platform("org.eclipse.xtext:xtext-dev-bom:${xtextVersion}"))
    implementation("org.eclipse.xtext:org.eclipse.xtext:${xtextVersion}")

    mwe2("org.eclipse.emf:org.eclipse.emf.mwe2.launch")
    mwe2("org.eclipse.xtext:org.eclipse.xtext.common.types:${xtextVersion}")
    mwe2("org.eclipse.xtext:org.eclipse.xtext.xtext.generator:${xtextVersion}")
    mwe2("org.eclipse.xtext:xtext-antlr-generator")
}


// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    main {
        java.srcDirs("emf-gen", "src", "src-gen")
        resources.srcDirs("src-gen", "stdlib")
        resources.include("**/*.simpleml", "**/*.tokens", "**/*.xtextbin")
    }
}


// Tasks ---------------------------------------------------------------------------------------------------------------

tasks.register<JavaExec>("generateXtextLanguage") {
    group = "Build"
    description = "Generate language files (e.g. EMF classes)"

    classpath = mwe2
    mainClass.set("org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher")
    args = listOf("src/de/unibonn/simpleml/GenerateSimpleML.mwe2", "-p", "rootPath=/${projectDir}/..")

    inputs.files(
        "model/custom/SimpleML.genmodel",
        "src/de/unibonn/simpleml/GenerateSimpleML.mwe2",
        "src/de/unibonn/simpleml/SimpleML.xtext"
    )
    outputs.dirs(
        "META-INF",
        "src-gen",
        "emf-gen",
        "../de.unibonn.simpleml.ide/src-gen",
        "../de.unibonn.simpleml.tests/src-gen",
        "../de.unibonn.simpleml.web/src-gen"
    )
    outputs.files("build.properties", "plugin.properties", "plugin.xml")
}

tasks {
    compileJava {
        dependsOn("generateXtextLanguage")
    }

    compileKotlin {
        dependsOn("generateXtextLanguage")
    }

    processResources {
        dependsOn("generateXtextLanguage")
    }

    clean {
        dependsOn("cleanGenerateXtextLanguage")
    }
}


