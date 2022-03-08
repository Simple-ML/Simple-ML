package de.projektionisten.simpleml.web.dto

data class ProcessMetadataDTO(
    val name: String,
    val emfPath: String, 
    val error: String,
    val input: List<ParameterDTO?>,
    val output: List<ParameterDTO?>,
    val description: String?
) {

}