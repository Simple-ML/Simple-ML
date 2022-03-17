# Package `simpleml.lang`

## Table of Contents

* [Class `Any`](#class-Any)
* [Class `Boolean`](#class-Boolean)
* [Class `Float`](#class-Float)
* [Class `Int`](#class-Int)
* [Class `Nothing`](#class-Nothing)
* [Class `Number`](#class-Number)
* [Class `String`](#class-String)
* [Global function `print`](#global-function-print)
* [Global function `println`](#global-function-println)
* [Enum `AnnotationTarget`](#enum-AnnotationTarget)
* [Annotation `Constant`](#annotation-Constant)
* [Annotation `Deprecated`](#annotation-Deprecated)
* [Annotation `Description`](#annotation-Description)
* [Annotation `NoSideEffects`](#annotation-NoSideEffects)
* [Annotation `Pure`](#annotation-Pure)
* [Annotation `PythonModule`](#annotation-PythonModule)
* [Annotation `PythonName`](#annotation-PythonName)
* [Annotation `Repeatable`](#annotation-Repeatable)
* [Annotation `Since`](#annotation-Since)
* [Annotation `Target`](#annotation-Target)

----------

## Class `Any`
Common superclass of all classes.

**Constructor:** _Class has no constructor._


----------

## Class `Boolean`
_No description available._

**Constructor:** _Class has no constructor._


----------

## Class `Float`
_No description available._

**Constructor:** _Class has no constructor._


----------

## Class `Int`
_No description available._

**Constructor:** _Class has no constructor._


----------

## Class `Nothing`
Common subclass of all classes.

**Constructor:** _Class has no constructor._


----------

## Class `Number`
_No description available._

**Constructor:** _Class has no constructor._


----------

## Class `String`
_No description available._

**Constructor:** _Class has no constructor._


## Global Function `print`
_No description available._

**Parameters:**
* `obj: Any` - _No description available._

**Results:** _None returned._

## Global Function `println`
_No description available._

**Parameters:**
* `obj: Any` - _No description available._

**Results:** _None returned._

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



## Annotation `Constant`
Values assigned to this parameter must be constant.

**Valid targets:**
* Parameter

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

## Annotation `NoSideEffects`
The function has no side effects.

**Valid targets:**
* Function

## Annotation `Pure`
The function has no side effects and returns the same results for the same arguments.

**Valid targets:**
* Function

## Annotation `PythonModule`
Qualified name of the corresponding module in Python (default is the qualified name of the package).

**Parameters:**
* `qualifiedName: String` - _No description available._

**Valid targets:**
* CompilationUnit

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

## Annotation `Repeatable`
The annotation can be called multiple times for the same declaration.

**Valid targets:**
* Annotation

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

## Annotation `Target`
The annotation can target these declaration types. If the @Target annotation is not used any declaration type can be targeted.

**Parameters:**
* `vararg targets: AnnotationTarget` - _No description available._

**Valid targets:**
* Annotation

----------

**This file was created automatically. Do not change it manually!**
