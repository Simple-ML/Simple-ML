val javaVersion: Int by rootProject.extra
val xtextVersion: String by rootProject.extra

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    `java-library`
    `java-test-fixtures`
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

dependencies {
    api(platform("org.eclipse.xtext:xtext-dev-bom:$xtextVersion"))
    implementation("org.eclipse.xtext:org.eclipse.xtext:$xtextVersion")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.testing:$xtextVersion")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.xbase.testing:$xtextVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.0.2")

    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api")
    testFixturesImplementation("org.eclipse.xtext:org.eclipse.xtext.testing:$xtextVersion")
    testFixturesImplementation("org.eclipse.xtext:org.eclipse.xtext.xbase.testing:$xtextVersion")
    testFixturesImplementation("io.kotest:kotest-assertions-core-jvm:5.0.2")
}

// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    main {
        java.srcDirs("emf-gen", "src-gen")
        resources.srcDirs("src-gen")
        resources.include(
            "**/*.smlflow",
            "**/*.smlstub",
            "**/*.tokens",
            "**/*.xtextbin"
        )
    }
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks {
    compileJava {
        dependsOn(rootProject.tasks.named("generateXtextLanguage"))
    }

    compileKotlin {
        dependsOn(rootProject.tasks.named("generateXtextLanguage"))
    }

    processResources {
        dependsOn(rootProject.tasks.named("generateXtextLanguage"))
    }

    clean {
        dependsOn(rootProject.tasks.named("cleanGenerateXtextLanguage"))
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
                minValue = 75
            }
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}
