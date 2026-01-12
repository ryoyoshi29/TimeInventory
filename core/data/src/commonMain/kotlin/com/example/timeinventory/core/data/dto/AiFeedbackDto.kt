package com.example.timeinventory.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AiFeedbackDto(
    val summary: String,
    val keep: KptItemDto,
    val problem: KptItemDto,
    @SerialName("tryAction")
    val tryAction: KptItemDto
)

@Serializable
data class KptItemDto(
    val title: String,
    val description: String
)