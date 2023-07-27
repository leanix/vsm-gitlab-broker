package net.leanix.vsm.gitlab.broker.connector.runner

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class InitialStateRunner: ApplicationRunner {

    private val logger: Logger = LoggerFactory.getLogger(InitialStateRunner::class.java)

    override fun run(args: ApplicationArguments?) {
        logger.info("Started get initial state")

        TODO("Not yet implemented")
    }
}