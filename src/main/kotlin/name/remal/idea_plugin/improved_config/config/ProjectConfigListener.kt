package name.remal.idea_plugin.improved_config.config

import com.intellij.util.messages.Topic
import com.intellij.util.messages.Topic.ProjectLevel
import java.util.EventListener

fun interface ProjectConfigListener : EventListener {

    companion object {
        @ProjectLevel
        val CONFIG_CHANGES: Topic<ProjectConfigListener> = Topic(ProjectConfigListener::class.java)
    }


    fun configurationChanged(settings: ProjectSettings)

}
