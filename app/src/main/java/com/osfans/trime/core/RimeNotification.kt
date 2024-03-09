package com.osfans.trime.core

sealed class RimeNotification<T>(open val value: T) {
    abstract val messageType: MessageType

    data class SchemaNotification(override val value: Value) :
        RimeNotification<SchemaNotification.Value>(value) {
        override val messageType: MessageType
            get() = MessageType.Schema

        data class Value(val schemaId: String, val schemaName: String)

        override fun toString() = "SchemaEvent(schemaId=${value.schemaId}, schemaName=${value.schemaName})"
    }

    data class OptionNotification(override val value: Value) :
        RimeNotification<OptionNotification.Value>(value) {
        override val messageType: MessageType
            get() = MessageType.Option

        data class Value(val option: String, val value: Boolean)

        override fun toString() = "OptionNotification(option=${value.option}, value=${value.value})"
    }

    data class DeployNotification(override val value: String) :
        RimeNotification<String>(value) {
        override val messageType: MessageType
            get() = MessageType.Deploy

        override fun toString() = "DeployNotification(state=$value)"
    }

    data class UnknownNotification(override val value: String) :
        RimeNotification<String>(value) {
        override val messageType: MessageType
            get() = MessageType.Unknown
    }

    enum class MessageType {
        Schema,
        Option,
        Deploy,
        Unknown,
    }

    companion object RimeNotificationHandler {
        @JvmStatic
        fun create(
            type: String,
            value: String,
        ) = when (type) {
            "schema" -> {
                val (id, name) = value.split('/', limit = 2)
                SchemaNotification(SchemaNotification.Value(id, name))
            }
            "option" ->
                OptionNotification(
                    OptionNotification.Value(
                        value.substringAfter('!'),
                        !value.startsWith('!'),
                    ),
                )
            "deploy" -> DeployNotification(value)
            else -> UnknownNotification(value)
        }
    }
}
