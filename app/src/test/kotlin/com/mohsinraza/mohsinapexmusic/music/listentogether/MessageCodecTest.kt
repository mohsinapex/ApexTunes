package com.mohsinraza.mohsinapexmusic.music.listentogether

import com.google.protobuf.ByteString
import com.mohsinraza.mohsinapexmusic.music.listentogether.proto.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageCodecTest {
    private val codec = MessageCodec(compressionEnabled = true)

    @Test
    fun `playback timing fields survive a protobuf round trip`() {
        val action =
            PlaybackActionPayload(
                action = PlaybackActions.PLAY,
                trackId = "track",
                position = 1_234L,
                serverTime = 9_000L,
                revision = 12L,
                capturedAtServerTime = 8_950L,
            )

        val (type, payload) = codec.decode(codec.encode(MessageTypes.PLAYBACK_ACTION, action))
        val decoded = codec.decodePayload(MessageTypes.SYNC_PLAYBACK, payload) as PlaybackActionPayload

        assertEquals(MessageTypes.PLAYBACK_ACTION, type)
        assertEquals(action.action, decoded.action)
        assertEquals(action.trackId, decoded.trackId)
        assertEquals(action.position, decoded.position)
        assertEquals(action.serverTime, decoded.serverTime)
        assertEquals(action.revision, decoded.revision)
        assertEquals(action.capturedAtServerTime, decoded.capturedAtServerTime)
    }

    @Test
    fun `timestamped ping is encoded and pong is decoded`() {
        val ping = PingPayload(clientTime = 1_000L, sequence = 3L)
        val (_, pingBytes) = codec.decode(codec.encode(MessageTypes.PING, ping))
        val encodedPing = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PingPayload.parseFrom(pingBytes)
        assertEquals(1_000L, encodedPing.getClientTime())
        assertEquals(3L, encodedPing.getSequence())

        val pong =
            com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PongPayload
                .newBuilder()
                .setClientTime(1_000L)
                .setServerReceiveTime(10_000L)
                .setServerSendTime(10_001L)
                .setSequence(3L)
                .build()
        val envelope =
            Envelope
                .newBuilder()
                .setType(MessageTypes.PONG)
                .setPayload(ByteString.copyFrom(pong.toByteArray()))
                .build()
        val (type, pongBytes) = codec.decode(envelope.toByteArray())
        val decoded = codec.decodePayload(type, pongBytes) as PongPayload

        assertEquals(PongPayload(1_000L, 10_000L, 10_001L, 3L), decoded)
        assertTrue(decoded.serverSendTime >= decoded.serverReceiveTime)
    }
}
