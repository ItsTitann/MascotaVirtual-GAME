package com.example.animation.data.model

data class PetData(
    val id: String = "",
    val name: String = "",
    val level: Int = 1,
    val health: Int = 100,
    val energy: Int = 100,
    val funLevel: Int = 100,
    val hunger: Int = 100,
    val hygiene: Int = 100,
    val xp: Int = 0,
    val sleeping: Boolean = false,
    val lastUpdate: Long = System.currentTimeMillis()
)
