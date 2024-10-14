package name.remal.idea_plugin.improved_config.config

import com.intellij.openapi.components.BaseState

class ProjectSettings : BaseState() {

    var reformatCode by enum(ReformatCode.DISABLED)

    var optimizeImports by property(false)

}
