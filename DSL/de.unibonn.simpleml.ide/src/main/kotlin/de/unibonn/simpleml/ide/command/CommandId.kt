package de.unibonn.simpleml.ide.command

enum class CommandId {
    RemoveOnceOtherCommandsAreAdded;

    override fun toString(): String {
        return "simple-ml." + this.name
    }
}
