package name.remal.idea_plugin.improved_config.config

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.listCellRenderer
import com.intellij.ui.dsl.builder.panel

class ProjectConfigurableView {

    val component = panel {
        group("Actions on Save") {
            row("Reformat code") {
                reformatCodeComponent = comboBox(ReformatCode.entries, listCellRenderer { value ->
                    text = value.text
                }).component
            }
            row {
                optimizeImportsComponent = checkBox("Optimize imports").component
            }
        }
    }


    private lateinit var reformatCodeComponent: ComboBox<ReformatCode>

    var reformatCode: ReformatCode
        get() = reformatCodeComponent.item
        set(value) {
            reformatCodeComponent.item = value
        }


    private lateinit var optimizeImportsComponent: JBCheckBox

    var optimizeImports: Boolean
        get() = optimizeImportsComponent.isSelected
        set(value) {
            optimizeImportsComponent.isSelected = value
        }

}
