import java.io.FileOutputStream
import java.io.IOException
import java.lang.Integer.min
import java.nio.charset.StandardCharsets
import java.time.*
import java.time.format.DateTimeFormatter


class Protocol (val binaryRepresentation: ByteArray) {

    companion object {
        fun from(user: String, text: String, file: ByteArray = ByteArray(0)): Protocol {
            var userBinary = user.toByteArray(StandardCharsets.UTF_8)
            var timeBinary = LocalDateTime.now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                .toString().toByteArray(StandardCharsets.UTF_8)
            var textBinary = text.toByteArray(StandardCharsets.UTF_8)
            val userSize = byteArrayOf(min(userBinary.size, 255).toByte())
            userBinary = userBinary.copyOf(256)
            val timeSize = byteArrayOf(min(timeBinary.size, 255).toByte())
            timeBinary = timeBinary.copyOf(256)
            val textSize = byteArrayOf(
                ((textBinary.size and 0xff0000) / 65536).toByte(),
                ((textBinary.size and 0x00ff00) / 256).toByte(),
                (textBinary.size and 0x0000ff).toByte()
            )
            textBinary = textBinary.copyOf(16777216)
            val fileSize = byteArrayOf(
                ((file.size and 0xff0000) / 65536).toByte(),
                ((file.size and 0x00ff00) / 256).toByte(),
                (file.size and 0x0000ff).toByte()
            )
            val fileBinary = file.copyOf(16777216)
            /*println((userSize + timeSize + textSize + fileSize + userBinary + timeBinary + textBinary + fileBinary)
                .size)*/
            return Protocol(
                (userSize + timeSize + textSize + fileSize +
                        userBinary + timeBinary + textBinary + fileBinary)
            )
        }
    }

    fun getText(): String {
        val size = binaryRepresentation.slice(2..4).toByteArray().toUByteArray()
        var textSize = size[0].toInt()
        for (i in 1..2) {
            textSize *= 256
            textSize += size[i].toInt()
        }
        val pointer = 520
        return binaryRepresentation.slice(pointer until textSize + pointer)
            .toByteArray()
            .decodeToString()
    }

    fun getSender(): String {
        val size = binaryRepresentation.first().toUByte().toInt()
        return binaryRepresentation.slice(8 until size + 8).toByteArray()
            .decodeToString()
    }

    fun saveFile(path: String) {
        val size = binaryRepresentation.slice(5..7).toByteArray().toUByteArray()
        var fileSize = size[0].toInt()
        for (i in 1..2) {
            fileSize *= 256
            fileSize += size[i].toInt()
        }
        val pointer = 16777736
        val file = binaryRepresentation.slice(pointer until fileSize+pointer).toByteArray()
        val fileName = getText().substringAfterLast("--f ")
        val fos = FileOutputStream(path + fileName)
        fos.write(file, 0, fileSize)
        fos.close()
    }

    @Throws(IOException::class)
    override fun toString() : String {
        val sizes = binaryRepresentation.slice(0..7).toByteArray().toUByteArray()
        val userSize = sizes[0].toInt()
        val timeSize = sizes[1].toInt()
        var textSize = sizes[2].toInt()
        for (i in 3..4) {
            textSize *= 8
            textSize += sizes[i].toInt()
        }
        var pointer = 8
        val name = binaryRepresentation.slice(pointer until userSize+pointer).toByteArray()
            .decodeToString()
        //println("name is $name")
        pointer = 264
        val time = LocalDateTime.parse(
            binaryRepresentation.slice(pointer until timeSize+pointer)
            .toByteArray()
            .decodeToString(),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )
            .plusHours(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("Z")).toLong() / 100)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        pointer = 520
        val text = binaryRepresentation.slice(pointer until textSize+pointer)
            .toByteArray()
            .decodeToString()
        pointer = 16777736
        return "<$time> [$name] $text"
    }
}