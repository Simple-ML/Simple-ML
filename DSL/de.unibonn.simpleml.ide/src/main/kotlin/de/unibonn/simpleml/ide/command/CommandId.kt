package de.unibonn.simpleml.ide.command

enum class CommandId {
    MoreParameters;

    override fun toString(): String {
        return "simple-ml." + this.name
    }
}
