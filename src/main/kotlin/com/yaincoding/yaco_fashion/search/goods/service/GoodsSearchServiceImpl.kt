package com.yaincoding.yaco_fashion.search.goods.service

import com.yaincoding.yaco_fashion.search.goods.document.GoodsDocumentParser
import com.yaincoding.yaco_fashion.search.goods.dto.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.search.goods.dto.SearchGoodsResponseDto
import com.yaincoding.yaco_fashion.search.goods.query_dsl.EsQueryParams
import com.yaincoding.yaco_fashion.search.goods.query_dsl.GoodsSort
import com.yaincoding.yaco_fashion.search.goods.query_dsl.GoodsSearchQueryDsl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
class GoodsSearchServiceImpl(
    @Value("\${elasticsearch.host}") private val host: String = "localhost",
    @Value("\${elasticsearch.port}") private val port: Int = 9200,
    @Value("\${elasticsearch.index.goods}") private val goodsIndex: String = "goods",
    @Autowired val restTemplate: RestTemplate
): GoodsSearchService {

    override fun getById(id: Int): GetGoodsResponseDto? {

        val url = "http://${host}:${port}/${goodsIndex}/_doc/${id}"
        try {
            val response: String? = restTemplate.getForObject(url, String::class.java)
            response?.let {
                return GoodsDocumentParser.parseGetGoodsResponse(response)
            }
        } catch (e: HttpClientErrorException.NotFound) {
            return null
        }

        return null
    }

    override fun search(requestDto: SearchGoodsRequestDto): SearchGoodsResponseDto {

        val url = "${host}:${port}/${goodsIndex}/_search"
        val esQueryParams: EsQueryParams = EsQueryParams().apply {
            query=requestDto.query
            sort= GoodsSort.valueOf(requestDto.sort.uppercase())
            categoryId=requestDto.categoryId
            page=requestDto.page
            size=requestDto.size
        }
        val esQuery: String = GoodsSearchQueryDsl.createEsQuery(esQueryParams)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val httpEntity: HttpEntity<String> = HttpEntity<String>(esQuery, headers)
        val response: String? = restTemplate.postForObject(url, httpEntity, String::class.java)

        response?.let {
            return GoodsDocumentParser.parseSearchGoodsResponse(response)
        }

        return SearchGoodsResponseDto(count=0, docs= emptyList())
    }
}