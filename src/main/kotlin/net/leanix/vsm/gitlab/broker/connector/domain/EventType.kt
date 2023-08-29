package net.leanix.vsm.gitlab.broker.connector.domain

import com.fasterxml.jackson.annotation.JsonValue

enum class EventType(@JsonValue val type: String) {
    STATE("state"),
    CHANGE("change"),
    COMMAND("command");

    companion object {
        fun from(type: String?): EventType? = EventType.values().firstOrNull { it.type == type }
    }
}
