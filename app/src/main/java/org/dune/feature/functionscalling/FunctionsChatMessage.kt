package org.dune.feature.functionscalling

import java.util.*

enum class Participant {
    USER, MODEL, ERROR
}

data class FunctionsChatMessage(
    val id: String = UUID.randomUUID().toString(),
    var text: String = "",
    val participant: Participant = Participant.USER,
    var isPending: Boolean = false
)