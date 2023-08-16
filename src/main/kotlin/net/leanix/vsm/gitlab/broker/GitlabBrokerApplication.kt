package net.leanix.vsm.gitlab.broker

import net.leanix.vsm.gitlab.broker.shared.properties.GitLabOnPremProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(GitLabOnPremProperties::class)
class GitlabBrokerApplication

fun main() {
    runApplication<GitlabBrokerApplication>()
}
