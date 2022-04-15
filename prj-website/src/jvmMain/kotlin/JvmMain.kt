import cli.cli
import cli.defaultHandlers
import java.io.File

fun main(args: Array<String>) {
    println("=".repeat(50))
    println("Working folder ${File(".").canonicalPath}")
    cli(*args) { defaultHandlers() }
}

