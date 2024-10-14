package name.remal.idea_plugin.improved_config

import com.intellij.codeInsight.actions.onSave.FormatOnSaveOptions
import com.intellij.codeInsight.actions.onSave.OptimizeImportsOnSaveOptions
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import name.remal.idea_plugin.improved_config.config.ProjectConfigListener
import name.remal.idea_plugin.improved_config.config.ProjectSettings
import name.remal.idea_plugin.improved_config.config.ReformatCode

class ActionsOnSaveHandler(project: Project) : ProjectConfigListener {

    private val formatOnSaveOptions = project.service<FormatOnSaveOptions>()
    private val optimizeImportsOnSaveOptions = project.service<OptimizeImportsOnSaveOptions>()
    private val logger = thisLogger()

    override fun configurationChanged(settings: ProjectSettings) {
        logger.info("Configuration changed: $settings")

        getStateOf(formatOnSaveOptions).apply {
            logger.info("  configuring ${javaClass.name}")
            this["myRunOnSave"] = settings.reformatCode != ReformatCode.DISABLED
            this["myAllFileTypesSelected"] = true
            this["myFormatOnlyChangedLines"] = settings.reformatCode == ReformatCode.CHANGED_LINES
        }

        getStateOf(optimizeImportsOnSaveOptions).apply {
            logger.info("  configuring ${javaClass.name}")
            this["myRunOnSave"] = settings.optimizeImports
            this["myAllFileTypesSelected"] = true
        }
    }

    private fun getStateOf(obj: Any): Any {
        val method = obj.javaClass.getMethod("getState")
            .apply { isAccessible = true }
        return method.invoke(obj)
    }

    private operator fun Any.set(fieldName: String, value: Any?) {
        val field = javaClass.getField(fieldName)
            .apply { isAccessible = true }
        field[this] = value
    }

}
