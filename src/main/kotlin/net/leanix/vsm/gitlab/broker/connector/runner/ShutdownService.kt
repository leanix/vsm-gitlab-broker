package net.leanix.vsm.gitlab.broker.connector.runner

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class ShutdownService {

    private val logger = LoggerFactory.getLogger(ShutdownService::class.java)

    @PreDestroy
    fun onDestroy() {
        logger.info("Shutting down github broker")
    }
}
