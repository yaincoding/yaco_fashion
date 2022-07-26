package com.yaincoding.yaco_fashion.service.elasticsearch.goods

import com.yaincoding.yaco_fashion.document.goods.GoodsDocumentParser
import com.yaincoding.yaco_fashion.dto.goods.GetGoodsResponseDto
import com.yaincoding.yaco_fashion.dto.goods.SearchGoodsResponseDto
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
        val goods_index: String = "goods",
        @Autowired val restTemplate: RestTemplate
): GoodsService {

    override fun getById(id: Int): GetGoodsResponseDto? {

        val url = "http://${host}:${port}/${goods_index}/_doc/${id}"
        val response: String? = restTemplate.getForObject(url, String::class.java)

        response?.let {
            return GoodsDocumentParser.parseGetGoodsResponse(response)
        }

        return null
    }

    override fun search(query: String): SearchGoodsResponseDto {
        val url = "http://${host}:${port}/${goods_index}/_search"
        val response: String? = restTemplate.postForObject(url, null, String::class.java)
    }
}