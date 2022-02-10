import java.io.*
import java.net.Socket

internal class ServerThread(  // сокет, через который сервер общается с клиентом,
    private val socket: Socket
) : Thread() {
    companion object {
        var totalConnections = 0
    }

    //private val ut = BufferedOutputStream(socket.getOutputStream());
    var connectionNo: Int = 0
    val incoming = socket.getInputStream()
    val outcoming = BufferedOutputStream(socket.getOutputStream())

    override fun run() {
        try {
            while (true) {
                val incomingData = Protocol(incoming.readNBytes(33554952))
                //println(incomingData.binaryRepresentation.size)
                println(incomingData.toString())

                // Читаем чужую переписку
                val user = incomingData.getSender()
                val message = incomingData.getText()
                // --u
                if ((user=="Server") && (message.matches(Regex("""^--u .+$""")))) {
                    val newUsername = message.split(" ").component2()
                    if (Server.usernames.contains(newUsername)) {
                        send(Protocol.from("Server", "This username is in use, please choose another: "))
                    }
                    else {
                        Server.usernames.add(newUsername)
                        send(Protocol.from("Server", "Registration successful"))
                        for (connection in Server.serverList) {
                            connection?.send(Protocol.from("Server", "$newUsername joins the chat!"))
                        }
                    }
                }
                // --e
                else if (message.matches(Regex("""^--e$"""))) {
                    Server.usernames.remove(user)
                    for (connection in Server.serverList) {
                        connection?.send(Protocol.from("Server", "$user has left the chat!"))
                    }
                    interrupt()
                }
                else {
                    for (connection in Server.serverList) {
                        connection?.send(incomingData)
                    }
                }
            }
        } catch (e: IOException) {
            println(e.message)
        }
    }

    private fun send(message: Protocol) {
        try {
            outcoming.write(message.binaryRepresentation)
            outcoming.flush()
            //outcoming.close()
        } catch (e: IOException) {
            println(e.message)
        }
    }

    init {
        start() // вызываем run()
        connectionNo = totalConnections
        totalConnections++
        println("started")
    }
}