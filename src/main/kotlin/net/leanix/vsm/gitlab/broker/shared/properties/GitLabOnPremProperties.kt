package net.leanix.vsm.gitlab.broker.shared.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "leanix.vsm.connector")
data class GitLabOnPremProperties(
    val apiUserToken: String
)
