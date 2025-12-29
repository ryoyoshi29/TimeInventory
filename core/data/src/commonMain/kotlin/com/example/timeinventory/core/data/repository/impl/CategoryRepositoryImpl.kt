package com.example.timeinventory.core.data.repository.impl

import com.example.timeinventory.core.data.mapper.toDomainModel
import com.example.timeinventory.core.data.mapper.toEntity
import com.example.timeinventory.core.data.repository.CategoryRepository
import com.example.timeinventory.core.database.dao.CategoryDao
import com.example.timeinventory.core.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao,
) : CategoryRepository {

    override fun getCategoriesStream(): Flow<List<Category>> {
        return categoryDao.getAllAsFlow().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun initializeDefaultCategories(categories: List<Category>) {
        categories.forEach { category ->
            categoryDao.upsert(category.toEntity())
        }
    }

    override suspend fun upsertCategory(category: Category) {
        categoryDao.upsert(category.toEntity())
    }

    override suspend fun deleteCategory(id: Uuid) {
        categoryDao.deleteById(id.toString())
    }

    override suspend fun getCategoryById(id: Uuid): Category? {
        return categoryDao.getById(id.toString())?.toDomainModel()
    }
}
