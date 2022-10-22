package com.yaincoding.yaco_fashion.search.goods.service

import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocument
import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocumentParser
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.query_dsl.EsQueryParams
import com.yaincoding.yaco_fashion.search.goods.query_dsl.GoodsSort
import com.yaincoding.yaco_fashion.search.goods.query_dsl.GoodsSearchQueryDsl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class GoodsSearchServiceImpl(
    @Value("\${elasticsearch.index.goods}") private val goodsIndex: String,
    private val webClient: WebClient,
    private val documentParser: GoodsDocumentParser,
): GoodsSearchService {

    override fun getById(id: Int): GoodsDocument? {

        val response: String? = webClient
            .get()
            .uri("/${goodsIndex}/_doc/${id}")
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        response?.let {
            return documentParser.parseGetGoodsResponse(response)
        }

        return null
    }

    override fun search(requestDto: SearchGoodsRequestDto): SearchGoodsResponseDto {

        val esQueryParams: EsQueryParams = EsQueryParams().apply {
            query=requestDto.query
            sort= GoodsSort.valueOf(requestDto.sort.uppercase())
            categoryId=requestDto.categoryId
            page=requestDto.page
            size=requestDto.size
        }
        val esQuery: String = GoodsSearchQueryDsl.createEsQuery(esQueryParams)

        val response: String? = webClient
            .post()
            .uri("/${goodsIndex}/_search")
            .bodyValue(esQuery)
            .retrieve()
            .bodyToMono(String::class.java)
            .block()

        return documentParser.parseSearchGoodsResponse(response!!)
    }
}