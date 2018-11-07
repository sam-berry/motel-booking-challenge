package org.samberry.motelbooking.api

data class Room(
    val roomNumber: String,
    val numberOfBeds: Int,
    val buildingLevel: Int,
    val handicapAccessible: Boolean = false,
    val petFriendly: Boolean = false
)
