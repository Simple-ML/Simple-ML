import com.github.gradle.node.npm.task.NpxTask

// Plugins -------------------------------------------------------------------------------------------------------------

plugins {
    id("com.github.node-gradle.node")
}

node {
    version.set("16.5.0")
    download.set(true)
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

    command.set("vsce")
    args.set(listOf("package", "--out", extensionPath))
}

tasks.register<Exec>("installExtension") {
    dependsOn("vsCodeExtension")

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

//tasks {
//    clean {
//        doLast {
//            delete("serverStartScripts/de.unibonn.simpleml.ide-ls.jar")
//            delete("dist")
//            delete(named("copyApplication").get().outputs)
//        }
//    }
//}


//updateVersion {
//    doLast {
//        // custom code
//        def versionPattern = /\d+.\d+(.\d+)?/
//        def encoding = 'UTF-8'
//        def filesToUpdate = [
//                new File('vscode-extension', 'package.json'),
//        new File('vscode-extension-self-contained', 'package.json'),
//        new File('atom-extension', 'package.json')
//        ]
//
//        // String replacements - isn't long enough to justify advanced code ;)
//        filesToUpdate.forEach { file ->
//
//            String text = file.getText(encoding)
//            text = text.replaceAll("\"version\": \"$versionPattern\",", "\"version\": \"$project.version\",")
//            file.setText(text, encoding)
//        }
//    }
//}

//// Configuration for vscode projects
//configure(subprojects.findAll { it.name.startsWith('vscode') }) {
//
//    apply plugin: 'com.github.node-gradle.node'
//    node {
//        version = '10.16.0'
//        npmVersion = '6.10.2'
//        download = true
//    }
//
//    def inputFiles = fileTree(
//            dir: projectDir,
//    excludes: [ 'out/**', '.gitignore', '.gradle/**', 'build/**', '*.gradle' ]
//    )
//
//    npmInstall {
//        inputs.files(inputFiles)
//        outputs.dir('out')
//    }
//
//    task vscodeExtension(dependsOn: [npmInstall, npmInstallVsce], type: NodeTask) {
//    ext.destDir = new File(buildDir, 'vscode')
//    ext.archiveName = "$project.name-${project.version}.vsix"
//    ext.destPath = "$destDir/$archiveName"
//    inputs.with {
//        files inputFiles
//                dir npmInstallVsce.destPath
//    }
//    outputs.dir destDir
//            doFirst {
//                destDir.mkdirs()
//            }
//    script = file("$npmInstallVsce.destPath/out/vsce")
//    args = [ 'package', '--out', destPath ]
//    execOverrides {
//        workingDir = projectDir
//    }
//}
//
//    task clean {
//        doLast {
//            delete vscodeExtension.destDir
//                    delete 'out' // output of npmInstall - don't want to delete node_modules
//        }
//
//    }
//
//}
//
//plugins.withType(com.moowork.gradle.node.NodePlugin) {
//    node {
//        workDir = file("$rootProject.buildDir/nodejs")
//        nodeModulesDir = rootProject.projectDir
//    }
//}
//

//
//release {
//    tagTemplate = 'v${version}'
//    preTagCommitMessage = '[release] pre tag commit: '
//    tagCommitMessage = '[release] creating tag: '
//    newVersionCommitMessage = '[release] new version commit: '
//    failOnSnapshotDependencies = false
//}

//task copyApplication(type: Sync) {
//    def installDistTask = project(':org.xtext.example.mydsl.ide').tasks.installDist
//    dependsOn installDistTask
//            from installDistTask.outputs
//            into 'src/mydsl'
//}
//
//clean {
//    doLast {
//        delete copyApplication.outputs
//    }
//}
//
//npmInstall.dependsOn copyApplication
//
//        task installExtension(type: Exec, dependsOn: vscodeExtension) {
//    if (System.properties['os.name'].toLowerCase().contains('windows')) {
//        commandLine 'code.cmd'
//    } else {
//        commandLine 'code'
//    }
//    args '--install-extension', vscodeExtension.destPath
//}
//
//task startCode(type:Exec, dependsOn: installExtension) {
//    if (System.properties['os.name'].toLowerCase().contains('windows')) {
//        commandLine 'code.cmd'
//    } else {
//        commandLine 'code'
//    }
//    args "$rootProject.projectDir/demo/", '--new-window'
//}
//
//task publish(dependsOn: vscodeExtension, type: NodeTask) {
//    script = file("$rootProject.projectDir/node_modules/vsce/out/vsce")
//    args = [ 'publish', '-p', System.getenv('ACCESS_TOKEN'), "--packagePath", "${project.buildDir}/vscode/vscode-extension-self-contained-${project.version}.vsix"]
//    execOverrides {
//        workingDir = projectDir
//    }
//}