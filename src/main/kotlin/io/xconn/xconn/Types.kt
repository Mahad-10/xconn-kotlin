package io.xconn.xconn

import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.websocket.close
import io.xconn.wampproto.SessionDetails
import io.xconn.wampproto.messages.Message
import io.xconn.wampproto.serializers.Serializer

interface IBaseSession {
    fun id(): Long

    fun realm(): String

    fun authid(): String

    fun authrole(): String

    fun serializer(): Serializer

    suspend fun send(data: Any)

    suspend fun receive(): Any

    suspend fun sendMessage(msg: Message)

    suspend fun receiveMessage(): Message

    suspend fun close()
}

class BaseSession(
    private val webSocketSession: DefaultClientWebSocketSession,
    private val sessionDetails: SessionDetails,
    private val serializer: Serializer,
) : IBaseSession {
    override fun id(): Long {
        return sessionDetails.sessionID
    }

    override fun realm(): String {
        return sessionDetails.realm
    }

    override fun authid(): String {
        return sessionDetails.authid
    }

    override fun authrole(): String {
        return sessionDetails.authrole
    }

    override fun serializer(): Serializer {
        return serializer
    }

    override suspend fun send(data: Any) {
        webSocketSession.sendFrame(data)
    }

    override suspend fun sendMessage(msg: Message) {
        val serializedData = serializer.serialize(msg)
        send(serializedData)
    }

    override suspend fun receive(): Any {
        val frame = webSocketSession.incoming.receive()
        return receiveFrame(frame)
    }

    override suspend fun receiveMessage(): Message {
        return serializer.deserialize(receive())
    }

    override suspend fun close() {
        webSocketSession.close()
    }
}
