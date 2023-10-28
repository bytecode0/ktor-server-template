package com.example.infraestructure.eventbus

import com.example.domain.events.Event
import kotlinx.coroutines.channels.Channel

class EventBus {
    private val bufferCapacity = 10
    private val channel = Channel<Event>(bufferCapacity)

    suspend fun subscribe(handler: suspend (event: Event) -> Unit) {
        for (event in channel) {
            handler(event)
        }
    }

    suspend fun publish(event: Event) {
        channel.send(event)
    }
}