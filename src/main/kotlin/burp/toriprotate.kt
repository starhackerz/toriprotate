package burp

import net.freehaven.tor.control.TorControlConnection

fun log(source: String, text: String) = BurpExtender.cb.printOutput("[" + source + "] " + text)

class BurpExtender : IBurpExtender, IExtensionStateListener {

    companion object {
        lateinit var cb: IBurpExtenderCallbacks
        lateinit var conf: Config
    }

    // Called on load of the extension
    override fun registerExtenderCallbacks(callbacks: IBurpExtenderCallbacks) {
        cb = callbacks
        conf = Config.loadConfig()

        val ui = UI()

        cb.setExtensionName("TorIpRotate")
        cb.registerExtensionStateListener(this)
        cb.addSuiteTab(ui)

        log("Main", "TorIpRotate extension loaded.")
    }

    override fun extensionUnloaded() {}
}

enum class ConfigKeys() {
    AFTER_N      {override fun default() = "5"},
    TOR_PASSWORD {override fun default() = ""},
    CHECK_IP     {override fun default() = "false"};

    abstract fun default() : String
}

class Config(val confs : MutableMap<ConfigKeys, String>) {

    public fun get(name: ConfigKeys) : String = confs.getOrElse(name, {-> name.default()})
    public fun set(name: ConfigKeys, value: String) {
        confs.put(name, value)
        BurpExtender.cb.saveExtensionSetting(name.toString(), value)
    }

    companion object {
        public fun loadConfig() : Config{
            val confs = mutableMapOf<ConfigKeys, String>()
            for(v in ConfigKeys.values()) {
                confs.put(v, BurpExtender.cb.loadExtensionSetting(v.toString()) ?: v.default())
            }
            return Config(confs)
        }
    }
}
