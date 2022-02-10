import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestProtocol {
    @Test
    fun testProtocol() {
        val p = Protocol.from("noone", "123456789")
        println(p.toString())
        assertTrue(Regex("""<\d+-\d+-\d+ \d+:\d+:\d+> \[noone] 123456789""").matches(p.toString()))
        /*val a = ByteArray(33554952)
        a[0] = 255.toByte()
        val b = Protocol(a)
        println(b)*/
    }
}