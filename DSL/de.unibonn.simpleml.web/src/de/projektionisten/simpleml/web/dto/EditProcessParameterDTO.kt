package de.projektionisten.simpleml.web.dto

data class EditProcessParameterDTO(
    val entityPath: String,
    val parameterType: String,
    val parameterIndex: Number,
    val value: String
) {
  
}
