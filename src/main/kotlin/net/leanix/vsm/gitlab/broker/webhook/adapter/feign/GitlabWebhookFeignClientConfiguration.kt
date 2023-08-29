package net.leanix.vsm.gitlab.broker.webhook.adapter.feign

import feign.RequestInterceptor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GitlabWebhookFeignClientConfiguration(

    @Value("\${leanix.vsm.connector.gitlab-token}") private val gitlabAccessToken: String
) {
    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header("PRIVATE-TOKEN", gitlabAccessToken)
        }
    }
}
