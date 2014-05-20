package fr.acxio.tools.agia.ftp;

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
 
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Proxy;
import java.nio.charset.Charset;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultFtpClientFactory implements FtpClientFactory {
	
	private static Logger logger = LoggerFactory.getLogger(DefaultFtpClientFactory.class);
	
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String account;
	
	private String activeExternalIPAddress;
	private Integer activeMinPort;
	private Integer activeMaxPort;
	private Boolean autodetectUTF8;
	private Integer bufferSize;
	private Charset charset;
	private Integer connectTimeout;
	private String controlEncoding;
	private Integer controlKeepAliveReplyTimeout;
	private Long controlKeepAliveTimeout;
	private Integer dataTimeout;
	private Integer defaultPort;
	private Integer defaultTimeout;
	private Integer fileStructure;
	private Integer fileTransferMode;
	private Integer fileType;
	private Boolean keepAlive;
	private Boolean listHiddenFiles;
	private FTPFileEntryParserFactory parserFactory;
	private String passiveLocalIPAddress;
	private Boolean passiveNatWorkaround;
	private Proxy proxy;
	private Integer receieveDataSocketBufferSize;
	private Integer receiveBufferSize;
	private Boolean remoteVerificationEnabled;
	private String reportActiveExternalIPAddress;
	private Integer sendBufferSize;
	private Integer sendDataSocketBufferSize;
	private Boolean strictMultilineParsing;
	private Boolean tcpNoDelay;
	private Boolean useEPSVwithIPv4;
	
	private String systemKey;
	private String defaultDateFormat;
	private String recentDateFormat;
	private String serverLanguageCode;
	private String shortMonthNames;
	private String serverTimeZoneId;
	
	private boolean localPassiveMode;

	public void setHost(String sHost) {
		host = sHost;
	}

	public void setPort(Integer sPort) {
		port = sPort;
	}

	public void setUsername(String sUsername) {
		username = sUsername;
	}

	public void setPassword(String sPassword) {
		password = sPassword;
	}

	public void setAccount(String sAccount) {
		account = sAccount;
	}

	public void setActiveExternalIPAddress(String sActiveExternalIPAddress) {
		activeExternalIPAddress = sActiveExternalIPAddress;
	}

	public void setActiveMinPort(Integer sActiveMinPort) {
		activeMinPort = sActiveMinPort;
	}

	public void setActiveMaxPort(Integer sActiveMaxPort) {
		activeMaxPort = sActiveMaxPort;
	}

	public void setAutodetectUTF8(Boolean sAutodetectUTF8) {
		autodetectUTF8 = sAutodetectUTF8;
	}

	public void setBufferSize(Integer sBufferSize) {
		bufferSize = sBufferSize;
	}

	public void setCharset(Charset sCharset) {
		charset = sCharset;
	}

	public void setConnectTimeout(Integer sConnectTimeout) {
		connectTimeout = sConnectTimeout;
	}

	public void setControlEncoding(String sControlEncoding) {
		controlEncoding = sControlEncoding;
	}

	public void setControlKeepAliveReplyTimeout(
			Integer sControlKeepAliveReplyTimeout) {
		controlKeepAliveReplyTimeout = sControlKeepAliveReplyTimeout;
	}

	public void setControlKeepAliveTimeout(Long sControlKeepAliveTimeout) {
		controlKeepAliveTimeout = sControlKeepAliveTimeout;
	}

	public void setDataTimeout(Integer sDataTimeout) {
		dataTimeout = sDataTimeout;
	}

	public void setDefaultPort(Integer sDefaultPort) {
		defaultPort = sDefaultPort;
	}

	public void setDefaultTimeout(Integer sDefaultTimeout) {
		defaultTimeout = sDefaultTimeout;
	}

	public void setFileStructure(Integer sFileStructure) {
		fileStructure = sFileStructure;
	}

	public void setFileTransferMode(Integer sFileTransferMode) {
		fileTransferMode = sFileTransferMode;
	}

	public void setFileType(Integer sFileType) {
		fileType = sFileType;
	}

	public void setKeepAlive(Boolean sKeepAlive) {
		keepAlive = sKeepAlive;
	}

	public void setListHiddenFiles(Boolean sListHiddenFiles) {
		listHiddenFiles = sListHiddenFiles;
	}

	public void setParserFactory(FTPFileEntryParserFactory sParserFactory) {
		parserFactory = sParserFactory;
	}

	public void setPassiveLocalIPAddress(String sPassiveLocalIPAddress) {
		passiveLocalIPAddress = sPassiveLocalIPAddress;
	}

	public void setPassiveNatWorkaround(Boolean sPassiveNatWorkaround) {
		passiveNatWorkaround = sPassiveNatWorkaround;
	}

	public void setProxy(Proxy sProxy) {
		proxy = sProxy;
	}

	public void setReceieveDataSocketBufferSize(
			Integer sReceieveDataSocketBufferSize) {
		receieveDataSocketBufferSize = sReceieveDataSocketBufferSize;
	}

	public void setReceiveBufferSize(Integer sReceiveBufferSize) {
		receiveBufferSize = sReceiveBufferSize;
	}

	public void setRemoteVerificationEnabled(Boolean sRemoteVerificationEnabled) {
		remoteVerificationEnabled = sRemoteVerificationEnabled;
	}

	public void setReportActiveExternalIPAddress(
			String sReportActiveExternalIPAddress) {
		reportActiveExternalIPAddress = sReportActiveExternalIPAddress;
	}

	public void setSendBufferSize(Integer sSendBufferSize) {
		sendBufferSize = sSendBufferSize;
	}

	public void setSendDataSocketBufferSize(Integer sSendDataSocketBufferSize) {
		sendDataSocketBufferSize = sSendDataSocketBufferSize;
	}

	public void setStrictMultilineParsing(Boolean sStrictMultilineParsing) {
		strictMultilineParsing = sStrictMultilineParsing;
	}

	public void setTcpNoDelay(Boolean sTcpNoDelay) {
		tcpNoDelay = sTcpNoDelay;
	}

	public void setUseEPSVwithIPv4(Boolean sUseEPSVwithIPv4) {
		useEPSVwithIPv4 = sUseEPSVwithIPv4;
	}

	public void setSystemKey(String sSystemKey) {
		systemKey = sSystemKey;
	}

	public void setDefaultDateFormat(String sDefaultDateFormat) {
		defaultDateFormat = sDefaultDateFormat;
	}

	public void setRecentDateFormat(String sRecentDateFormat) {
		recentDateFormat = sRecentDateFormat;
	}

	public void setServerLanguageCode(String sServerLanguageCode) {
		serverLanguageCode = sServerLanguageCode;
	}

	public void setShortMonthNames(String sShortMonthNames) {
		shortMonthNames = sShortMonthNames;
	}

	public void setServerTimeZoneId(String sServerTimeZoneId) {
		serverTimeZoneId = sServerTimeZoneId;
	}

	public void setLocalPassiveMode(boolean sLocalPassiveMode) {
		localPassiveMode = sLocalPassiveMode;
	}

	public FTPClient getFtpClient() throws IOException {
		FTPClient aClient = new FTPClient();
		
// Debug output
//		aClient.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
		
		if (activeExternalIPAddress != null) {
			aClient.setActiveExternalIPAddress(activeExternalIPAddress);
		}
		if (activeMinPort != null && activeMaxPort != null) {
			aClient.setActivePortRange(activeMinPort, activeMaxPort);
		}
		if (autodetectUTF8 != null) {
			aClient.setAutodetectUTF8(autodetectUTF8);
		}
		if (bufferSize != null) {
			aClient.setBufferSize(bufferSize);
		}
		if (charset != null) {
			aClient.setCharset(charset);
		}
		if (connectTimeout != null) {
			aClient.setConnectTimeout(connectTimeout);
		}
		if (controlEncoding != null) {
			aClient.setControlEncoding(controlEncoding);
		}
		if (controlKeepAliveReplyTimeout != null) {
			aClient.setControlKeepAliveReplyTimeout(controlKeepAliveReplyTimeout);
		}
		if (controlKeepAliveTimeout != null) {
			aClient.setControlKeepAliveTimeout(controlKeepAliveTimeout);
		}
		if (dataTimeout != null) {
			aClient.setDataTimeout(dataTimeout);
		}
		if (defaultPort != null) {
			aClient.setDefaultPort(defaultPort);
		}
		if (defaultTimeout != null) {
			aClient.setDefaultTimeout(defaultTimeout);
		}
		if (fileStructure != null) {
			aClient.setFileStructure(fileStructure);
		}
		if (keepAlive != null) {
			aClient.setKeepAlive(keepAlive);
		}
		if (listHiddenFiles != null) {
			aClient.setListHiddenFiles(listHiddenFiles);
		}
		if (parserFactory != null) {
			aClient.setParserFactory(parserFactory);
		}
		if (passiveLocalIPAddress != null) {
			aClient.setPassiveLocalIPAddress(passiveLocalIPAddress);
		}
		if (passiveNatWorkaround != null) {
			aClient.setPassiveNatWorkaround(passiveNatWorkaround);
		}
		if (proxy != null) {
			aClient.setProxy(proxy);
		}
		if (receieveDataSocketBufferSize != null) {
			aClient.setReceieveDataSocketBufferSize(receieveDataSocketBufferSize);
		}
		if (receiveBufferSize != null) {
			aClient.setReceiveBufferSize(receiveBufferSize);
		}
		if (remoteVerificationEnabled != null) {
			aClient.setRemoteVerificationEnabled(remoteVerificationEnabled);
		}
		if (reportActiveExternalIPAddress != null) {
			aClient.setReportActiveExternalIPAddress(reportActiveExternalIPAddress);
		}
		if (sendBufferSize != null) {
			aClient.setSendBufferSize(sendBufferSize);
		}
		if (sendDataSocketBufferSize != null) {
			aClient.setSendDataSocketBufferSize(sendDataSocketBufferSize);
		}
		if (strictMultilineParsing != null) {
			aClient.setStrictMultilineParsing(strictMultilineParsing);
		}
		if (tcpNoDelay != null) {
			aClient.setTcpNoDelay(tcpNoDelay);
		}
		if (useEPSVwithIPv4 != null) {
			aClient.setUseEPSVwithIPv4(useEPSVwithIPv4);
		}
		
		if (systemKey != null) {
			FTPClientConfig aClientConfig = new FTPClientConfig(systemKey);
			if (defaultDateFormat != null) {
				aClientConfig.setDefaultDateFormatStr(defaultDateFormat);
			}
			if (recentDateFormat != null) {
				aClientConfig.setRecentDateFormatStr(recentDateFormat);
			}
			if (serverLanguageCode != null) {
				aClientConfig.setServerLanguageCode(serverLanguageCode);
			}
			if (shortMonthNames != null) {
				aClientConfig.setShortMonthNames(shortMonthNames);
			}
			if (serverTimeZoneId != null) {
				aClientConfig.setServerTimeZoneId(serverTimeZoneId);
			}
			aClient.configure(aClientConfig);
		}
		
		if (logger.isInfoEnabled()) {
			logger.info("Connecting to : {}", host);
		}
		
		if (port == null) {
			aClient.connect(host);
		} else {
			aClient.connect(host, port);
		}
		
		int aReplyCode = aClient.getReplyCode();

	    if(!FTPReply.isPositiveCompletion(aReplyCode)) {
	    	aClient.disconnect();
	    	throw new IOException("Cannot connect to " + host + ". Returned code : " + aReplyCode);
	    }
			    
		try {
			if (localPassiveMode) {
				aClient.enterLocalPassiveMode();
			}
			
			boolean aIsLoggedIn = false;
			if (account == null) {
				aIsLoggedIn = aClient.login(username, password);
			} else {
				aIsLoggedIn = aClient.login(username, password, account);
			}
			if (!aIsLoggedIn) {
				throw new IOException(aClient.getReplyString());
			}
		} catch (IOException e) {
			aClient.disconnect();
			throw e;
		}
		
		if (fileTransferMode != null) {
			aClient.setFileTransferMode(fileTransferMode);
		}
		if (fileType != null) {
			aClient.setFileType(fileType);
		}

		return aClient;
	}
}
