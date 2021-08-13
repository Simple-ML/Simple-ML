val javaSourceVersion: JavaVersion by rootProject.extra
val javaTargetVersion: JavaVersion by rootProject.extra
val xtextVersion: String by rootProject.extra


// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    java
    kotlin("jvm")
    id("org.xtext.xtend")
    war
}

java {
    sourceCompatibility = javaSourceVersion
    targetCompatibility = javaTargetVersion
}


// Dependencies --------------------------------------------------------------------------------------------------------

dependencies {
    implementation(project(":de.unibonn.simpleml"))
    implementation(project(":de.unibonn.simpleml.ide"))
    implementation("org.eclipse.xtext:org.eclipse.xtext.xbase.web:${xtextVersion}")
    implementation("org.eclipse.xtext:org.eclipse.xtext.web.servlet:${xtextVersion}")
    implementation("org.eclipse.xtend:org.eclipse.xtend.lib:${xtextVersion}")
    implementation("org.webjars:requirejs:2.3.6")
    implementation("org.webjars:jquery:3.4.1")
    implementation("org.webjars:ace:1.3.3")
    implementation("org.emfjson:emfjson-jackson:1.2.0")

    providedCompile("org.eclipse.jetty:jetty-annotations:11.0.6")
    providedCompile("org.slf4j:slf4j-simple:1.7.32")
}


// Source sets ---------------------------------------------------------------------------------------------------------

sourceSets {
    main {
        java.srcDirs("src", "src-gen")
        resources.srcDirs("resources")
    }
}


// Tasks ---------------------------------------------------------------------------------------------------------------

tasks.register<JavaExec>("jettyRun") {
    group = "run"
    description = "Starts an example Jetty server with your language"

    dependsOn(sourceSets.main.get().runtimeClasspath)
    classpath = sourceSets.main.get().runtimeClasspath.filter { it.exists() }
    mainClass.set("de.unibonn.simpleml.web.ServerLauncher")
    standardInput = System.`in`
}

tasks {
    compileKotlin {
        dependsOn(generateXtext)
    }
}
