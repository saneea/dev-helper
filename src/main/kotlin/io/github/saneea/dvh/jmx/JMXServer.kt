package io.github.saneea.dvh.jmx

import java.lang.management.ManagementFactory
import java.rmi.registry.LocateRegistry
import javax.management.ObjectName
import javax.management.StandardMBean
import javax.management.remote.JMXConnectorServer
import javax.management.remote.JMXConnectorServerFactory
import javax.management.remote.JMXServiceURL


fun main(args: Array<String>) {
    println("hi")

    try {
        val registry = LocateRegistry.createRegistry(9999)

        val registry1 = LocateRegistry.getRegistry(9999)
    }
    catch (e: Exception)
    {
    }


    val mbs = ManagementFactory.getPlatformMBeanServer()
    val name = ObjectName("io.github.saneea.dvh.jmx:type=My")
    val mbean = My()

    //val mbean = StandardMBean(mbeanImpl, My::class.java)

    mbs.registerMBean(mbean, name)

    val serviceUrl: JMXServiceURL =JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server")

    val cs: JMXConnectorServer = JMXConnectorServerFactory.newJMXConnectorServer(serviceUrl, null, mbs)
    cs.start()

    println("Waiting forever...");
    Thread.sleep(Long.MAX_VALUE);
}

interface MyMBean {
    val myName: String
    var someValue: Int

    fun writeToConsole(message: String)
    fun concat(str1: String, str2: String): String
}

class My : MyMBean {

    override val myName: String = "abc"

    override var someValue: Int = 0

    override fun writeToConsole(message: String) {
        println(message)
    }

    override fun concat(str1: String, str2: String) = str1 + str2

}

class JMXServer {

}