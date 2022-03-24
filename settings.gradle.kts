rootProject.name = "kotlin-mpp-starter"
val exclude = setOf("")
rootProject.projectDir.listFiles()?.apply {
    filterNotNull()
        .filter { it.isDirectory && it.name.startsWith("prj-") }
        .forEach {
            if (exclude.contains(it.name))
                println(" exclude ${it.name}")
            else {
                println("include(${it.name})")
                include(it.name)
            }
        }
}
