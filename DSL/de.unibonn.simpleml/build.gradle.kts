val javaVersion: Int by rootProject.extra
val xtextVersion: String by rootProject.extra

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    `java-library`
    kotlin("jvm")
    idea
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

idea {
    module {
        excludeDirs.add(file("META-INF"))
    }
}

// Dependencies --------------------------------------------------------------------------------------------------------

val mwe2: Configuration by configurations.creating {
    extendsFrom(configurations.compileClasspath.get())
}

dependencies {
    api(platform("org.eclipse.xtext:xtext-dev-bom:$xtextVersion"))
    implementation("org.eclipse.xtext:org.eclipse.xtext:$xtextVersion")

    mwe2("org.eclipse.emf:org.eclipse.emf.mwe2.launch")
    mwe2("org.eclipse.xtext:org.eclipse.xtext.common.types:$xtextVersion")
    mwe2("org.eclipse.xtext:org.eclipse.xtext.xtext.generator:$xtextVersion")
    mwe2("org.eclipse.xtext:xtext-antlr-generator")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.testing:$xtextVersion")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.xbase.testing:$xtextVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.1")
}

// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    main {
        java.srcDirs("emf-gen", "src-gen")
        resources.srcDirs("src-gen")
        resources.include("**/*.simpleml", "**/*.tokens", "**/*.xtextbin")
    }
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks.register<JavaExec>("generateXtextLanguage") {
    group = "Build"
    description = "Generate language files (e.g. EMF classes)"

    classpath = mwe2
    mainClass.set("org.eclipse.emf.mwe2.launch.runtime.Mwe2Launcher")
    args = listOf("src/main/kotlin/de/unibonn/simpleml/GenerateSimpleML.mwe2", "-p", "rootPath=/$projectDir/..")

    inputs.files(
        "model/custom/SimpleML.ecore",
        "model/custom/SimpleML.genmodel",
        "src/main/kotlin/de/unibonn/simpleml/GenerateSimpleML.mwe2",
        "src/main/kotlin/de/unibonn/simpleml/SimpleML.xtext"
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

    doLast {
        delete(
            fileTree("src") {
                include("**/*.xtend")
            }
        )
        delete(
            fileTree("../de.unibonn.simpleml.ide/src") {
                include("**/*.xtend")
            }
        )
        delete(file("../de.unibonn.simpleml.tests"))
        delete(
            fileTree("../de.unibonn.simpleml.web/src/de/unibonn/simpleml/web") {
                include("**/*.xtend")
            }
        )
    }
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

    test {
        useJUnitPlatform()

        extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
            excludes = listOf(

                // Classes in emf-gen
                "de\\.unibonn\\.simpleml\\.simpleML\\..*",
            )
        }
    }

    koverVerify {
        rule {
            name = "Minimal line coverage rate in percents"
            bound {
                minValue = 67
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
