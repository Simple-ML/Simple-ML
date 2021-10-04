package de.projektionisten.simpleml.web.emf.dto

import java.util.List
import org.eclipse.xtend.lib.annotations.Data

@Data
class ProcessMetadataDTO {
    String name

    String emfPath

    String error

    List<ParameterDTO> input
    
    List<ParameterDTO> output
}