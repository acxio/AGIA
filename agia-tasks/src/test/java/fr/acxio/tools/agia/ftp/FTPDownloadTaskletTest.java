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

import java.io.File;

import org.junit.Test;
import org.mockftpserver.fake.FakeFtpServer;
import org.mockftpserver.fake.UserAccount;
import org.mockftpserver.fake.filesystem.DirectoryEntry;
import org.mockftpserver.fake.filesystem.FileEntry;
import org.mockftpserver.fake.filesystem.FileSystem;
import org.mockftpserver.fake.filesystem.UnixFakeFileSystem;
import org.mockftpserver.fake.filesystem.WindowsFakeFileSystem;
import org.springframework.batch.core.StepContribution;

public class FTPDownloadTaskletTest {

	@Test
	public void testExecuteUnix() throws Exception {
		assertFalse(new File("target/file2.zip").exists());
		assertFalse(new File("target/file3.zip").exists());

		FakeFtpServer aFakeFtpServer = new FakeFtpServer();
		aFakeFtpServer.setServerControlPort(0);
		aFakeFtpServer.addUserAccount(new UserAccount("user", "password", "/"));
		
		FileSystem aFileSystem = new UnixFakeFileSystem();
		aFileSystem.add(new DirectoryEntry("/data"));
		aFileSystem.add(new FileEntry("/data/file1.txt", "file1"));
		aFileSystem.add(new FileEntry("/data/file2.zip", "file2"));
		aFileSystem.add(new FileEntry("/data/file3.zip", "file3"));
		aFakeFtpServer.setFileSystem(aFileSystem);

		aFakeFtpServer.start();
		int aPort = aFakeFtpServer.getServerControlPort();
		
		FTPDownloadTasklet aTasklet = new FTPDownloadTasklet();
		DefaultFtpClientFactory aFtpClientFactory = new DefaultFtpClientFactory();
		aFtpClientFactory.setHost("localhost");
		aFtpClientFactory.setPort(aPort);
		aFtpClientFactory.setUsername("user");
		aFtpClientFactory.setPassword("password");
		
		aFtpClientFactory.setFileTransferMode(2);
		aFtpClientFactory.setFileType(2);
		aFtpClientFactory.setBufferSize(100000);
		
		aTasklet.setFtpClientFactory(aFtpClientFactory);
		
		aTasklet.setLocalBaseDir("target");
		aTasklet.setRemoteBaseDir("/data/");
		aTasklet.setRegexFilename(".*\\.zip");
		
		StepContribution aStepContribution = mock(StepContribution.class);
		
		aTasklet.execute(aStepContribution, null);
		
		assertTrue(new File("target/file2.zip").exists());
		assertTrue(new File("target/file3.zip").exists());
		
		new File("target/file2.zip").delete();
		new File("target/file3.zip").delete();
		
		verify(aStepContribution, times(2)).incrementReadCount();
        verify(aStepContribution, times(2)).incrementWriteCount(1);
	}
	
	@Test
	public void testExecuteWindows() throws Exception {
		assertFalse(new File("target/file2.zip").exists());
		assertFalse(new File("target/file3.zip").exists());

		FakeFtpServer aFakeFtpServer = new FakeFtpServer();
		aFakeFtpServer.setServerControlPort(0);
		aFakeFtpServer.addUserAccount(new UserAccount("user", "password", "c:\\"));
		
		FileSystem aFileSystem = new WindowsFakeFileSystem();
		aFileSystem.add(new DirectoryEntry("c:\\data"));
		aFileSystem.add(new FileEntry("c:\\data\\file1.txt", "file1"));
		aFileSystem.add(new FileEntry("c:\\data\\file2.zip", "file2"));
		aFileSystem.add(new FileEntry("c:\\data\\file3.zip", "file3"));
		aFakeFtpServer.setFileSystem(aFileSystem);

		aFakeFtpServer.start();
		int aPort = aFakeFtpServer.getServerControlPort();
		
		FTPDownloadTasklet aTasklet = new FTPDownloadTasklet();
		DefaultFtpClientFactory aFtpClientFactory = new DefaultFtpClientFactory();
		aFtpClientFactory.setHost("localhost");
		aFtpClientFactory.setPort(aPort);
		aFtpClientFactory.setUsername("user");
		aFtpClientFactory.setPassword("password");
		aFtpClientFactory.setSystemKey("WINDOWS");
		
		aFtpClientFactory.setFileTransferMode(2);
		aFtpClientFactory.setFileType(2);
		aFtpClientFactory.setBufferSize(100000);
		
		aTasklet.setFtpClientFactory(aFtpClientFactory);
		
		aTasklet.setLocalBaseDir("target");
		aTasklet.setRemoteBaseDir("c:/data/");
		aTasklet.setRegexFilename(".*\\.zip");
		
		aTasklet.execute(null, null);
		
		assertTrue(new File("target/file2.zip").exists());
		assertTrue(new File("target/file3.zip").exists());
		
		new File("target/file2.zip").delete();
		new File("target/file3.zip").delete();
	}

}
