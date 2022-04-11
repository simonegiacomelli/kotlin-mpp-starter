package time

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Created by Simone on 01/12/2015.
 */
class BackoffTest {
    private lateinit var target: Backoff
    private val maxDelay: Long = 18000

    @BeforeTest
    fun setUp() {
        target = Backoff(5000, maxDelay)
    }

    @Test
    fun testNext() {
        assertEquals(target.next(), 5000L)
        assertEquals(target.next(), 5000L * 2)
    }

    @Test
    fun testLimit() {
        target.next()
        target.next()
        target.next()
        assertEquals(target.next(), maxDelay)
    }

    @Test
    fun testIteration() {
        assertEquals(target.iteration(), 0L)
        target.next()
        assertEquals(target.iteration(), 1L)
        target.next()
        assertEquals(target.iteration(), 2L)
    }
}