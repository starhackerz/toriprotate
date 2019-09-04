package burp

import javax.swing.BorderFactory
import java.awt.BorderLayout
import javax.swing.border.EmptyBorder
import javax.swing.border.EtchedBorder
import java.awt.GridLayout
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.JTextField
import javax.swing.event.DocumentListener
import javax.swing.event.DocumentEvent

class UI () : ITab {
    override public fun getTabCaption() = "Tor Ip Rotate"
    override public fun getUiComponent() = mainPanel

    val mainPanel = JPanel(BorderLayout())
    val topContainer = JPanel(BorderLayout())

    val toggleIpRotator = JCheckBox("Tor Ip Rotate active")
    val configPanel = JPanel(GridLayout(0, 2))
    val afterN = JTextField(10)
    val torpw = JTextField(10)
    val checkIp = JCheckBox("Check new IP using https://icanhazip.com/ after every change (view extension log)")

    init {
        // for padding
        toggleIpRotator.setBorder(EmptyBorder(30, 30, 0, 30))
        configPanel.setBorder(
            BorderFactory.createCompoundBorder(
                EmptyBorder(0, 30, 30, 30),
                BorderFactory.createEtchedBorder(EtchedBorder.RAISED)
            )
        )

        // action listeners
        afterN.getDocument().addDocumentListener(object: DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.AFTER_N, afterN.getText().trim())
            }
            override fun removeUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.AFTER_N, afterN.getText().trim())
            }
            override fun insertUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.AFTER_N, afterN.getText().trim())
            }
        })
        torpw.getDocument().addDocumentListener(object: DocumentListener {
            override fun changedUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.TOR_PASSWORD, torpw.getText().trim())
            }
            override fun removeUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.TOR_PASSWORD, torpw.getText().trim())
            }
            override fun insertUpdate(e: DocumentEvent) {
                BurpExtender.conf.set(ConfigKeys.TOR_PASSWORD, torpw.getText().trim())
            }
        })

        checkIp.addActionListener{_ ->
            BurpExtender.conf.set(ConfigKeys.CHECK_IP, checkIp.isSelected().toString())
        }

        toggleIpRotator.addActionListener {_ ->
            if(toggleIpRotator.isSelected()) {
                addWatcher()
            }
            else {
                removeWatcher()
            }
        }

        // restore config
        afterN.setText(BurpExtender.conf.get(ConfigKeys.AFTER_N))
        torpw.setText(BurpExtender.conf.get(ConfigKeys.TOR_PASSWORD))
        checkIp.setSelected(BurpExtender.conf.get(ConfigKeys.CHECK_IP) == "true")
        configPanel.add(JLabel("How many requests between each IP switch (reactivate to apply):"))
        configPanel.add(afterN)
        configPanel.add(JLabel("Warning: If you set this number very low, you might hit Tors rate limiting."))
        configPanel.add(JPanel())
        configPanel.add(JLabel("If you observe that the reported 'new' ip address is mostly the same, try to tweak this setting."))
        configPanel.add(JPanel())
        configPanel.add(JLabel("Tor Socket Password:"))
        configPanel.add(torpw)
        configPanel.add(checkIp)
        configPanel.add(JPanel())

        topContainer.setBorder(EmptyBorder(30, 30, 0, 30))
        topContainer.add(toggleIpRotator, BorderLayout.NORTH)
        topContainer.add(configPanel, BorderLayout.SOUTH)
        mainPanel.add(topContainer, BorderLayout.NORTH)
    }
}
