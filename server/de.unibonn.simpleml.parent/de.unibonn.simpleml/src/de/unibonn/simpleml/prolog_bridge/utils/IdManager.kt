package de.unibonn.simpleml.prolog_bridge.utils

/**
 * The ID of a Prolog fact.
 */
inline class Id(val value: Int)

/**
 * Handles the mapping of objects, usually EObjects in the Simple-ML AST, to the IDs of Prolog facts.
 */
class IdManager {

    /**
     * Maps an object to an ID.
     */
    private val objToId = mutableMapOf<Any, Id>()

    /**
     * Maps an ID to an object.
     */
    private val idToObj = mutableMapOf<Id, Any>()

    /**
     * The next available ID.
     */
    private var nextId = 0

    /**
     * Assigns the next available ID to the given object unless it already has one and returns the ID for this object.
     */
    fun assignIdIfAbsent(obj: Any): Id {
        if (obj !in objToId) {
            val id = nextId()
            objToId[obj] = id
            idToObj[id] = obj
        }

        return objToId[obj]!!
    }

    /**
     * Returns the next available ID.
     */
    private fun nextId() = Id(nextId++)

    /**
     * Returns the object with the given ID or null if the ID was not assigned yet.
     */
    fun getObjectById(id: Id) = idToObj[id]

    /**
     * Checks if the given object already has an ID.
     */
    fun knowsObject(obj: Any) = obj in objToId

    /**
     * Check if the given ID has already been assigned to some object.
     */
    fun knowsId(id: Id) = id in idToObj
}
