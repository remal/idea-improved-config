package name.remal.idea_plugin.improved_config

import com.intellij.application.options.CodeStyle
import com.intellij.externalDependencies.DependencyOnPlugin
import com.intellij.externalDependencies.ExternalDependenciesManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.psi.codeStyle.CodeStyleSettingsManager
import com.intellij.util.containers.toMutableSmartList
import name.remal.idea_plugin.improved_config.config.ProjectConfigListener
import name.remal.idea_plugin.improved_config.config.ProjectSettings
import org.editorconfig.settings.EditorConfigSettings
import org.editorconfig.settings.EditorConfigSettings.EDITOR_CONFIG_ENABLED_TOPIC

class EditorConfigHandler(private val project: Project) : ProjectConfigListener {

    companion object {
        private val EDITOR_CONFIG_PLUGIN_ID = "org.editorconfig.editorconfigjetbrains"
    }

    override fun configurationChanged(settings: ProjectSettings) {
        val editorConfig = settings.editorConfig
        thisLogger().info("applying config: editorConfig=$editorConfig")

        CodeStyle.getSettings(project)
            .getCustomSettings(EditorConfigSettings::class.java)
            .ENABLED = editorConfig

        ApplicationManager.getApplication().messageBus
            .syncPublisher(EDITOR_CONFIG_ENABLED_TOPIC)
            .valueChanged(editorConfig)

        CodeStyleSettingsManager.getInstance(project)
            .notifyCodeStyleSettingsChanged()


        if (editorConfig) {
            val externalDependenciesManager = ExternalDependenciesManager.getInstance(project)
            val externalDependencies = externalDependenciesManager.allDependencies.toMutableSmartList()
            val editorConfigPluginDependencyAdded = externalDependencies.any {
                (it as? DependencyOnPlugin)?.pluginId == EDITOR_CONFIG_PLUGIN_ID
            }
            if (!editorConfigPluginDependencyAdded) {
                externalDependencies.add(
                    DependencyOnPlugin(EDITOR_CONFIG_PLUGIN_ID, null, null)
                )
                externalDependenciesManager.allDependencies = externalDependencies
            }
        }
    }

}
