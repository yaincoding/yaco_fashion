package com.yaincoding.yaco_fashion.domain.category.repository

import com.yaincoding.yaco_fashion.domain.category.entity.Category
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryRepository: CrudRepository<Category, Long> {
}