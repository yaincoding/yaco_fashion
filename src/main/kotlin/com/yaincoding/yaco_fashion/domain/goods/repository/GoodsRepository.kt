package com.yaincoding.yaco_fashion.domain.goods.repository

import com.yaincoding.yaco_fashion.domain.goods.entity.Goods
import org.springframework.stereotype.Repository
import org.springframework.data.repository.CrudRepository

@Repository
interface GoodsRepository: CrudRepository<Goods, Long> {
}