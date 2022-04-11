package folders.data.etc

import folders.Folders
import host.Hostname
import org.slf4j.LoggerFactory
import java.io.IOException
import java.io.StringReader
import java.nio.file.Path
import java.util.*
import kotlin.io.path.readText

private val log = LoggerFactory.getLogger("PropsLoader.kt")

fun Properties.load(configFolder: Path, hostname: String?) {
    val config = configFolder.resolve(configFilename)
    logLoading(config)
    putAll(readProps(config))
    if (!hostname.isNullOrBlank()) {
        val hostProps = configFolder.resolve("$hostname.properties")
        logLoading(hostProps)
        if (hostProps.toFile().exists()) putAll(readProps(hostProps))
    }
}

private fun readProps(path: Path): Properties = Properties().apply { load(StringReader(path.readText())) }

private fun logLoading(path: Path) {
    try {
        log.info("Reading config file: " + path.toFile().canonicalPath)
    } catch (e: IOException) {
        e.printStackTrace()
    }
}


fun Folders.config(): Config {
    val configFile = data.etc.resolve(configFilename).canonicalFile
    if (!configFile.exists())
        error("Il file di configurazione non esiste: `$configFile`")
    val config = Config().apply { load(data.etc.toPath(), Hostname.get()) }
    return config
}
