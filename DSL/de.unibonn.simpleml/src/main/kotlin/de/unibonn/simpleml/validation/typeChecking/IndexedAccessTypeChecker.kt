package de.unibonn.simpleml.validation.typeChecking

import de.unibonn.simpleml.emf.isResolved
import de.unibonn.simpleml.naming.qualifiedNameOrNull
import de.unibonn.simpleml.simpleML.SimpleMLPackage
import de.unibonn.simpleml.simpleML.SmlIndexedAccess
import de.unibonn.simpleml.simpleML.SmlReference
import de.unibonn.simpleml.staticAnalysis.typing.ClassType
import de.unibonn.simpleml.staticAnalysis.typing.VariadicType
import de.unibonn.simpleml.staticAnalysis.typing.type
import de.unibonn.simpleml.stdlibAccess.StdlibClasses
import de.unibonn.simpleml.validation.AbstractSimpleMLChecker
import de.unibonn.simpleml.validation.codes.ErrorCode
import org.eclipse.xtext.validation.Check

class IndexedAccessTypeChecker: AbstractSimpleMLChecker() {

    @Check
    fun receiverMustBeVariadic(smlIndexedAccess: SmlIndexedAccess) {
        val receiver = smlIndexedAccess.receiver

        if (receiver is SmlReference && !receiver.declaration.isResolved()) {
            return
        }

        if (receiver.type() !is VariadicType) {
            error(
                "The receiver of an indexed access must refer to a variadic parameter.",
                SimpleMLPackage.Literals.SML_ABSTRACT_CHAINED_EXPRESSION__RECEIVER,
                ErrorCode.WrongType
            )
        }
    }

    @Check
    fun indexMustBeInt(smlIndexedAccess: SmlIndexedAccess) {
        val index = smlIndexedAccess.index

        if (index is SmlReference && !index.declaration.isResolved()) {
            return
        }

        val hasWrongType = when (val indexType = index.type()) {
            is ClassType -> indexType.isNullable || indexType.smlClass.qualifiedNameOrNull() != StdlibClasses.Int
            else -> true
        }

        if (hasWrongType) {
            error(
                "The index of an indexed access must be an instance of the class 'Int'.",
                SimpleMLPackage.Literals.SML_INDEXED_ACCESS__INDEX,
                ErrorCode.WrongType
            )
        }
    }
}
