package name.remal.idea_plugin.improved_config.config

import com.intellij.openapi.components.service
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ProjectConfigLoadActivity : ProjectActivity, DumbAware {

    override suspend fun execute(project: Project) {
        project.service<ProjectSettingsPersistentState>().state
    }

}
