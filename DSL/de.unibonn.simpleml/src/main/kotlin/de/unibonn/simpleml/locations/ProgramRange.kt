package de.unibonn.simpleml.locations

/**
 * A range in a program from a start to an end position with some length.
 *
 * @see ProgramPosition
 * @see ProgramRangeLength
 */
data class ProgramRange(val start: ProgramPosition, val end: ProgramPosition, val length: ProgramRangeLength) {

    companion object {

        @JvmStatic
        fun fromInts(startLine: Int, startColumn: Int, endLine: Int, endColumn: Int, length: Int): ProgramRange {
            return ProgramRange(
                ProgramPosition.fromInts(startLine, startColumn),
                ProgramPosition.fromInts(endLine, endColumn),
                ProgramRangeLength(length)
            )
        }
    }

    override fun toString(): String {
        return "$start .. $end ($length)"
    }
}

/**
 * The number of characters in a program range. This value must be non-negative.
 *
 * @throws IllegalArgumentException If value is negative.
 */
@JvmInline
value class ProgramRangeLength(val value: Int) {
    init {
        require(value >= 0) { "Length must be at least 0." }
    }

    override fun toString(): String {
        val chars = if (value == 1) "char" else "chars"
        return "$value $chars"
    }
}
