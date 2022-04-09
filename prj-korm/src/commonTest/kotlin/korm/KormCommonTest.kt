package korm

import kotlin.test.Test


class KormCommonTest {

    @Test
    fun test_query() {

        class User1(val id: UInt)
        class User2(val id: UInt, val name: String)
        class User3(val id: UInt, val name: String, val age: Int)
//
//        val user1: User1 = korm(::User1)
//        val user2: User2 = korm(::User2)
//        val user3: User3 = korm(::User3)


    }


}
