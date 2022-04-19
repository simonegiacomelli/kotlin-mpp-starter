package pages.bootstrap.databinding.demo

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable
import pages.bootstrap.databinding.Bindable

@Serializable
class User3 : Bindable() {

    var age by this(42)
    var degree by this("master")
    var birthday: LocalDate? by this("2022-12-31".toLocalDate())

    override fun toString() = "age=$age degree=$degree valueMap=$bindingValueMap"
}

