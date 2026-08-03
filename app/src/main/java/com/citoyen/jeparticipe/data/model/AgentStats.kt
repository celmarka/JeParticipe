package com.citoyen.jeparticipe.data.model

data class AgentStats(
    val total: Int = 0,
    val enAttente: Int = 0,
    val enCours: Int = 0,
    val resolus: Int = 0,
    val rejetes: Int = 0
)