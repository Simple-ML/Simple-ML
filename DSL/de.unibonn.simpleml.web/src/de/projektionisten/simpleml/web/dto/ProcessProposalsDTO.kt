package de.projektionisten.simpleml.web.dto

data class ProcessProposalsDTO(
    val frontendId: String,
    val emfPath: String?,
    val proposals: List<ProcessMetadataDTO>
) {

}