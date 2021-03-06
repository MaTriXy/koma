package koma.storage.config.server

import koma.storage.config.config_paths
import java.io.File
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Proxy

fun save_server_proxy(servername: String, proxy: Proxy): Boolean {
    val server_dir = config_paths.getCreateProfileDir( servername)
    if (server_dir == null) return false
    val file = File(server_dir + File.separator + ".conf.toml")
    try {
        file.writeText(proxy.toSimpleString())
    } catch (e: IOException) {
        return false
    }
    return true
}

fun get_server_proxy(servername: String): Proxy {
    val server_dir = config_paths.getCreateProfileDir( servername)
    if (server_dir == null) return Proxy.NO_PROXY
    val file = File(server_dir + File.separator + ".conf.toml")
    if (!file.isFile) return Proxy.NO_PROXY
    try {
        val proxy = file.readText()
        return parseProxy(proxy)?: Proxy.NO_PROXY
    } catch (e: IllegalStateException) {
        println("failed to load proxy of $servername")
        return Proxy.NO_PROXY
    }
}

fun parseProxy(proxy: String): Proxy? {
    val proxyconf = proxy.split(" ", limit = 3)
    if (proxyconf.size < 1 ) return null
    if (proxyconf[0] == "DIRECT" ) return Proxy.NO_PROXY
    if (proxyconf.size < 3 ) return null
    val type = when(proxyconf[0]) {
        "HTTP" -> Proxy.Type.HTTP
        "SOCKS" -> Proxy.Type.SOCKS
        else -> return null
    }
    val host = proxyconf[1]
    val port = proxyconf[2].toIntOrNull()
    if (port == null) return null
    val sa = InetSocketAddress(host, port)
    return Proxy(type, sa)
}

fun Proxy.toSimpleString(): String {
    val sb = StringBuilder(this.type().toString())
    val addr = this.address() as InetSocketAddress?
    if (addr != null) {
        val host = addr.hostString
        if (host != null) sb.append(" " + host)
        val port = addr.port
        sb.append(" " + port)
    }
    return sb.toString()
}
