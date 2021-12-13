@file:Suppress("unused")

package de.unibonn.simpleml.xtextConfiguration

import org.eclipse.emf.mwe.utils.DirectoryCleaner
import org.eclipse.emf.mwe.utils.ProjectMapping
import org.eclipse.emf.mwe.utils.StandaloneSetup
import org.eclipse.emf.mwe2.ecore.EcoreGenerator
import org.eclipse.emf.mwe2.runtime.workflow.Workflow
import org.eclipse.emf.mwe2.runtime.workflow.WorkflowContextImpl
import org.eclipse.xtext.xtext.generator.CodeConfig
import org.eclipse.xtext.xtext.generator.DefaultGeneratorModule
import org.eclipse.xtext.xtext.generator.StandardLanguage
import org.eclipse.xtext.xtext.generator.XtextGenerator
import org.eclipse.xtext.xtext.generator.model.project.StandardProjectConfig

fun workflow(lambda: Workflow.() -> Unit): Workflow {
    return Workflow().apply(lambda)
}

fun Workflow.standaloneSetup(lambda: StandaloneSetup.() -> Unit) {
    addBean(StandaloneSetup().apply(lambda))
}

fun StandaloneSetup.projectMapping(projectName: String, path: String) {
    addProjectMapping(
        ProjectMapping().apply {
            this.projectName = projectName
            this.path = path
        }
    )
}

fun Workflow.directoryCleaner(directory: String) {
    addComponent(
        DirectoryCleaner().apply {
            setDirectory(directory)
        }
    )
}

fun Workflow.ecoreGenerator(genModel: String, srcPaths: List<String>, lambda: EcoreGenerator.() -> Unit = {}) {
    addComponent(
        EcoreGenerator().apply {
            setGenModel(genModel)
            srcPaths.forEach { addSrcPath(it) }
            lambda()
        }
    )
}

fun Workflow.xtextGenerator(lambda: XtextGenerator.() -> Unit) {
    addComponent(XtextGenerator().apply(lambda))
}

fun XtextGenerator.configuration(lambda: DefaultGeneratorModule.() -> Unit) {
    configuration = DefaultGeneratorModule().apply(lambda)
}

fun DefaultGeneratorModule.project(lambda: StandardProjectConfig.() -> Unit) {
    project = StandardProjectConfig().apply(lambda)
}

fun DefaultGeneratorModule.code(lambda: CodeConfig.() -> Unit) {
    code = CodeConfig().apply(lambda)
}

fun XtextGenerator.standardLanguage(lambda: StandardLanguage.() -> Unit) {
    addLanguage(StandardLanguage().apply(lambda))
}

fun Workflow.execute() {
    run(WorkflowContextImpl())
}
