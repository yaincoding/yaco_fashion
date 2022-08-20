package com.yaincoding.yaco_fashion.service.elasticsearch.goods

import com.yaincoding.yaco_fashion.document.goods.GoodsDocumentParser
import com.yaincoding.yaco_fashion.dto.goods.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.dto.goods.SearchGoodsRequestDto
import com.yaincoding.yaco_fashion.dto.goods.SearchGoodsResponseDto
import com.yaincoding.yaco_fashion.query_dsl.EsQueryParams
import com.yaincoding.yaco_fashion.query_dsl.GoodsSort
import com.yaincoding.yaco_fashion.query_dsl.QueryDslFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate

@Service
@ConfigurationProperties(prefix="elasticsearch")
class GoodsServiceImpl(
        val host: String = "localhost",
        val port: Int = 9200,
        val goods_index: String = "goods",
        @Autowired val restTemplate: RestTemplate
): GoodsService {

    override fun getById(id: Int): GetGoodsResponseDto? {

        val url = "http://${host}:${port}/${goods_index}/_doc/${id}"
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

        val url = "http://${host}:${port}/${goods_index}/_search"
        val esQueryParams: EsQueryParams = EsQueryParams().apply {
            query=requestDto.query
            sort=GoodsSort.valueOf(requestDto.sort.uppercase())
            categoryId=requestDto.categoryId
        }
        val esQuery: String = QueryDslFactory.createEsQuery(esQueryParams)
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