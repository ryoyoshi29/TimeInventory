package com.example.timeinventory.core.data.mapper

import com.example.timeinventory.core.database.entity.CategoryEntity
import com.example.timeinventory.core.model.Category
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun CategoryEntity.toDomainModel(): Category {
    return Category(
        id = Uuid.parse(id),
        name = name,
        colorArgb = colorArgb,
        sortOrder = sortOrder,
    )
}

@OptIn(ExperimentalUuidApi::class)
fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id.toString(),
        name = name,
        colorArgb = colorArgb,
        sortOrder = sortOrder,
    )
}
