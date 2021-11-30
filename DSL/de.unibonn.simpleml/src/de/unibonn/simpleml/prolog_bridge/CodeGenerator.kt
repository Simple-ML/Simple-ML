package de.unibonn.simpleml.prolog_bridge

import com.google.inject.Inject
import com.google.inject.Provider
import de.unibonn.simpleml.SimpleMLStandaloneSetup
import de.unibonn.simpleml.prolog_bridge.model.facts.PlAtom
import de.unibonn.simpleml.prolog_bridge.model.facts.PlBooleanAtom
import de.unibonn.simpleml.prolog_bridge.model.facts.PlFact
import de.unibonn.simpleml.prolog_bridge.model.facts.PlList
import de.unibonn.simpleml.prolog_bridge.model.facts.PlNull
import de.unibonn.simpleml.prolog_bridge.model.facts.PlNumber
import de.unibonn.simpleml.prolog_bridge.model.facts.PlString
import de.unibonn.simpleml.prolog_bridge.model.facts.PlTerm
import de.unibonn.simpleml.simpleML.SimpleMLFactory
import de.unibonn.simpleml.simpleML.SmlAnnotation
import de.unibonn.simpleml.simpleML.SmlArgumentList
import de.unibonn.simpleml.simpleML.SmlCompilationUnit
import de.unibonn.simpleml.simpleML.SmlConstructor
import de.unibonn.simpleml.simpleML.SmlDeclaration
import de.unibonn.simpleml.simpleML.SmlExpression
import de.unibonn.simpleml.simpleML.SmlNamedType
import de.unibonn.simpleml.simpleML.SmlNamedTypeDeclaration
import de.unibonn.simpleml.simpleML.SmlParameter
import de.unibonn.simpleml.simpleML.SmlParameterList
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.simpleML.SmlResult
import de.unibonn.simpleml.simpleML.SmlType
import de.unibonn.simpleml.simpleML.SmlTypeArgumentValue
import de.unibonn.simpleml.simpleML.SmlTypeParameter
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.xtext.nodemodel.ICompositeNode
import org.eclipse.xtext.nodemodel.impl.NodeModelBuilder
import org.eclipse.xtext.resource.SaveOptions
import org.eclipse.xtext.resource.XtextResource
import org.eclipse.xtext.serializer.impl.Serializer
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

class CodeGenerator {
    @Inject
    private val resourceSetProvider: Provider<ResourceSet>? = null

    @Inject
    internal var serializer: Serializer? = null

    internal lateinit var data: MutableList<String>
    private val facts = mutableMapOf<Int, PlFact>()
    private val syntax = mutableMapOf<Int, PlFact>()
    private val eob = mutableMapOf<Int, EObject>()
    private val nodes: Map<Int, ICompositeNode>? = null

    fun readFile(file: String) {
        data = ArrayList()
        try {
            Files.newBufferedReader(Paths.get(file)).use { br ->

                // read line by line
                var line: String?
                line = br.readLine()
                while (line != null) {
                    if (line != "" && !line.startsWith(":-") && !line.startsWith("simpleml:pefCount")) {
                        data.add(line + "\n")
                    }
                    line = br.readLine()
                }
            }
        } catch (e: IOException) {
            System.err.format("IOException: %s%n", e)
        }
    }

    fun getData(): List<String> {
        return data
    }

    fun getFacts(): Map<Int, PlFact> {
        return facts
    }

    fun getSyntax(): Map<Int, PlFact> {
        return syntax
    }

    private fun parseData() {
        for (s in data) {
            var regex = Pattern.compile("^[^(]*")
            val regexMatcher = regex.matcher(s)
            val name = if (regexMatcher.find()) {
                PlAtom(regexMatcher.group())
            } else {
                null
            }
            regex = Pattern.compile("\\((.*?)\\)")
            val regexMatcher2 = regex.matcher(s)
            val arguments = if (regexMatcher2.find()) {
                PlTerm.fromString(regexMatcher2.group().substring(1, regexMatcher2.group().length - 1))
            } else {
                null
            }

            if (name != null && arguments != null) {
                // TODO create the correct classes (actual PEF class)

//                val f = PlFact(name, arguments)

//                if (f.factName == "simpleml:sourceLocationT") {
//                    syntax[f.id2] = f
//                } else {
//                    facts[f.id2] = f
//                }
            }
        }
    }

    private fun handleList(list: PlTerm): List<EObject>? {
        return when (list) {
            is PlList -> {
                val args = ArrayList<EObject>()
                for (p in list.arguments) {
                    val t = facts[(p as PlNumber).value.toInt()]
                    val a = doSwitch(t)
                    if (a != null) args.add(a)
                }

                args
            }
            is PlNull -> null
            else -> throw IllegalArgumentException()
        }
    }

    private fun handleStringAsString(str: PlTerm): String? {
        return when (str) {
            is PlNull -> null
            is PlString -> {
                str.toRawString()
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun handleIntAsInt(i: PlTerm): Int {
        when (i) {
            is PlNumber -> {
                return i.value.toInt()
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun handleDoubleAsDouble(i: PlTerm): Double {
        when (i) {
            is PlNumber -> {
                return i.value.toDouble()
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun handleBoolean(b: PlTerm): Boolean {
        when (b) {
            is PlBooleanAtom -> {
                return b.value
            }
            else -> throw IllegalArgumentException()
        }
    }

    private fun handleInt(i: PlTerm): EObject? {
        return when (i) {
            is PlNull -> null
            is PlNumber -> doSwitch(facts[i.value.toInt()])
            else -> throw IllegalArgumentException()
        }
    }

    private fun addList(list: EList<EObject>, addList: List<EObject>) {
        for (e in addList) {
            list.add(e)
        }
    }

    private fun doSwitch(f: PlFact?): EObject? {
        if (f == null) return null
        if (eob.contains(handleIntAsInt(f.plArguments[0]))) return eob[handleIntAsInt(f.plArguments[0])]

        val key = handleIntAsInt(f.plArguments[0])

        when (f.factName.value) {
            "simpleml:compilationUnitT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlCompilationUnit()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                addList(e.imports as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)
                addList(e.members as EList<EObject>, handleList(f.plArguments[4]) as List<EObject>)

                return e
            }
            "simpleml:workflowT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlWorkflow()

                e.name = handleStringAsString(f.plArguments[2])
                e.body = SimpleMLFactory.eINSTANCE.createSmlBlock()
                addList(e.body.statements as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)

                return e
            }
            "simpleml:importT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlImport()
                eob[key] = e

                e.importedNamespace = handleStringAsString(f.plArguments[2])
                e.alias = SimpleMLFactory.eINSTANCE.createSmlImportAlias()
                e.alias.name = handleStringAsString(f.plArguments[3])

                return e
            }
            "simpleml:assignmentT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlAssignment()
                eob[key] = e

                e.expression = handleInt(f.plArguments[2]) as SmlExpression
                e.assigneeList = SimpleMLFactory.eINSTANCE.createSmlAssigneeList()
                addList(e.assigneeList.assignees as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)

                return e
            }
            "simpleml:expressionStatementT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlExpressionStatement()
                eob[key] = e

                e.expression = handleInt(f.plArguments[2]) as SmlExpression

                return e
            }
            "simpleml:placeholderT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlPlaceholder()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                addList(e.annotations as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)
                addList(e.modifiers as EList<EObject>, handleList(f.plArguments[4]) as List<EObject>)

                return e
            }
            "simpleml:callT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlCall()
                eob[key] = e

                e.receiver = handleInt(f.plArguments[3]) as SmlExpression
                e.argumentList = SimpleMLFactory.eINSTANCE.createSmlArgumentList()
                addList(e.argumentList.arguments as EList<EObject>, handleList(f.plArguments[4]) as List<EObject>)
                e.typeArgumentList = SimpleMLFactory.eINSTANCE.createSmlTypeArgumentList()

                // TODO das hier auslagern, wird wohl häufiger benutzt
                val l = handleList(f.plArguments[5])
                if (l != null) addList(e.typeArgumentList.typeArguments as EList<EObject>, l)
                else e.typeArgumentList = null

                return e
            }
            "simpleml:memberAccessT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlMemberAccess()
                eob[key] = e

                e.receiver = handleInt(f.plArguments[3]) as SmlExpression
                e.member = handleInt(f.plArguments[4]) as SmlReference
                e.isNullable = handleBoolean(f.plArguments[5])

                return e
            }
            "simpleml:referenceT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlReference()
                eob[key] = e

                e.declaration = handleInt(f.plArguments[3]) as SmlDeclaration?

                return e
            }
            "simpleml:typeParameterConstraintT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlTypeParameterConstraint()
                eob[key] = e

                e.operator = handleStringAsString(f.plArguments[3])
                e.leftOperand = handleInt(f.plArguments[4]) as SmlTypeParameter
                e.rightOperand = handleInt(f.plArguments[5]) as SmlType

                return e
            }
            "simpleml:typeParameterT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlTypeParameter()
                eob[key] = e

                e.variance = handleStringAsString(f.plArguments[2])

                return e
            }
            "simpleml:argumentT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlArgument()
                eob[key] = e

                e.parameter = handleInt(f.plArguments[3]) as SmlParameter?
                e.value = handleInt(f.plArguments[4]) as SmlExpression?

                return e
            }
            "simpleml:parameterT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlParameter()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.type = handleInt(f.plArguments[3]) as SmlType?
                e.defaultValue = handleInt(f.plArguments[4]) as SmlExpression?
                e.isVararg = handleBoolean(f.plArguments[5])

                return e
            }
            "simpleml:stringLiteralT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlString()
                eob[key] = e

                e.value = handleStringAsString(f.plArguments[3])

                return e
            }
            "simpleml:intLiteralT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlInt()
                eob[key] = e

                e.value = handleIntAsInt(f.plArguments[3])

                return e
            }
            "simpleml:floatLiteralT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlFloat()
                eob[key] = e

                e.value = handleDoubleAsDouble(f.plArguments[3])

                return e
            }
            "simpleml:booleanLiteralT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlBoolean()
                eob[key] = e

                e.isTrue = handleBoolean(f.plArguments[3])

                return e
            }
            "simpleml:nullT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlParameterList()
                eob[key] = e

                return e
            }
            "simpleml:starProjectionT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlParameterList()
                eob[key] = e

                return e
            }
            "simpleml:typeProjectionT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlTypeProjection()
                eob[key] = e

                e.variance = handleStringAsString(f.plArguments[3])
                e.type = handleInt(f.plArguments[4]) as SmlType?

                return e
            }
            "simpleml:prefixOperationT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlPrefixOperation()
                eob[key] = e

                e.operator = handleStringAsString(f.plArguments[3])
                e.operand = handleInt(f.plArguments[4]) as SmlExpression?

                return e
            }
            "simpleml:infixOperationT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlInfixOperation()
                eob[key] = e

                e.leftOperand = handleInt(f.plArguments[3]) as SmlExpression?
                e.operator = handleStringAsString(f.plArguments[4])
                e.rightOperand = handleInt(f.plArguments[5]) as SmlExpression?

                return e
            }
            "simpleml:lambdaT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlLambda()
                eob[key] = e

                e.parameterList = handleInt(f.plArguments[4]) as SmlParameterList?
                e.body = SimpleMLFactory.eINSTANCE.createSmlBlock()
                addList(e.body.statements as EList<EObject>, handleList(f.plArguments[5]) as List<EObject>)

                return e
            }
            "simpleml:functionTypeT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlCallableType()
                eob[key] = e

                e.parameterList = SimpleMLFactory.eINSTANCE.createSmlParameterList()
                addList(e.parameterList.parameters as EList<EObject>, handleList(f.plArguments[2]) as List<EObject>)
                e.resultList = SimpleMLFactory.eINSTANCE.createSmlResultList()
                addList(e.resultList.results as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)

                return e
            }
            "simpleml:memberTypeT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlMemberType()
                eob[key] = e

                e.member = handleInt(f.plArguments[2]) as SmlNamedType?
                e.receiver = handleInt(f.plArguments[3]) as SmlType?

                return e
            }
            "simpleml:namedTypeT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlNamedType()
                eob[key] = e

                e.declaration = handleInt(f.plArguments[2]) as SmlNamedTypeDeclaration?
                e.typeArgumentList = SimpleMLFactory.eINSTANCE.createSmlTypeArgumentList()
                addList(
                    e.typeArgumentList.typeArguments as EList<EObject>,
                    handleList(f.plArguments[3]) as List<EObject>
                )
                e.isNullable = handleBoolean(f.plArguments[4])

                return e
            }
            "simpleml:functionT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlFunction()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.parameterList = SimpleMLFactory.eINSTANCE.createSmlParameterList()
                addList(e.parameterList.parameters as EList<EObject>, handleList(f.plArguments[4]) as List<EObject>)
                e.resultList = SimpleMLFactory.eINSTANCE.createSmlResultList()
                addList(e.resultList.results as EList<EObject>, handleList(f.plArguments[6]) as List<EObject>)
                e.typeParameterConstraintList = SimpleMLFactory.eINSTANCE.createSmlTypeParameterConstraintList()
                addList(
                    e.typeParameterConstraintList.constraints as EList<EObject>,
                    handleList(f.plArguments[7]) as List<EObject>
                )
                e.typeParameterList = SimpleMLFactory.eINSTANCE.createSmlTypeParameterList()
                addList(
                    e.typeParameterList.typeParameters as EList<EObject>,
                    handleList(f.plArguments[8]) as List<EObject>
                )

                return e
            }
            "simpleml:wildcardT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlWildcard()
                eob[key] = e

                return e
            }
            "simpleml:yieldT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlYield()
                eob[key] = e

                e.result = handleInt(f.plArguments[3]) as SmlResult?

                return e
            }
            "simpleml:lambdaYieldT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlLambdaYield()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[3])

                return e
            }
            "simpleml:resultT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlResult()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.type = handleInt(f.plArguments[3]) as SmlType?

                return e
            }
            "simpleml:attribute" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlAttribute()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.type = handleInt(f.plArguments[3]) as SmlType?

                return e
            }
            "simpleml:annotationT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlAnnotation()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.parameterList = handleInt(f.plArguments[3]) as SmlParameterList?

                return e
            }
            "simpleml:annotationUseT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlAnnotationUse()
                eob[key] = e

                e.annotation = handleInt(f.plArguments[2]) as SmlAnnotation?
                e.argumentList = handleInt(f.plArguments[3]) as SmlArgumentList?

                return e
            }
            "simpleml:enumInstanceT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlEnumInstance()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])

                return e
            }
            "simpleml:constructorT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlConstructor()
                eob[key] = e

                e.parameterList = SimpleMLFactory.eINSTANCE.createSmlParameterList()
                addList(e.parameterList.parameters as EList<EObject>, handleList(f.plArguments[2]) as List<EObject>)

                return e
            }
            "simpleml:classT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlClass()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.body = SimpleMLFactory.eINSTANCE.createSmlClassBody()
                addList(e.body.members as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)
                e.constructor = handleInt(f.plArguments[4]) as SmlConstructor?
                e.parentTypeList = SimpleMLFactory.eINSTANCE.createSmlParentTypeList()
                addList(e.parentTypeList.parentTypes as EList<EObject>, handleList(f.plArguments[5]) as List<EObject>)
                e.typeParameterConstraintList = SimpleMLFactory.eINSTANCE.createSmlTypeParameterConstraintList()
                addList(
                    e.typeParameterConstraintList.constraints as EList<EObject>,
                    handleList(f.plArguments[6]) as List<EObject>
                )
                e.typeParameterList = SimpleMLFactory.eINSTANCE.createSmlTypeParameterList()
                addList(
                    e.typeParameterList.typeParameters as EList<EObject>,
                    handleList(f.plArguments[7]) as List<EObject>
                )

                return e
            }
            "simpleml:enumT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlEnum()
                eob[key] = e

                e.name = handleStringAsString(f.plArguments[2])
                e.body = SimpleMLFactory.eINSTANCE.createSmlEnumBody()
                addList(e.body.instances as EList<EObject>, handleList(f.plArguments[3]) as List<EObject>)

                return e
            }
            "simpleml:typeArgumentT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlTypeArgument()
                eob[key] = e

                e.typeParameter = handleInt(f.plArguments[3]) as SmlTypeParameter?
                e.value = handleInt(f.plArguments[4]) as SmlTypeArgumentValue?

                return e
            }
            "simpleml:unionTypeT" -> {
                val e = SimpleMLFactory.eINSTANCE.createSmlUnionType()
                eob[key] = e

                e.typeArgumentList = SimpleMLFactory.eINSTANCE.createSmlTypeArgumentList()
                addList(
                    e.typeArgumentList.typeArguments as EList<EObject>,
                    handleList(f.plArguments[2]) as List<EObject>
                )

                return e
            }
            else -> return null
        }
    }

    fun run() {

        val set = resourceSetProvider!!.get()
        val fileURI = URI.createFileURI("output.simpleml")
        val resource = set.createResource(fileURI) as XtextResource

        readFile("prolog_facts.pl")
        parseData()

        NodeModelBuilder()

        println(facts.size)
        val u = doSwitch(facts[0]) as SmlCompilationUnit?
        resource.contents.add(u)

        // To create the node model, this class might be a possible starting point (did not check yet)
        // new NodeModelBuilder()
        // See https://www.eclipse.org/Xtext/documentation/303_runtime_concepts.html#serialization

        // We could also relax the criteria simpleml(prolog(program)) == program to
        // format(simpleml(prolog(program))) == format(program)
        // where format automatically formats the given program. Then we would not need to bother with syntactic
        // information at all and only restore comments.

        try {
            val saveOptions = SaveOptions
                .newBuilder()
                .format()
                .options
                .toOptionsMap()
            resource.save(saveOptions)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            val injector = SimpleMLStandaloneSetup().createInjectorAndDoEMFRegistration()
            val creator = injector.getInstance(CodeGenerator::class.java)
            creator.run()
        }
    }
}
