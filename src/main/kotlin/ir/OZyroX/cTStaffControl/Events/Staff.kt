package ir.OZyroX.cTStaffControl.Events

import java.time.LocalDateTime

data class Staff(
    val name: String,
    val uuid: String,
    val rank: String,
    val prefix: String,
    val weight: Int,
    val lastLogin: String
)
