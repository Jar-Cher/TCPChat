import java.io.*
import java.net.ServerSocket
import java.util.*
import kotlin.collections.ArrayList


class NBServer {

    companion object {
        const val PORT = 8081
        internal var serverList: LinkedList<ServerThread?> = LinkedList<ServerThread?>()
        internal val usernames = mutableSetOf("Server")

        @JvmStatic
        @Throws(IOException::class)
        fun main(args: Array<String>) {
            val server = ServerSocket(PORT)
            server.use { server ->
                while (true) {
                    val socket = server.accept()
                    try {
                        serverList.add(ServerThread(socket))
                    } catch (e: IOException) {
                        println(e.message)
                        socket.close()
                    }
                }
            }
        }
    }
}