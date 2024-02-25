package org.example

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import shark.core.plugin.Plugin
import shark.core.plugin.SharkPlugin

@Component
@SharkPlugin(PluginTemplate.pluginId)
class PluginTemplate : Plugin() {

    companion object {
        const val pluginId = "example_plugin"
    }

    val logger: Logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun initialize() {
        logger.info("Initialized!")
    }

}
