
[Tutorial][tutorial] - [Idea and basic concepts][tutorial_concepts] | [Interface][tutorial_interface] | [**API**][api] | [DSL][dsl-tutorial]

[tutorial]: ./Tutorial.md
[tutorial_concepts]: ./Tutorial-Basic-Concepts.md
[tutorial_interface]: ./Tutorial-The-Simple-ML-Interface.md
[api]: ./README.md
[dsl-tutorial]: ./DSL/tutorial/README.md

# Package `simpleml.lang`

## Table of Contents

* Classes
  * [`Any`](#class-Any)
  * [`Boolean`](#class-Boolean)
  * [`Float`](#class-Float)
  * [`Int`](#class-Int)
  * [`Nothing`](#class-Nothing)
  * [`Number`](#class-Number)
  * [`String`](#class-String)
* Global functions
  * [`print`](#global-function-print)
  * [`println`](#global-function-println)
* Enums
  * [`AnnotationTarget`](#enum-AnnotationTarget)
* Annotations
  * [`Constant`](#annotation-Constant)
  * [`Deprecated`](#annotation-Deprecated)
  * [`Description`](#annotation-Description)
  * [`NoSideEffects`](#annotation-NoSideEffects)
  * [`Pure`](#annotation-Pure)
  * [`PythonModule`](#annotation-PythonModule)
  * [`PythonName`](#annotation-PythonName)
  * [`Repeatable`](#annotation-Repeatable)
  * [`Since`](#annotation-Since)
  * [`Target`](#annotation-Target)

----------

<a name='class-Any'/>

## Class `Any`
Common superclass of all classes.

**Constructor:** _Class has no constructor._


----------

<a name='class-Boolean'/>

## Class `Boolean`
_No description available._

**Constructor:** _Class has no constructor._


----------

<a name='class-Float'/>

## Class `Float`
_No description available._

**Constructor:** _Class has no constructor._


----------

<a name='class-Int'/>

## Class `Int`
_No description available._

**Constructor:** _Class has no constructor._


----------

<a name='class-Nothing'/>

## Class `Nothing`
Common subclass of all classes.

**Constructor:** _Class has no constructor._


----------

<a name='class-Number'/>

## Class `Number`
_No description available._

**Constructor:** _Class has no constructor._


----------

<a name='class-String'/>

## Class `String`
_No description available._

**Constructor:** _Class has no constructor._


## Global Functions
### Instance Method `print`
_No description available._

**Parameters:**
* `obj: Any` - _No description available._

**Results:** _None returned._

### Instance Method `println`
_No description available._

**Parameters:**
* `obj: Any` - _No description available._

**Results:** _None returned._

<a name='enum-AnnotationTarget'/>

## Enum `AnnotationTarget`
Declaration types that can be targeted by annotations.
### Enum Variant `Annotation`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Attribute`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Class`
_No description available._

**Parameters:** _None expected._


### Enum Variant `CompilationUnit`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Enum`
_No description available._

**Parameters:** _None expected._


### Enum Variant `EnumVariant`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Function`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Parameter`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Result`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Step`
_No description available._

**Parameters:** _None expected._


### Enum Variant `TypeParameter`
_No description available._

**Parameters:** _None expected._


### Enum Variant `Workflow`
_No description available._

**Parameters:** _None expected._



<a name='annotation-Constant'/>

## Annotation `Constant`
Values assigned to this parameter must be constant.

**Valid targets:**
* Parameter

<a name='annotation-Deprecated'/>

## Annotation `Deprecated`
The declaration should no longer be used.

**Parameters:**
* `alternative: String? = null` - _No description available._
* `reason: String? = null` - _No description available._
* `sinceVersion: String? = null` - _No description available._
* `removalVersion: String? = null` - _No description available._

**Valid targets:**
* Annotation
* Attribute
* Class
* Enum
* EnumVariant
* Function
* Parameter
* Result
* Step
* TypeParameter

<a name='annotation-Description'/>

## Annotation `Description`
Purpose of a declaration.

**Parameters:**
* `description: String` - _No description available._

**Valid targets:**
* Annotation
* Attribute
* Class
* CompilationUnit
* Enum
* EnumVariant
* Function
* Parameter
* Result
* Step
* TypeParameter
* Workflow

<a name='annotation-NoSideEffects'/>

## Annotation `NoSideEffects`
The function has no side effects.

**Valid targets:**
* Function

<a name='annotation-Pure'/>

## Annotation `Pure`
The function has no side effects and returns the same results for the same arguments.

**Valid targets:**
* Function

<a name='annotation-PythonModule'/>

## Annotation `PythonModule`
Qualified name of the corresponding module in Python (default is the qualified name of the package).

**Parameters:**
* `qualifiedName: String` - _No description available._

**Valid targets:**
* CompilationUnit

<a name='annotation-PythonName'/>

## Annotation `PythonName`
Name of the corresponding API element in Python (default is the name of the declaration in the stubs).

**Parameters:**
* `name: String` - _No description available._

**Valid targets:**
* Attribute
* Class
* Enum
* EnumVariant
* Function
* Parameter
* Step
* Workflow

<a name='annotation-Repeatable'/>

## Annotation `Repeatable`
The annotation can be called multiple times for the same declaration.

**Valid targets:**
* Annotation

<a name='annotation-Since'/>

## Annotation `Since`
Version in which a declaration was added.

**Parameters:**
* `version: String` - _No description available._

**Valid targets:**
* Annotation
* Attribute
* Class
* CompilationUnit
* Enum
* EnumVariant
* Function
* Parameter
* Result
* Step
* TypeParameter
* Workflow

<a name='annotation-Target'/>

## Annotation `Target`
The annotation can target these declaration types. If the @Target annotation is not used any declaration type can be targeted.

**Parameters:**
* `vararg targets: AnnotationTarget` - _No description available._

**Valid targets:**
* Annotation

----------

**This file was created automatically. Do not change it manually!**
