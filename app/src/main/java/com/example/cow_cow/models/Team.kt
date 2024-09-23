package com.example.cow_cow.models

data class Team(
    var members: MutableList<Player>,
    var teamScore: Int = 0
)