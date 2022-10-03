package com.yaincoding.yaco_fashion.domain.top_keyword.repository

import com.yaincoding.yaco_fashion.domain.top_keyword.entity.TopKeyword
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TopKeywordRepository: CrudRepository<TopKeyword, Long> {
}