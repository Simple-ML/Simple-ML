package de.projektionisten.simpleml.web.dto

data class CreateEntityDTO(
    val referenceIfFunktion: String,
    val placeholderName: String,
    val associationTargetPath: String
) {
  
}

