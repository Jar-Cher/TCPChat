import java.io.*
import java.net.ServerSocket
import java.util.*
import kotlin.collections.ArrayList


class Server {

    companion object {
        const val PORT = 8081
        internal var serverList: LinkedList<ServerThread?> = LinkedList<ServerThread?>() // список всех нитей
        internal val usernames = mutableSetOf("Server")
        private var server: ServerSocket? = null // серверсокет
        private var incoming: BufferedReader? = null // поток чтения из сокета
        private var outcoming: BufferedWriter? = null // поток записи в сокет

        @JvmStatic
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            val server = ServerSocket(PORT)
            server.use { server ->
                while (true) {
                    // Блокируется до возникновения нового соединения:
                    val socket = server.accept()
                    try {
                        serverList.add(ServerThread(socket)) // добавить новое соединенние в список
                    } catch (e: IOException) {
                        // Если завершится неудачей, закрывается сокет,
                        // в противном случае, нить закроет его при завершении работы:
                        println(e.message)
                        socket.close()
                    }
                }
            }
        }
    }
}