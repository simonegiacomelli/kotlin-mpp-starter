package folders.data.etc

import config.ConfigBase

class Config : ConfigBase() {
    val watch_design_html by BooleanProp("watch_design_html", false)
}
