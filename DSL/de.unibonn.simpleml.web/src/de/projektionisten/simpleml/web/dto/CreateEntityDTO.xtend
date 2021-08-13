package de.projektionisten.simpleml.web.dto

import org.eclipse.xtend.lib.annotations.Data

@Data
class CreateEntityDTO {
	String name
	
	String className
	
	String value
	
	CreateEntityDTO[] children 
	
//	static def de.projektionisten.simpleml.web.emf.dto.CreateEntityDTO convert(CreateEntityDTO entityDTO) {
//		return new de.projektionisten.simpleml.web.emf.dto.CreateEntityDTO(
//			entityDTO.name,
//			entityDTO.className,
//			entityDTO.value,
//			entityDTO.children?.map[
//				return CreateEntityDTO.convert(it) as de.projektionisten.simpleml.web.emf.dto.CreateEntityDTO
//			]
//		);
//	}
}