//package de.projektionisten.simpleml.web.emf.^extension.Creation
//
//import java.util.ArrayList
//import java.util.regex.Pattern
//import de.projektionisten.simpleml.web.emf.dto.CreateEntityDTO
//
//class EmfCreationHelper {
//	static def createEntity(CreateEntityDTO dto) {
//		val action = createCreationStrategie(dto)
//		action.create()
//	}
//
//	static def CreationHandler createCreationStrategie(CreateEntityDTO dto) {
//		switch dto.className.split(Pattern.quote('.')).last {
//			case 'Assignment':
//				new CreateAssignment(dto.name, createCreationStrategie(dto.children.last))
//			case 'ProcessCall':{
//				val childActions = new ArrayList<CreationHandler>
//				dto.children.forEach[child|
//					childActions.add(createCreationStrategie(child))
//				]
//				new CreateProcessCall(dto.name, childActions)
//			}
//			case 'ArrayLiteral': {
//				val childActions = new ArrayList<CreationHandler>
//				dto.children.forEach[child|
//					childActions.add(createCreationStrategie(child))
//				]
//				new CreateArrayLiteral(childActions)
//			}
//			case 'DoctionaryLiteral': {
//				val childActions = new ArrayList<CreationHandler>
//				dto.children.forEach[child|
//					childActions.add(new CreateDictionaryProperty(child.name, createCreationStrategie(child)))
//				]
//				new CreateDictionaryLiteral(childActions)
//			}	
//			case 'StringLiteral':
//				new CreateStringLiteral(dto.value)
//			case 'BooleanLiteral':
//				new CreateBooleanLiteral(Boolean.parseBoolean(dto.value))
//			case 'IntegerLiteral':
//				new CreateIntegerLiteral(Integer.parseInt(dto.value))
//			case 'FloatLiteral':
//				new CreateFloatLiteral(Float.parseFloat(dto.value))
//			case 'DateLiteral':
//				new CreateDateLiteral(dto.value)
//			case 'TimeLiteral':
//				new CreateTimeLiteral(dto.value)
//			case 'DateTimeLiteral':
//				new CreateDateTimeLiteral(dto.value)
//		}
//	}
//}