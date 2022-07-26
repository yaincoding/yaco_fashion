package com.yaincoding.yaco_fashion.service.elasticsearch.goods

import com.yaincoding.yaco_fashion.dto.goods.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.dto.goods.SearchResponseDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.net.URI

@Service
@ConfigurationProperties(prefix="elasticsearch")
class GoodsServiceImpl(
        val host: String = "localhost",
        val port: Int = 9200,
        @Autowired private val restTemplate: RestTemplate
): GoodsService {

    override fun getById(id: Int): GetGoodsResponseDto {

        val headers: HttpHeaders = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val httpEntity = HttpEntity(null, headers)

        val url = "http://${host}:${port}/_doc/${id}"
        val uri: URI = URI.create(url)
    }

    override fun search(keyword: String): SearchResponseDto {
        TODO("Not yet implemented")
    }
}