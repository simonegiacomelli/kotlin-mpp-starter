package databinding

//@Serializable
class User : Bindable() {
    var name: String by this()
    var age: Int by this()
}