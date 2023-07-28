package net.leanix.vsm.gitlab.broker.shared.properties

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import java.util.*

@Configuration
class GradleProperties {

    private val logger: Logger = LoggerFactory.getLogger(GradleProperties::class.java)

    companion object {
        lateinit var GITLAB_ENTERPRISE_VERSION: String
            private set
    }

    @PostConstruct
    fun loadVersion() {
        try {
            val gradleProperties = Properties()
            gradleProperties.load(this::class.java.getResourceAsStream("/gradle.properties"))
            GITLAB_ENTERPRISE_VERSION = gradleProperties.getProperty("version")
            logger.info("Running GitLab broker on version: $GITLAB_ENTERPRISE_VERSION")
        } catch (e: RuntimeException) {
            GITLAB_ENTERPRISE_VERSION = "unknown"
            logger.error("Unable to load GitLab broker version")
        }
    }
}
