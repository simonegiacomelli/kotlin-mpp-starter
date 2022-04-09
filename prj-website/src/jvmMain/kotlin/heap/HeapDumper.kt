package heap

import com.sun.management.HotSpotDiagnosticMXBean
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.management.ManagementFactory

object HeapDumper {
    private val log = LoggerFactory.getLogger(javaClass)

    // This is the name of the HotSpot Diagnostic MBean
    private const val HOTSPOT_BEAN_NAME = "com.sun.management:type=HotSpotDiagnostic"

    // get the hotspot diagnostic MBean from the
    // platform MBean server
    // field to store the hotspot diagnostic MBean
    @get:Synchronized
    private val hotspotMBean: HotSpotDiagnosticMXBean by lazy {
        val server = ManagementFactory.getPlatformMBeanServer()
        val res = ManagementFactory.newPlatformMXBeanProxy(
            server, HOTSPOT_BEAN_NAME, HotSpotDiagnosticMXBean::class.java
        )
        res
    }

    fun dumpHeap(fileName: String, live: Boolean) {
        hotspotMBean.dumpHeap(fileName, live)
    }

    @JvmStatic
    fun main(args: Array<String>) {
        // default heap dump file name
        var fileName = "c:/temp/heap.bin"
        // by default dump only the live objects
        var live = true
        when (args.size) {
            2 -> {
                live = args[1] == "true"
                fileName = args[0]
            }
            1 -> fileName = args[0]
        }

        // dump the heap
        dumpHeap(fileName, live)
    }

    fun enableJvmToHeapDumpOnOutOfMemory(dumpFilename: String) {
        hotspotMBean.setVMOption("HeapDumpOnOutOfMemoryError", "true")
        hotspotMBean.setVMOption("HeapDumpPath", dumpFilename)
    }


    fun enableHeapDump(heapDumpRootFolder: File) {
        heapDumpRootFolder.mkdirs()
        val dt = Clock.System.now().toString()
        val relative = "heapdump-$dt".replace(":", "-").replace(".", "-").replace("T", "--") + ".hprof"
        val path = heapDumpRootFolder.resolve(relative).canonicalPath
        log.info("Abilito l'heapdump sul file [$path]")
        enableJvmToHeapDumpOnOutOfMemory(path)
    }


}