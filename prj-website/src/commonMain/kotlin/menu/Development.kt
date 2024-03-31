package menu

import accesscontrol.Role.Admin

class Development(parent: Menu) : Menu(parent, "development", "Development", Admin) {
    val date_input_delphi_style = menu("date_input", "Date input Delphi-style")
    val html_editor = menu("html_editor", "Html editor")
    val html_display = menu("html_display", "Html display")
    val two_way_data_binding = menu("data_binding", "Two way data binding")
    val data_binding_demo = menu("data_binding_demo", "Data binding demo")
    val custom_elements_demo = menu("custom_elements_demo", "Custom Elements demo")
    val spinner = menu("spinner3", "Spinner for 3 secs")
}