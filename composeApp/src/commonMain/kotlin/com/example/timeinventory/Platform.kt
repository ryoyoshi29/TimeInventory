package com.example.timeinventory

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform