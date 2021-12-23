package de.unibonn.simpleml.validation.codes

enum class WarningCode {

    // Casing of names
    NameShouldBeLowerCamelCase,
    NameShouldBeUpperCamelCase,
    SegmentsShouldBeLowerCamelCase,

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
