import java.io.*
import java.net.Socket

public class RecieverThread(private val clientSocket: Socket, val client: Client) : Thread() {
    val incoming = BufferedInputStream(clientSocket.getInputStream())
    override fun run() {
        try {
            while (true) {
                val input = Protocol(incoming.readNBytes(33554952))
                if (input.getSender() == "Server") {
                    if (input.getText() == "Registration successful") {
                        client.isRegistered = true
                        client.isRegistrationInProgress = false
                    } else if (input.getText() == "This username is in use, please choose another: ") {
                        client.isRegistrationInProgress = false
                    }
                }
                println(input.toString())
                if (input.getText().matches(Regex("""^.* --f .+$"""))) {
                    input.saveFile(System.getProperty("user.dir") + "\\ReceivedFiles\\")
                    println("File received, saved at: " + System.getProperty("user.dir") + "\\ReceivedFiles")
                }
            }
        } catch (e: IOException) {
            println(e.message)
        }
    }
}