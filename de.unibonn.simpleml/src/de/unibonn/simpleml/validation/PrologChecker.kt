package de.unibonn.simpleml.validation

import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import org.eclipse.xtext.validation.Check
import org.eclipse.emf.ecore.EObject

class PrologChecker : AbstractSimpleMLChecker() {

    @Check
    fun checkWithProlog(smlCompilationUnit: SmlCompilationUnit) {
        // Call Main to create factbase TODO hier muss dann die Factbase wiederverwendet werden
        // Use Prolog to check factbase for errors

        // Show errors in editor
        val issues = listOf<Issue>()
        issues.forEach {
            when (it.severity) {
                "error" -> {
                    error(
                            it.message,
                            it.targetObject,
                            null
                    )
                }
                "warning" -> {
                    warning(
                            it.message,
                            it.targetObject,
                            null
                    )
                }
                "info" -> {
                    info(
                            it.message,
                            it.targetObject,
                            null
                    )
                }
            }
        }
    }
}

data class Issue(val severity: String, val message: String, val targetObject: EObject)

//[
//  {
//	  "severity": "error",
//	  "message": "no error found",
//	  "targetObject": 8
//  }
//]