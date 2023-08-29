package net.leanix.vsm.gitlab.broker.connector.domain

import com.fasterxml.jackson.annotation.JsonValue

enum class CommandEventAction(@JsonValue val action: String) {
    FAILED("failed"),
    FINISHED("finished");

    companion object {
        fun from(action: String?): CommandEventAction? = CommandEventAction.values()
            .firstOrNull { it.action == action }
    }
}
