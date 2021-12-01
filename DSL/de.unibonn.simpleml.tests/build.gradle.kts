val javaVersion: Int by rootProject.extra
val xtextVersion: String by rootProject.extra

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    java
    kotlin("jvm")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(javaVersion))
    }
}

// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    implementation(project(":de.unibonn.simpleml"))

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.testing:$xtextVersion")
    testImplementation("org.eclipse.xtext:org.eclipse.xtext.xbase.testing:$xtextVersion")
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.6.3")
    testImplementation("io.mockk:mockk:1.12.1")
}

// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    test {
        java.srcDirs("src", "src-gen")
        resources.srcDirs("resources")
    }
}

// Tasks ---------------------------------------------------------------------------------------------------------------

tasks.test {
    useJUnitPlatform()
}
