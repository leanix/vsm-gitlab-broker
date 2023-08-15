package net.leanix.vsm.gitlab.broker.shared.auth.adapter.feign.config

import feign.RequestInterceptor
import net.leanix.vsm.gitlab.broker.shared.Constants.GITLAB_ON_PREM_VERSION_HEADER
import net.leanix.vsm.gitlab.broker.shared.auth.application.GetBearerToken
import net.leanix.vsm.gitlab.broker.shared.properties.GradleProperties.Companion.GITLAB_ENTERPRISE_VERSION
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders.AUTHORIZATION

class MtmFeignClientConfiguration(private val getBearerToken: GetBearerToken) {

    @Bean
    fun requestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            it.header(GITLAB_ON_PREM_VERSION_HEADER, GITLAB_ENTERPRISE_VERSION)
            it.header(AUTHORIZATION, "Bearer ${getBearerToken()}")
        }
    }
}
