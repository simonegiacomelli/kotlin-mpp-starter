package pages.bootstrap.databinding

import kotlinx.serialization.Serializable

@Serializable
class User3 : Binding() {

    var age by this(42)
    var degree by this("master")

    override fun toString() = "age=$age degree=$degree valueMap=$bindingValueMap"
}

