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
 
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.springframework.batch.core.StepContribution;

public class FTPUploadTaskletTest {

	@Test
	public void testExecuteUnix() throws Exception {
		FakeFtpServer aFakeFtpServer = new FakeFtpServer();
		aFakeFtpServer.setServerControlPort(0);
		aFakeFtpServer.addUserAccount(new UserAccount("user", "password", "/"));
		
		FileSystem aFileSystem = new UnixFakeFileSystem();
		aFileSystem.add(new DirectoryEntry("/data"));
		aFileSystem.add(new FileEntry("/data/file1.txt", "file1"));
		aFileSystem.add(new FileEntry("/data/file2.html", "file2"));
		aFileSystem.add(new FileEntry("/data/file3.html", "file3"));
		aFakeFtpServer.setFileSystem(aFileSystem);

		aFakeFtpServer.start();
		int aPort = aFakeFtpServer.getServerControlPort();
		
		assertFalse(aFileSystem.exists("/data/input.csv"));
		assertFalse(aFileSystem.exists("/data/input1000.csv"));
		assertFalse(aFileSystem.exists("/data/mixedindex.csv"));
		
		FTPUploadTasklet aTasklet = new FTPUploadTasklet();
		DefaultFtpClientFactory aFtpClientFactory = new DefaultFtpClientFactory();
		aFtpClientFactory.setHost("localhost");
		aFtpClientFactory.setPort(aPort);
		aFtpClientFactory.setUsername("user");
		aFtpClientFactory.setPassword("password");
		
		aTasklet.setFtpClientFactory(aFtpClientFactory);
		
		aTasklet.setLocalBaseDir("src/test/resources/testFiles");
		aTasklet.setRemoteBaseDir("/data/");
		aTasklet.setRegexFilename(".*\\.csv");
		
		StepContribution aStepContribution = mock(StepContribution.class);
		
		aTasklet.execute(aStepContribution, null);
		
		assertTrue(aFileSystem.exists("/data/input.csv"));
		assertTrue(aFileSystem.exists("/data/input1000.csv"));
		assertTrue(aFileSystem.exists("/data/mixedindex.csv"));
		
		verify(aStepContribution, times(3)).incrementReadCount();
        verify(aStepContribution, times(3)).incrementWriteCount(1);
	}
	
	@Test
	public void testExecuteWindows() throws Exception {
		FakeFtpServer aFakeFtpServer = new FakeFtpServer();
		aFakeFtpServer.setServerControlPort(0);
		aFakeFtpServer.addUserAccount(new UserAccount("user", "password", "c:\\"));
		
		FileSystem aFileSystem = new WindowsFakeFileSystem();
		aFileSystem.add(new DirectoryEntry("c:\\data"));
		aFileSystem.add(new FileEntry("c:\\data\\file1.txt", "file1"));
		aFileSystem.add(new FileEntry("c:\\data\\file2.html", "file2"));
		aFileSystem.add(new FileEntry("c:\\data\\file3.html", "file3"));
		aFakeFtpServer.setFileSystem(aFileSystem);

		aFakeFtpServer.start();
		int aPort = aFakeFtpServer.getServerControlPort();
		
		assertFalse(aFileSystem.exists("c:\\data\\input.csv"));
		assertFalse(aFileSystem.exists("c:\\data\\input1000.csv"));
		assertFalse(aFileSystem.exists("c:\\data\\mixedindex.csv"));
		
		FTPUploadTasklet aTasklet = new FTPUploadTasklet();
		DefaultFtpClientFactory aFtpClientFactory = new DefaultFtpClientFactory();
		aFtpClientFactory.setHost("localhost");
		aFtpClientFactory.setPort(aPort);
		aFtpClientFactory.setUsername("user");
		aFtpClientFactory.setPassword("password");
		aFtpClientFactory.setSystemKey("WINDOWS");
		
		aTasklet.setFtpClientFactory(aFtpClientFactory);
		
		aTasklet.setLocalBaseDir("src/test/resources/testFiles");
		aTasklet.setRemoteBaseDir("c:/data/");
		aTasklet.setRegexFilename(".*\\.csv");
		
		aTasklet.execute(null, null);
		
		assertTrue(aFileSystem.exists("c:\\data\\input.csv"));
		assertTrue(aFileSystem.exists("c:\\data\\input1000.csv"));
		assertTrue(aFileSystem.exists("c:\\data\\mixedindex.csv"));
	}

}
