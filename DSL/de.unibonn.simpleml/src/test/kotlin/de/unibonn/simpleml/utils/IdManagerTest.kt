package de.unibonn.simpleml.utils

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class IdManagerTest {

    class TestClass

    private lateinit var idManager: IdManager<Any>

    @BeforeEach
    fun reset() {
        idManager = IdManager()
    }

    @Nested
    inner class AssignIdIfAbsent {

        @Test
        fun `should return a new unique ID for an unseen object`() {
            val firstObject = TestClass()
            val secondObject = TestClass()

            idManager.assignIdIfAbsent(firstObject) shouldNotBe idManager.assignIdIfAbsent(secondObject)
        }

        @Test
        fun `should return the same ID for the same object`() {
            val obj = TestClass()

            idManager.assignIdIfAbsent(obj) shouldBe idManager.assignIdIfAbsent(obj)
        }
    }

    @Nested
    inner class GetObjectById {

        @Test
        fun `should return the object with the given ID if it exists`() {
            val obj = TestClass()
            val id = idManager.assignIdIfAbsent(obj)

            idManager.getObjectById(id) shouldBe obj
        }

        @Test
        fun `should return null if no object with the given ID exists`() {
            idManager.getObjectById(Id<Any>(10)).shouldBeNull()
        }
    }

    @Nested
    inner class KnowsObject {

        @Test
        fun `should return whether an ID has been assigned to this object`() {
            val firstObject = TestClass()
            val secondObject = TestClass()
            idManager.assignIdIfAbsent(firstObject)

            idManager.knowsObject(firstObject).shouldBeTrue()
            idManager.knowsObject(secondObject).shouldBeFalse()
        }
    }

    @Nested
    inner class KnowsId {

        @Test
        fun `should return whether an ID is in use`() {
            val firstObject = TestClass()
            val id = idManager.assignIdIfAbsent(firstObject)

            idManager.knowsId(id).shouldBeTrue()
            idManager.knowsId(Id<Any>(10)).shouldBeFalse()
        }
    }
}
