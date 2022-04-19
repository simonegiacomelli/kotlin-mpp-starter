package serialization

//val format = Json {
//
//    serializersModule = SerializersModule {
//        polymorphic(Any::class) {
//            subclass(String::class, String.serializer())
//            subclass(Int::class, Int.serializer())
//        }
//        contextual(String.serializer())
//        contextual(Int.serializer())
//        contextual(Float.serializer())
//        contextual(Double.serializer())
//        contextual(Boolean.serializer())
//        contextual(Char.serializer())
//        contextual(Byte.serializer())
//    }
//}