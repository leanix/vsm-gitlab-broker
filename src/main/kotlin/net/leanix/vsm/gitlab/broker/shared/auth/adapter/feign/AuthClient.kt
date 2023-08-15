package net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign

import net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.data.JwtTokenResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(
    name = "authentication",
    url = "\${leanix.vsm.auth.access-token-uri}"
)
fun interface AuthClient {

    @PostMapping(value = ["/oauth2/token"], consumes = [APPLICATION_FORM_URLENCODED_VALUE])
    fun getToken(
        @RequestHeader(name = AUTHORIZATION) authorization: String,
        @RequestBody body: String
    ): JwtTokenResponse
}
