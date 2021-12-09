package de.unibonn.simpleml.locations

/**
 * A specific position in a program.
 */
data class ProgramPosition(val line: ProgramLine, val column: ProgramColumn) : Comparable<ProgramPosition> {
    companion object {

        @JvmStatic
        fun fromInts(line: Int, column: Int): ProgramPosition {
            return ProgramPosition(
                ProgramLine(line),
                ProgramColumn(column)
            )
        }
    }

    override fun toString(): String {
        return "$line:$column"
    }

    override operator fun compareTo(other: ProgramPosition): Int {
        val lineComparison = this.line.compareTo(other.line)
        if (lineComparison != 0) {
            return lineComparison
        }

        return this.column.compareTo(other.column)
    }
}

/**
 * A line in a program. Counting starts at 1.
 *
 * @throws IllegalArgumentException If value is less than 1.
 */
@JvmInline
value class ProgramLine(val value: Int) : Comparable<ProgramLine> {
    init {
        require(value >= 1) { "Line must be at least 1." }
    }

    override fun toString(): String {
        return value.toString()
    }

    override operator fun compareTo(other: ProgramLine): Int {
        return this.value.compareTo(other.value)
    }
}

/**
 * A column in a program. Counting starts at 1.
 *
 * @throws IllegalArgumentException If value is less than 1.
 */
@JvmInline
value class ProgramColumn(val value: Int) : Comparable<ProgramColumn> {
    init {
        require(value >= 1) { "Column must be at least 1." }
    }

    override fun toString(): String {
        return value.toString()
    }

    override operator fun compareTo(other: ProgramColumn): Int {
        return this.value.compareTo(other.value)
    }
}
