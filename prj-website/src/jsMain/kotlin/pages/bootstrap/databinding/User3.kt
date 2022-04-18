package pages.bootstrap.databinding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import kotlinx.serialization.Serializable

@Serializable
class User3 : Binding() {

    var age by this(valueOf(42))
    var degree by this(valueOf("master"))
    var birthday: LocalDate? by this(valueOf("2022-12-31".toLocalDate()))

    override fun toString() = "age=$age degree=$degree valueMap=$bindingValueMap"
}

