package pages.bootstrap.databinding.demo

import databinding.Bindable
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable

@Serializable
class User3 : Bindable() {

    var age by this(42)
    var degree by this("master")
    var birthday: LocalDate? by this("2022-12-31".toLocalDate())

    override fun toString() = "age=$age degree=$degree valueMap=$bindingValueMap"
}

