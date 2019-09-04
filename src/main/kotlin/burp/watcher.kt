package burp

import kotlin.concurrent.thread

import net.freehaven.tor.control.TorControlConnection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream
import java.net.Socket
import java.net.URL

class RequestWatcher() : IHttpListener {
    var counter = 0

    // if it is not an int, we just (almost) never change the ip at all
    val max = try {BurpExtender.conf.get(ConfigKeys.AFTER_N).toInt()} catch(e: NumberFormatException) {Integer.MAX_VALUE}

    @Synchronized
    override fun processHttpMessage(toolFlag: Int, isReq: Boolean, msg: IHttpRequestResponse) {
        if (isReq) {

            // if we don't filter our own requests, we get stuck in an infinity loop,
            // because the counter is only reset afterwards
            // we check for this specific URL too because then the extension works for other extension requests too
            if(toolFlag == IBurpExtenderCallbacks.TOOL_EXTENDER &&
               BurpExtender.cb.getHelpers().analyzeRequest(msg).getUrl().toString() == "https://icanhazip.com:443/") {
                return
            }

            counter++
            if(counter >= max) {
                log("Watcher", "Changing IP")
                // this is in an extra thread because else burp won't show any exceptions if something goes wrong
                // we can't just wrap everything in a thread because that might create concurrency issues
                // changeIP and checkIP must be in the same thread, else checkIP might return the old value
                thread(start = true) {
                    changeIP()

                    if(BurpExtender.conf.get(ConfigKeys.CHECK_IP) == "true") {
                        log("Watcher", "New IP is: ${checkIP()}")
                    }
                }
                counter = 0
            }
        }
    }
}


fun addWatcher() {
    BurpExtender.cb.registerHttpListener(RequestWatcher())
    log("Watcher", "Registered Watcher")
}


fun removeWatcher() {
    for(listener in BurpExtender.cb.getHttpListeners()) {
        BurpExtender.cb.removeHttpListener(listener)
    }

    log("Watcher", "Removed Watcher")
}

fun changeIP() {
    try {
        val sock = Socket("localhost", 9051)
        val con = TorControlConnection(sock)
        con.launchThread(true)

        con.authenticate(BurpExtender.cb.getHelpers().stringToBytes(
            BurpExtender.conf.get(ConfigKeys.TOR_PASSWORD)
        ))
        con.signal("NEWNYM")
    }
    catch(e: Throwable) {
        log(
            "changeIP",
            "Caught Exception while attempting to change IP:"
        )
        e.printStackTrace(PrintStream(BurpExtender.cb.getStdout()))
    }
}

fun checkIP() : String {
    // we need to use the burp build in facilities, else we most likely won't take the tor proxy
    val helpers = BurpExtender.cb.getHelpers()
    val response = BurpExtender.cb.makeHttpRequest(
        helpers.buildHttpService("icanhazip.com", 443, true),
        helpers.buildHttpRequest(URL("https://icanhazip.com"))
    ).getResponse()

    // extract the body (the service returns only the ip)
    return helpers.bytesToString(
        response.sliceArray(
            helpers.analyzeResponse(response
        ).getBodyOffset()..response.size - 1)
    )
}
