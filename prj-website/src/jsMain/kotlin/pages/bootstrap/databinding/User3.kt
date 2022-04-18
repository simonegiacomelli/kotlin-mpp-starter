package pages.bootstrap.databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable

@Serializable
class User3 : Binding() {

    var age by this(value(42))
    var degree by this(value("master"))
    var birthday: LocalDate? by this(value("2022-12-31".toLocalDate()))

    override fun toString() = "age=$age degree=$degree valueMap=$bindingValueMap"
}

