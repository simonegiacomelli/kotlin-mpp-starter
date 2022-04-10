package database.exposed.expansion

import database.exposed.DatabaseTest
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.test.Test
import kotlin.test.assertEquals

class LocalDateTimeTest : DatabaseTest() {

    override fun setupExposed() {
        tables(aaa_test)
    }

    @Test
    fun write() {
        val datetime1 = kotlinx.datetime.LocalDateTime(2021, 1, 18, 14, 30, 31, nanosecond = 123456000)
        val datetimeExpected = kotlinx.datetime.LocalDateTime(2021, 1, 18, 14, 30, 31)
        transaction {
            aaa_test.insert {
                it[id] = 1L
                it[dt] = datetime1
            }
        }

        transaction {
            val rec = aaa_test.selectAll().single()
            assertEquals(1L, rec[aaa_test.id].value)
            assertEquals(datetimeExpected, rec[aaa_test.dt])
        }
    }

    @Test
    fun writeHiRes() {
        val datetime1 = kotlinx.datetime.LocalDateTime(2021, 1, 18, 14, 30, 31, nanosecond = 123456000)
        transaction {
            aaa_test.insert {
                it[id] = 1L
                it[dt_hi_res] = datetime1
            }
        }

        transaction {
            val rec = aaa_test.selectAll().single()
            assertEquals(datetime1, rec[aaa_test.dt_hi_res])
        }
    }

    @Test
    fun updateHiRes() {
        val datetime1 = kotlinx.datetime.LocalDateTime(2021, 1, 18, 14, 30, 31, nanosecond = 123456000)
        transaction {
            aaa_test.insert {
                it[id] = 1L
            }
        }

        transaction {
            aaa_test.update({ aaa_test.id.eq(1L) }) {
                it[dt_hi_res] = datetime1
            }
        }

        transaction {
            val rec = aaa_test.selectAll().single()
            assertEquals(datetime1, rec[aaa_test.dt_hi_res])
        }
    }


}


object aaa_test : LongIdTable() {
    val dt = datetime("dt").nullable()
    val dt_hi_res = datetime("dt_hi_res", highResolution = true).nullable()
//    override val primaryKey = PrimaryKey(id)
}

