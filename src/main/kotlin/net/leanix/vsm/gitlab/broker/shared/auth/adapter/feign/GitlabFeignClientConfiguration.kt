package net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitlabFeignClientConfiguration(

    @Value("\${leanix.gitlab.access-token}") private val gitlabAccessToken: String
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header("PRIVATE-TOKEN", gitlabAccessToken)
        }
    }
}
