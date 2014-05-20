/*
 * Copyright 2014 Acxio
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.configuration.PropertiesConfiguration;
import org.eclipse.jetty.util.security.Credential;

def inputReader = System.in.newReader()

// create new version of readLine that accepts a prompt to remove duplication from the loop
inputReader.metaClass.readLine = { String prompt -> print prompt ; readLine() }

// Wizard step definition

abstract class WizardStep {
    def reader
    def prompt
	def defaultValue
    
    public String readValue() {
        def isValidated = false
        def aResult
        while (!isValidated) {
            aResult = reader.readLine(getFullPrompt())
            aResult = (aResult ? aResult : defaultValue)
            isValidated = isValid(aResult)
        }
        return getComputedValue(aResult)
    }
    
    String getFullPrompt() {
		return prompt + " [" + defaultValue + "] "
	}
	
	String getComputedValue(String sValue) {
		return sValue
	}
	
    abstract boolean isValid(String sValue)
}

// String step

class StringWizardStep extends WizardStep {
	
	boolean isValid(String sValue) {
		return true
	}
}

// Yes/No step

class YesNoWizardStep extends WizardStep {
    
    boolean isValid(String sValue) {
        return ((sValue == 'Y') || (sValue == 'y') || (sValue == 'N') || (sValue == 'n'))
    }
    
    String getFullPrompt() {
        return prompt + " (Y/N) [" + defaultValue + "] "
    }
}

// TCP Port step

class TCPPortWizardStep extends WizardStep {
	private final static def portPattern = ~/(6553[0-5]|655[0-2]\d|65[0-4]\d\d|6[0-4]\d{3}|[1-5]\d{4}|[1-9]\d{0,3}|0)/
	
	boolean isValid(String sValue) {
		return portPattern.matcher(sValue).matches()
	}
}

// Directory step

class DirWizardStep extends WizardStep {
	
	boolean isValid(String sValue) {
		return (new File(sValue)).exists()
	}
	
	String getComputedValue(String sValue) {
		return new File(sValue).canonicalPath
	}
}

// Setup wizard

println()
println()
println("AGIA Setup")
println()

// Step 1 : Get current directory

def installDir = new DirWizardStep(reader:inputReader, prompt:"Installation directory", defaultValue:new File(".").canonicalPath).readValue()

// Step 2 : Get Jetty main port

def installPort = new TCPPortWizardStep(reader:inputReader, prompt:"Jetty main port", defaultValue:"9090").readValue()

// Step 3 : Get Jetty SSL port

def installSSLPort = new TCPPortWizardStep(reader:inputReader, prompt:"Jetty SSL port", defaultValue:"9443").readValue()

// Step 4 : Admin password

def installAdminPwd = new StringWizardStep(reader:inputReader, prompt:"Admin password", defaultValue:"admin").readValue()

// Display summary

println()
println("Setup summary")
println("=============")
println("Installation directory: " + installDir)
println("Jetty main port:        " + installPort)
println("Jetty SSL port:         " + installSSLPort)
println("Admin password:         " + installAdminPwd)
println()

// Prompt for action

def exec = new YesNoWizardStep(reader:inputReader, prompt:"Apply", defaultValue:"N").readValue()

// Do setup actions

println()

if ((exec == 'Y') || (exec == 'y')) {
	// Update "yasw/conf/wrapper.conf"
	print("Updating yasw/conf/wrapper.conf ...")
	
	PropertiesConfiguration wrapperconf = new PropertiesConfiguration("./yajsw/conf/wrapper.conf")
	wrapperconf.setProperty("wrapper.working.dir", installDir)
	wrapperconf.save()
	
	println("...................................[OK]")
	
	// Update "etc/jetty.xml"
	print("Updating etc/jetty.xml ...")
	
	def aJettyConfFile = new File("etc/jetty.xml")
	def aJettyConfDoc = groovy.xml.DOMBuilder.parse(aJettyConfFile.newReader())
	def aJettyConf = aJettyConfDoc.documentElement
	use(groovy.xml.dom.DOMCategory) {
		def aJettyPort = aJettyConf.depthFirst().grep{it.'@name' == "jetty.port"}[0]
		aJettyPort.setAttribute("default", installPort)
		def aJettySSLPort = aJettyConf.depthFirst().grep{it.'@name' == "confidentialPort"}[0]
		aJettySSLPort.setTextContent(installSSLPort)
	}
	def aSerializer = aJettyConfDoc.implementation.createLSSerializer()
	aJettyConfFile.withOutputStream { o ->
	  def lso = aJettyConfDoc.implementation.createLSOutput()
	  lso.byteStream = o
	  aSerializer.write(aJettyConfDoc, lso)
	}
	
	println("............................................[OK]")
	
	// Update "etc/realm.properties"
	print("Updating etc/realm.properties ...")
	def aCryptPwd = Credential.Crypt.crypt("admin", installAdminPwd)
	new File("etc/realm.properties").withWriter {
		it.writeLine("admin: " + aCryptPwd + ",admin")
	}

	println(".....................................[OK]")
}
