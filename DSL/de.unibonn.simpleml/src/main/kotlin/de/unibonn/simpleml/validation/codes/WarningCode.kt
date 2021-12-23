package de.unibonn.simpleml.validation.codes

enum class WarningCode {

    // Casing of names
    NameShouldBeLowercase,
    NameShouldBeUpperCase,
    SegmentsShouldBeLowercase,

    // Unused declarations
    UnusedParameter,
    UnusedPlaceholder,

    // Other
    DuplicateTarget,
    EmptyEnumVariantParameterList,
    ImplicitlyIgnoredResultOfCall,
    PlaceholderIsRenamingOfDeclaration,
    StatementDoesNothing,
}
