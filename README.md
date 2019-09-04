# TorIpRotate Burp Extension

## Setup

1. Download the extension from the releases.
2. Add the extension using the `Add` button at the `Extender` Tab.

## Basic Usage

1. At the time of writing the SOCKS proxy feature in burp hasn't really worked, therefore you might need to setup `privoxy` or something similar.
   For `privoxy`, you should add something like the following to your config:
   ```
   forward-socks5t / 127.0.0.1:9050 .
   ```
2. To use the tor control port, add the following lines to your `torrc` (usually at */etc/tor/torrc*)
   ```
   ControlPort 9051
   HashedControlPassword 16:2A55904034FFDE2D600B14BECF457C58B83CFC2C82DFD5A7AE7A905FCC

   ```
   You can get a hashed password by using:
   ```
   tor --hash-password password
   ```

3. After you made sure that burpsuite is working with tor properly, go to the `Tor Ip Rotate` tab and set everything up.
4. Afterwards, check the `Tor Ip Rotate active` checkbox.
5. If you have the option to check new IP addresses marked, you should probably go to the `Extender` tab to view the log there,
   or configure the extension to log to somewhere else.


## Building from Source

1. Clone this repository
2. Use maven to build the project: `mvn packaage`.
3. Add `toriprotate-1.0-SNAPSHOT-jar-with-dependencies.jar` like described at *Setup*. 
   Make sure you pick the right one, the other jar without *with dependencies* won't work.
