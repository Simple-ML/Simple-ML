import com.github.gradle.node.npm.task.NpxTask

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    base
    id("com.github.node-gradle.node")
    idea
}

node {
    version.set("16.5.0")
    download.set(true)
}

idea {
    module {
        sourceDirs.add(file("src"))
        sourceDirs.add(file("syntaxes"))

        excludeDirs.add(file("dist"))
        excludeDirs.add(file("node_modules"))
    }
}


// Tasks ---------------------------------------------------------------------------------------------------------------

val extensionPath = "dist/simple-ml-${project.version}.vsix"

tasks.register<Sync>("copyApplication") {
    val shadowJarTask = project(":de.unibonn.simpleml.ide").tasks.named("installDist")
    dependsOn(shadowJarTask)

    from(shadowJarTask.get().outputs)
    into("src/simpleml")
}

tasks {
    npmInstall {
        dependsOn("copyApplication")
    }
}

tasks.register<NpxTask>("vsCodeExtension") {
    group = "Build"
    description = "Generate an extension for VS Code"

    dependsOn("npmInstall")

    inputs.dir("icons")
    inputs.dir("src")
    inputs.dir("syntaxes")
    inputs.files(
        ".vscodeignore",
        "language-configuration.json",
        "package.json",
        "tsconfig.json",
        "webpack.config.js"
    )
    outputs.dirs("dist")

    command.set("vsce")
    args.set(listOf("package", "--out", extensionPath))
}

tasks.register<Exec>("installExtension") {
    dependsOn("vsCodeExtension")

    inputs.files(extensionPath)
    outputs.dirs()

    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("powershell", "code", "--install-extension", extensionPath)
    } else {
        commandLine("code", "--install-extension", extensionPath)
    }
}

tasks.register<Exec>("launchVSCode") {
    group = "Run"
    description = "Launch VS Code with the extension installed"

    dependsOn("installExtension")

    if (System.getProperty("os.name").toLowerCase().contains("windows")) {
        commandLine("powershell", "code", "-n", "../de.unibonn.simpleml/stdlib/stubs")
    } else {
        commandLine("code", "-n", "../de.unibonn.simpleml/stdlib/stubs")
    }
}

tasks {
    build {
        dependsOn("vsCodeExtension")
    }

    clean {
        delete(named("copyApplication").get().outputs)
        delete(named("vsCodeExtension").get().outputs)
    }
}
