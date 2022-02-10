import java.io.*
import java.net.Socket


class Client {
    var isRegistered = false
    var isRegistrationInProgress = false
    lateinit var recieverThread: Thread
    lateinit var senderThread: Thread
    lateinit var clientSocket: Socket

    fun connect() {
        try {
            clientSocket = Socket("localhost", 8081)
            recieverThread = RecieverThread(clientSocket!!, this)
            senderThread = SenderThread(clientSocket!!, this)
            recieverThread.start()
            senderThread.start()
        } catch (e: IOException) {
            println(e.message)
        }
    }

    fun disconnect() {
        try {
            senderThread.interrupt()
            recieverThread.interrupt()
            clientSocket.close()
            System.exit(0)
        } catch (ignored: IOException) {
        }
    }
}

class ClientInitializer() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val newClient = Client()
            newClient.connect()
        }
    }
}