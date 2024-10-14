package name.remal.idea_plugin.improved_config.config

import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import name.remal.idea_plugin.improved_config.config.ProjectConfigListener.Companion.CONFIG_CHANGES
import org.jetbrains.annotations.Nls
import javax.swing.JComponent

class ProjectConfigurable(project: Project) : Configurable {

    private val configListener = project.messageBus.syncPublisher(CONFIG_CHANGES)

    private val stateService = project.service<ProjectPersistentState>()

    private var disposableView: ProjectConfigurableView? = null


    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName() = "Project Auto Config"

    override fun createComponent(): JComponent? = ProjectConfigurableView().apply { disposableView = this }.component

    override fun disposeUIResources() {
        disposableView = null
    }


    private val settings by stateService::state

    private val view get() = disposableView!!

    override fun isModified() =
        settings.reformatCode != view.reformatCode
            || settings.optimizeImports != view.optimizeImports

    override fun apply() {
        settings.reformatCode = view.reformatCode
        settings.optimizeImports = view.optimizeImports
        configListener.configurationChanged(settings)
    }

    override fun reset() {
        view.reformatCode = settings.reformatCode
        view.optimizeImports = settings.optimizeImports
    }

}
