package net.leanix.vsm.gitlab.broker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GitlabBrokerApplication

fun main() {
    runApplication<GitlabBrokerApplication>()
}
