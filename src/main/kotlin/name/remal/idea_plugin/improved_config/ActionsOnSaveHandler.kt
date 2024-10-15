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

    override fun configurationChanged(settings: ProjectSettings) {
        val reformatCode = settings.reformatCode
        val optimizeImports = settings.optimizeImports
        thisLogger().info("applying config: reformatCode=$reformatCode, optimizeImports=$optimizeImports")

        thisLogger().info("  configuring ${javaClass.name}")
        getStateOf(formatOnSaveOptions).apply {
            this["myRunOnSave"] = reformatCode != ReformatCode.DISABLED
            this["myAllFileTypesSelected"] = true
            this["myFormatOnlyChangedLines"] = reformatCode == ReformatCode.CHANGED_LINES
        }

        thisLogger().info("  configuring ${javaClass.name}")
        getStateOf(optimizeImportsOnSaveOptions).apply {
            this["myRunOnSave"] = optimizeImports
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
