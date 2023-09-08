package net.leanix.vsm.gitlab.broker.shared.config

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAsync
@ConditionalOnProperty(name = ["async.enabled"], havingValue = "true", matchIfMissing = true)
public class AsyncConfiguration