package forms.accesscontrol

import databinding.Bindable

class Credential : Bindable() {
    var username: String by this("")
    var password: String by this("")
}