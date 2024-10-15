package name.remal.idea_plugin.improved_config.config

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.Service.Level.PROJECT
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.project.Project
import name.remal.idea_plugin.improved_config.config.ProjectConfigListener.Companion.CONFIG_CHANGES

@Service(PROJECT)
@State(
    name = "name.remal.improved-config",
    storages = [Storage("improved-config.xml")],
    defaultStateAsResource = true,
)
class ProjectSettingsPersistentState(project: Project) : SimplePersistentStateComponent<ProjectSettings>(ProjectSettings()) {

    private val configListener = project.messageBus.syncPublisher(CONFIG_CHANGES)

    override fun loadState(state: ProjectSettings) {
        super.loadState(state)
        configListener.configurationChanged(state)
    }

}
