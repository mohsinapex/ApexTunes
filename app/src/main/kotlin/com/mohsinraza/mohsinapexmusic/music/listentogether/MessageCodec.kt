/**
 * ApexTunes Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.mohsinraza.mohsinapexmusic.music.listentogether

import com.google.protobuf.MessageLite
import com.mohsinraza.mohsinapexmusic.music.listentogether.proto.*
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * Codec for encoding and decoding messages using Protocol Buffers
 */
class MessageCodec(
    var compressionEnabled: Boolean = false
) {
    companion object {
        private const val TAG = "MessageCodec"
        private const val COMPRESSION_THRESHOLD = 100 // Only compress if > 100 bytes
    }
    
    /**
     * Encode a message using Protocol Buffers
     */
    fun encode(msgType: String, payload: Any?): ByteArray {
        return encodeProtobuf(msgType, payload)
    }
    
    /**
     * Decode a protobuf message
     */
    fun decode(data: ByteArray): Pair<String, ByteArray> {
        return decodeProtobuf(data)
    }
    
    /**
     * Encode message using Protocol Buffers
     */
    private fun encodeProtobuf(msgType: String, payload: Any?): ByteArray {
        var payloadBytes = byteArrayOf()
        var compressed = false
        
        if (payload != null) {
            val protoMsg = toProtoMessage(payload)
            payloadBytes = protoMsg.toByteArray()
            
            // Compress if enabled and payload is large enough
            if (compressionEnabled && payloadBytes.size > COMPRESSION_THRESHOLD) {
                val compressedBytes = compressData(payloadBytes)
                if (compressedBytes.size < payloadBytes.size) {
                    payloadBytes = compressedBytes
                    compressed = true
                }
            }
        }
        
        val envelope = Envelope.newBuilder()
            .setType(msgType)
            .setPayload(com.google.protobuf.ByteString.copyFrom(payloadBytes))
            .setCompressed(compressed)
            .build()
        
        return envelope.toByteArray()
    }
    
    /**
     * Decode protobuf message
     */
    private fun decodeProtobuf(data: ByteArray): Pair<String, ByteArray> {
        val envelope = Envelope.parseFrom(data)
        
        var payloadBytes = envelope.payload.toByteArray()
        
        if (envelope.compressed) {
            payloadBytes = decompressData(payloadBytes) ?: payloadBytes
        }
        
        return Pair(envelope.type, payloadBytes)
    }
    
    /**
     * Compress data using GZIP
     */
    private fun compressData(data: ByteArray): ByteArray {
        val outputStream = ByteArrayOutputStream()
        GZIPOutputStream(outputStream).use { gzip ->
            gzip.write(data)
        }
        return outputStream.toByteArray()
    }
    
    /**
     * Decompress GZIP data
     */
    private fun decompressData(data: ByteArray): ByteArray? {
        return try {
            val inputStream = ByteArrayInputStream(data)
            GZIPInputStream(inputStream).use { gzip ->
                gzip.readBytes()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to decompress data")
            null
        }
    }
    
    /**
     * Convert Kotlin objects to protobuf messages
     */
    private fun toProtoMessage(payload: Any): MessageLite {
        return when (payload) {
            is CreateRoomPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.CreateRoomPayload.newBuilder()
                .setUsername(payload.username)
                .build()
            is JoinRoomPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.JoinRoomPayload.newBuilder()
                .setRoomCode(payload.roomCode)
                .setUsername(payload.username)
                .build()
            is ApproveJoinPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.ApproveJoinPayload.newBuilder()
                .setUserId(payload.userId)
                .build()
            is RejectJoinPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.RejectJoinPayload.newBuilder()
                .setUserId(payload.userId)
                .setReason(payload.reason ?: "")
                .build()
            is PlaybackActionPayload -> {
                val builder = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PlaybackActionPayload.newBuilder()
                    .setAction(payload.action)
                    .setPosition(payload.position ?: 0)
                    .setInsertNext(payload.insertNext ?: false)
                    .setVolume(payload.volume ?: 1f)
                    .setServerTime(payload.serverTime ?: 0)
                    .setRevision(payload.revision)
                    .setCapturedAtServerTime(payload.capturedAtServerTime ?: 0)
                
                payload.trackId?.let { builder.setTrackId(it) }
                payload.trackInfo?.let { builder.setTrackInfo(trackInfoToProto(it)) }
                payload.queueTitle?.let { builder.setQueueTitle(it) }
                payload.queue?.forEach { track ->
                    builder.addQueue(trackInfoToProto(track))
                }
                
                builder.build()
            }
            is PingPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PingPayload.newBuilder()
                .setClientTime(payload.clientTime)
                .setSequence(payload.sequence)
                .build()
            is BufferReadyPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.BufferReadyPayload.newBuilder()
                .setTrackId(payload.trackId)
                .build()
            is KickUserPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.KickUserPayload.newBuilder()
                .setUserId(payload.userId)
                .setReason(payload.reason ?: "")
                .build()
            is SuggestTrackPayload -> {
                val builder = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.SuggestTrackPayload.newBuilder()
                payload.trackInfo.let { builder.setTrackInfo(trackInfoToProto(it)) }
                builder.build()
            }
            is ApproveSuggestionPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.ApproveSuggestionPayload.newBuilder()
                .setSuggestionId(payload.suggestionId)
                .build()
            is RejectSuggestionPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.RejectSuggestionPayload.newBuilder()
                .setSuggestionId(payload.suggestionId)
                .setReason(payload.reason ?: "")
                .build()
            is ReconnectPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.ReconnectPayload.newBuilder()
                .setSessionToken(payload.sessionToken)
                .build()
            is TransferHostPayload -> com.mohsinraza.mohsinapexmusic.music.listentogether.proto.TransferHostPayload.newBuilder()
                .setNewHostId(payload.newHostId)
                .build()
            else -> throw IllegalArgumentException("Unsupported payload type: ${payload::class.simpleName}")
        }
    }
    
    /**
     * Decode protobuf payload to Kotlin objects
     */
    fun decodePayload(msgType: String, payloadBytes: ByteArray): Any? {
        if (payloadBytes.isEmpty()) return null
        
        return decodeProtobufPayload(msgType, payloadBytes)
    }
    
    /**
     * Decode protobuf payload
     */
    private fun decodeProtobufPayload(msgType: String, payloadBytes: ByteArray): Any? {
        return when (msgType) {
            MessageTypes.ROOM_CREATED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.RoomCreatedPayload.parseFrom(payloadBytes)
                RoomCreatedPayload(pb.getRoomCode(), pb.getUserId(), pb.getSessionToken())
            }
            MessageTypes.JOIN_REQUEST -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.JoinRequestPayload.parseFrom(payloadBytes)
                JoinRequestPayload(pb.getUserId(), pb.getUsername())
            }
            MessageTypes.JOIN_APPROVED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.JoinApprovedPayload.parseFrom(payloadBytes)
                JoinApprovedPayload(
                    pb.getRoomCode(),
                    pb.getUserId(),
                    pb.getSessionToken(),
                    protoToRoomState(pb.getState())
                )
            }
            MessageTypes.JOIN_REJECTED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.JoinRejectedPayload.parseFrom(payloadBytes)
                JoinRejectedPayload(pb.getReason())
            }
            MessageTypes.USER_JOINED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.UserJoinedPayload.parseFrom(payloadBytes)
                UserJoinedPayload(pb.getUserId(), pb.getUsername())
            }
            MessageTypes.USER_LEFT -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.UserLeftPayload.parseFrom(payloadBytes)
                UserLeftPayload(pb.getUserId(), pb.getUsername())
            }
            MessageTypes.SYNC_PLAYBACK -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PlaybackActionPayload.parseFrom(payloadBytes)
                val positionForAction =
                    pb.getPosition().takeIf {
                        it != 0L ||
                            pb.getAction() == PlaybackActions.PLAY ||
                            pb.getAction() == PlaybackActions.PAUSE ||
                            pb.getAction() == PlaybackActions.SEEK
                    }
                PlaybackActionPayload(
                    action = pb.getAction(),
                    trackId = pb.getTrackId().takeIf { it.isNotEmpty() },
                    position = positionForAction,
                    trackInfo = if (pb.hasTrackInfo()) protoToTrackInfo(pb.getTrackInfo()) else null,
                    insertNext = pb.getInsertNext().takeIf { it },
                    queue = pb.getQueueList().map { protoToTrackInfo(it) },
                    queueTitle = pb.getQueueTitle().takeIf { it.isNotEmpty() },
                    volume = pb.getVolume().takeIf { pb.getAction() == PlaybackActions.SET_VOLUME },
                    serverTime = pb.getServerTime().takeIf { it > 0 },
                    revision = pb.getRevision(),
                    capturedAtServerTime = pb.getCapturedAtServerTime().takeIf { it > 0 },
                )
            }
            MessageTypes.BUFFER_WAIT -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.BufferWaitPayload.parseFrom(payloadBytes)
                BufferWaitPayload(pb.getTrackId(), pb.getWaitingForList())
            }
            MessageTypes.BUFFER_COMPLETE -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.BufferCompletePayload.parseFrom(payloadBytes)
                BufferCompletePayload(pb.getTrackId())
            }
            MessageTypes.ERROR -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.ErrorPayload.parseFrom(payloadBytes)
                ErrorPayload(pb.getCode(), pb.getMessage())
            }
            MessageTypes.HOST_CHANGED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.HostChangedPayload.parseFrom(payloadBytes)
                HostChangedPayload(pb.getNewHostId(), pb.getNewHostName())
            }
            MessageTypes.KICKED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.KickedPayload.parseFrom(payloadBytes)
                KickedPayload(pb.getReason())
            }
            MessageTypes.SYNC_STATE -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.SyncStatePayload.parseFrom(payloadBytes)
                SyncStatePayload(
                    currentTrack = if (pb.hasCurrentTrack()) protoToTrackInfo(pb.getCurrentTrack()) else null,
                    isPlaying = pb.getIsPlaying(),
                    position = pb.getPosition(),
                    lastUpdate = pb.getLastUpdate(),
                    queue = pb.getQueueList().map { protoToTrackInfo(it) },
                    volume = pb.getVolume(),
                    revision = pb.getRevision(),
                )
            }
            MessageTypes.PONG -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.PongPayload.parseFrom(payloadBytes)
                PongPayload(pb.getClientTime(), pb.getServerReceiveTime(), pb.getServerSendTime(), pb.getSequence())
            }
            MessageTypes.RECONNECTED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.ReconnectedPayload.parseFrom(payloadBytes)
                ReconnectedPayload(
                    pb.getRoomCode(),
                    pb.getUserId(),
                    protoToRoomState(pb.getState()),
                    pb.getIsHost()
                )
            }
            MessageTypes.USER_RECONNECTED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.UserReconnectedPayload.parseFrom(payloadBytes)
                UserReconnectedPayload(pb.getUserId(), pb.getUsername())
            }
            MessageTypes.USER_DISCONNECTED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.UserDisconnectedPayload.parseFrom(payloadBytes)
                UserDisconnectedPayload(pb.getUserId(), pb.getUsername())
            }
            MessageTypes.SUGGESTION_RECEIVED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.SuggestionReceivedPayload.parseFrom(payloadBytes)
                SuggestionReceivedPayload(
                    pb.getSuggestionId(),
                    pb.getFromUserId(),
                    pb.getFromUsername(),
                    protoToTrackInfo(pb.getTrackInfo())
                )
            }
            MessageTypes.SUGGESTION_APPROVED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.SuggestionApprovedPayload.parseFrom(payloadBytes)
                SuggestionApprovedPayload(
                    pb.getSuggestionId(),
                    protoToTrackInfo(pb.getTrackInfo())
                )
            }
            MessageTypes.SUGGESTION_REJECTED -> {
                val pb = com.mohsinraza.mohsinapexmusic.music.listentogether.proto.SuggestionRejectedPayload.parseFrom(payloadBytes)
                SuggestionRejectedPayload(pb.getSuggestionId(), pb.getReason().takeIf { it.isNotEmpty() })
            }
            else -> null
        }
    }
    
    // Helper conversion functions
    
    private fun trackInfoToProto(track: TrackInfo): com.mohsinraza.mohsinapexmusic.music.listentogether.proto.TrackInfo {
        return com.mohsinraza.mohsinapexmusic.music.listentogether.proto.TrackInfo.newBuilder()
            .setId(track.id)
            .setTitle(track.title)
            .setArtist(track.artist)
            .setAlbum(track.album ?: "")
            .setDuration(track.duration)
            .setThumbnail(track.thumbnail ?: "")
            .setSuggestedBy(track.suggestedBy ?: "")
            .build()
    }
    
    private fun protoToTrackInfo(proto: com.mohsinraza.mohsinapexmusic.music.listentogether.proto.TrackInfo): TrackInfo {
        return TrackInfo(
            id = proto.getId(),
            title = proto.getTitle(),
            artist = proto.getArtist(),
            album = proto.getAlbum().takeIf { it.isNotEmpty() },
            duration = proto.getDuration(),
            thumbnail = proto.getThumbnail().takeIf { it.isNotEmpty() },
            suggestedBy = proto.getSuggestedBy().takeIf { it.isNotEmpty() }
        )
    }
    
    private fun protoToUserInfo(proto: com.mohsinraza.mohsinapexmusic.music.listentogether.proto.UserInfo): UserInfo {
        return UserInfo(
            userId = proto.getUserId(),
            username = proto.getUsername(),
            isHost = proto.getIsHost(),
            isConnected = proto.getIsConnected()
        )
    }
    
    private fun protoToRoomState(proto: com.mohsinraza.mohsinapexmusic.music.listentogether.proto.RoomState): RoomState {
        return RoomState(
            roomCode = proto.getRoomCode(),
            hostId = proto.getHostId(),
            users = proto.getUsersList().map { protoToUserInfo(it) },
            currentTrack = if (proto.hasCurrentTrack()) protoToTrackInfo(proto.getCurrentTrack()) else null,
            isPlaying = proto.getIsPlaying(),
            position = proto.getPosition(),
            lastUpdate = proto.getLastUpdate(),
            volume = proto.getVolume(),
            queue = proto.getQueueList().map { protoToTrackInfo(it) },
            revision = proto.getRevision(),
        )
    }
}
