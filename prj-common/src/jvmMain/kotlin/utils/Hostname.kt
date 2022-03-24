package utils

import java.net.InetAddress


val hostname: String by lazy { hostname() }

private fun hostname(): String {
    val env = System.getenv()
    var hostname =
        if (env.containsKey("COMPUTERNAME")) env["COMPUTERNAME"] else if (env.containsKey("HOSTNAME")) env["HOSTNAME"] else ""
    hostname = hostname.orEmpty()

    if (hostname != "") return hostname

    try {
        Runtime.getRuntime().exec("hostname").inputStream.use {
            return it.bufferedReader().readLine().orEmpty()
        }
    } catch (ex: Exception) {

    }

    val addr: InetAddress = InetAddress.getLocalHost()
    return addr.hostName

}