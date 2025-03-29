package io.duhan.security

import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.Encoders
import org.junit.jupiter.api.Test

class Base64Test {
    @Test
    fun encode() {
        val text = "Hello world"
        val byteArray = text.toByteArray()
        val result = Encoders.BASE64.encode(byteArray)
        println(text)
        println(byteArray)
        println(result)
    }

    @Test
    fun decode() {
//        Hello world
//        [B@4b34fff9
//        SGVsbG8gd29ybGQ=
        val decodedByteArray = Decoders.BASE64.decode("SGVsbG8gd29ybGQ=")
        val decodedString = String(decodedByteArray)
        println(decodedByteArray)
        println(decodedString)
    }
}
