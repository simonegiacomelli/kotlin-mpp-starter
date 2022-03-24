package utils

import java.io.File
import java.net.URL


class AutoLoadPackage {
    fun loadClasses(packageName: String) {
        var name = packageName
        if (!name.startsWith("/")) {
            name = "/$name"
        }
        name = name.replace('.', '/')

        val url: URL = AutoLoadPackage::class.java.getResource(name)
        val directory = File(url.file)

        println("Loading classes in package: $packageName")
        if (directory.exists()) {
            directory.walk()
                .filter { f -> f.isFile && !f.name.contains('$') && f.name.endsWith(".class") }
                .forEach {
                    try {
                        val fullyQualifiedClassName = packageName +
                                it.canonicalPath.removePrefix(directory.canonicalPath)
                                    .dropLast(6) // remove .class
                                    .replace('/', '.')
                                    .replace('\\', '.')
                        println("  loading class: $fullyQualifiedClassName")
                        val o = Class.forName(fullyQualifiedClassName)
                    } catch (cnfex: ClassNotFoundException) {
                        System.err.println(cnfex)
                    }
                }
        }
    }
}