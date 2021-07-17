package io.github.saneea.dvh.jmx

import com.sun.tools.attach.VirtualMachine
import javax.management.JMX
import javax.management.ObjectName
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXServiceURL

fun main(args: Array<String>) {

    val serviceUrl: JMXServiceURL = JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:9999/server")

    JMXConnectorFactory
        .connect(serviceUrl, null)
        .use { jmxConnector ->

            val mBeanServerConnection = jmxConnector.mBeanServerConnection

            val name = ObjectName("io.github.saneea.dvh.jmx:type=My")

            val mBeanInfo = mBeanServerConnection.getMBeanInfo(name)

            val newMBeanProxy = JMX.newMBeanProxy(mBeanServerConnection, name, MyMBean::class.java)

            println(newMBeanProxy.someValue)
        }

//
//    VirtualMachine.attach("")

}