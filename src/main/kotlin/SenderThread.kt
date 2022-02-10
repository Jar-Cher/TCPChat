
import java.io.*
import java.net.Socket


internal class SenderThread(private val clientSocket: Socket, val client: Client) : Thread() {
    val outcoming = BufferedOutputStream(clientSocket.getOutputStream())
    val incoming = BufferedReader(InputStreamReader(System.`in`))
    var i = 0

    override fun run() {

        println("Hi! Please, write your username (no whitespaces allowed): ")
        var username = "USERNAME_ERROR"

        while(!client.isRegistered) {
            while(!client.isRegistrationInProgress && !client.isRegistered) {
                try {
                    username = incoming.readLine() // сообщения с консоли
                    client.isRegistrationInProgress = true
                    val protocol = Protocol.from("Server", "--u $username").binaryRepresentation
                    outcoming.write(protocol) // отправляем на сервер
                    outcoming.flush()
                    //println()
                } catch (e: IOException) {
                    println(e.message)
                }
                //println(i)
                i++
            }
            print("")
        }
        //println("Made here!")

        while (true) {
            var userInput: String

            try {
                //println("Ready to read!")
                userInput = incoming.readLine() // сообщения с консоли
                //println()
                when {
                    userInput.matches(Regex("""^.* --f .+$""")) -> {
                        val fileName = userInput.substringAfterLast("--f ")
                        val file = File(fileName)
                        val fileStream = FileInputStream(file)
                        val protocol = Protocol.from(username, userInput, fileStream.readAllBytes()).binaryRepresentation
                        fileStream.close()
                        outcoming.write(protocol) // отправляем на сервер
                        outcoming.flush()
                    }
                    userInput.matches(Regex("""^--e$""")) -> {
                        val protocol = Protocol.from(username, userInput).binaryRepresentation
                        outcoming.write(protocol) // отправляем на сервер
                        outcoming.flush()
                        client.disconnect()
                    }
                    else -> {
                        val protocol = Protocol.from(username, userInput).binaryRepresentation
                        outcoming.write(protocol) // отправляем на сервер
                        outcoming.flush()
                    }
                }
                //out.close()
            } catch (e: IOException) {
                println(e.message)
            }
        }
    }
}

