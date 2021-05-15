package io.github.saneea.dvh

import io.github.saneea.dvh.feature.DvhRootFeature
import kotlin.system.exitProcess

object App {

    @JvmStatic
    fun main(args: Array<String>) {
        val rootFeature = DvhRootFeature()
        try {
            rootFeature.context = FeatureContext(null, "dvh", args)
            rootFeature.run()
        } catch (appExitException: AppExitException) {
            exitProcess(appExitException.exitCode)
        }
    }
}