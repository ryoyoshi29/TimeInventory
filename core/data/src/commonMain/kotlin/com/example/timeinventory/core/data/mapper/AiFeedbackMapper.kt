package com.example.timeinventory.core.data.mapper

import com.example.timeinventory.core.database.entity.AiFeedbackEntity
import com.example.timeinventory.core.model.AiFeedback
import com.example.timeinventory.core.model.KptElement
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun AiFeedbackEntity.toDomainModel(): AiFeedback {
    return AiFeedback(
        id = Uuid.parse(id),
        targetDate = targetDate,
        summary = summary,
        keep = KptElement(
            title = keepTitle,
            description = keepDescription,
        ),
        problem = KptElement(
            title = problemTitle,
            description = problemDescription,
        ),
        tryAction = KptElement(
            title = tryActionTitle,
            description = tryActionDescription,
        ),
    )
}

@OptIn(ExperimentalUuidApi::class)
fun AiFeedback.toEntity(): AiFeedbackEntity {
    return AiFeedbackEntity(
        id = id.toString(),
        targetDate = targetDate,
        summary = summary,
        keepTitle = keep.title,
        keepDescription = keep.description,
        problemTitle = problem.title,
        problemDescription = problem.description,
        tryActionTitle = tryAction.title,
        tryActionDescription = tryAction.description,
    )
}
