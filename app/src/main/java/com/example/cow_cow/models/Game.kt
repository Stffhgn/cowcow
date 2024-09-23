package com.example.cow_cow.models

data class Game(
    var players: MutableList<Player>,
    var isTeamMode: Boolean = false,
    var team: Team? = null
)