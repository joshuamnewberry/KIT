package edu.gvsu.cis.kit

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform