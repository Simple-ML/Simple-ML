package de.unibonn.simpleml.validation.codes

enum class WarningCode {

    // Name conventions
    NameShouldBeLowerCamelCase,
    NameShouldBeUpperCamelCase,
    SegmentsShouldBeLowerCamelCase,

    // Deprecation
    AssignedDeclarationIsDeprecated,
    CorrespondingParameterIsDeprecated,
    CorrespondingTypeParameterIsDeprecated,
    ReferencedDeclarationIsDeprecated,

    // Unused declarations
    UnusedParameter,
    UnusedPlaceholder,

    // Other
    DuplicateTarget,
    ImplicitlyIgnoredResultOfCall,
    PlaceholderIsRenamingOfDeclaration,
    StatementDoesNothing,
}
