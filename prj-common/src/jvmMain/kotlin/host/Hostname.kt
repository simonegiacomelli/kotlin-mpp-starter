package host

import java.net.InetAddress

object Hostname {
    private var hostName: String? = null

    fun get(): String? {
        if (hostName != null) return hostName
        hostName = System.getProperty("host.name", null)
        if (hostName == null) hostName = System.getenv("HOSTNAME")
        if (hostName == null) hostName = System.getenv("COMPUTERNAME")
        if (hostName == null) {
            try {
                val hn = Runtime.getRuntime().exec("hostname").inputStream.bufferedReader().use { it.readText() }
                hostName = hn.replace("\n", "").replace("\r", "")
            } catch (ignored: Exception) {
            }
        }
        if (hostName == null) {
            try {
                hostName = InetAddress.getLocalHost().hostName
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        return if (hostName != null) hostName else "unknown_hostname"
    }

    @JvmStatic
    fun main(args: Array<String>) {
        println("Hostname: " + get())
    }
}