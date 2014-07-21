package fr.acxio.tools.agia.google;

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
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.FileCredentialStore;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Children;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.About;
import com.google.api.services.drive.model.ChildList;
import com.google.api.services.drive.model.ChildReference;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.ParentReference;
import com.google.api.services.drive.model.Property;
import com.google.api.services.drive.model.PropertyList;

public class GoogleDriveServiceImpl implements GoogleDriveService {
	
	private static final String FOLDER_MIMETYPE = "application/vnd.google-apps.folder";
	private static final String PATH_SPLIT_REGEX = "/";
	private static final Pattern PATH_EXTRACT_PATTERN = Pattern.compile("^(?:(.*)/)?([^/]*)$");

	private static Logger logger = LoggerFactory.getLogger(GoogleDriveServiceImpl.class);

	private HttpTransport httpTransport;
	private Drive drive;
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private static final String APPLICATION_NAME = "Acxio-AGIA.2";

	private Resource clientSecretsResource;
	private Resource userCredentialStoreResource;
	private VerificationCodeReceiver verificationCodeReceiver;
	private String user;
	
	private String rootFolderId = null;

	public Drive getDrive() {
		return drive;
	}

	public void setClientSecretsResource(Resource sClientSecretsResource) {
		clientSecretsResource = sClientSecretsResource;
	}

	public void setUserCredentialStoreResource(Resource sUserCredentialStoreResource) {
		userCredentialStoreResource = sUserCredentialStoreResource;
	}

	public void setVerificationCodeReceiver(
			VerificationCodeReceiver sVerificationCodeReceiver) {
		verificationCodeReceiver = sVerificationCodeReceiver;
	}

	public void setUser(String sUser) {
		user = sUser;
	}
	
	protected String getRootFolderID() throws IOException {
		if ((drive != null) && (rootFolderId == null)) {
			About aAbout = drive.about().get().execute();
			rootFolderId = aAbout.get("rootFolderId").toString();
		}
		return rootFolderId;
	}

	protected Credential authorize() throws GoogleException, GeneralSecurityException, IOException {
		// load client secrets
	    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
	        JSON_FACTORY, clientSecretsResource.getInputStream());
	    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
	        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
	    	logger.error(
	          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=drive "
	          + "into " + clientSecretsResource.getFile().getAbsolutePath());
	    	throw new GoogleException("ClientSecrets not configured");
	    }
	    // set up file credential store
	    FileCredentialStore credentialStore = new FileCredentialStore(
	    		userCredentialStoreResource.getFile(), JSON_FACTORY);
	    // set up authorization code flow
	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
	    		httpTransport, JSON_FACTORY, clientSecrets,
	        Collections.singleton(DriveScopes.DRIVE_FILE)).setCredentialStore(credentialStore).build();
	    // authorize
	    return new AuthorizationCodeInstalledApp(flow, verificationCodeReceiver).authorize(user);
	}

	
	@Override
	public void connect() throws GoogleException {
		try {
			httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			Credential credential = authorize();
			drive = new Drive.Builder(httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

		} catch (IOException e) {
			throw new GoogleException(e);
		} catch (GeneralSecurityException e) {
			throw new GoogleException(e);
		}
	}
	
	@Override
	public File createFile(String sName, java.io.File sFile, String sMimeType, Map<String, String> sProperties) throws IOException {
		File aResult = null;
		Matcher aPathMatcher = PATH_EXTRACT_PATTERN.matcher(sName);
		if (aPathMatcher.matches()) {
			String aPath = aPathMatcher.group(1);
			String aName = aPathMatcher.group(2);
			if ((aName == null) || aName.isEmpty()) {
				throw new IOException("Name is mandatory.");
			}
			String aParentID = getRootFolderID();
			if ((aPath != null) && !aPath.isEmpty()) {
				aParentID = createPath(aPath).getId();
			}
			aResult = createFile(aParentID, aName, sFile, sMimeType, sProperties);
		} else {
			throw new IOException("Name does not conform to paths syntax.");
		}
		return aResult;
	}

	@Override
	public File createFile(String sParentID, String sName, java.io.File sFile, String sMimeType, Map<String, String> sProperties) throws IOException {
		File aResult = null;
		if (drive != null) {
			File fileMetadata = new File();
		    fileMetadata.setTitle(sName);
		    ParentReference aParentReference = new ParentReference();
		    aParentReference.setId(sParentID);
		    fileMetadata.setParents(Arrays.asList(aParentReference));

		    FileContent mediaContent = new FileContent(sMimeType, sFile);

		    Drive.Files.Insert insert = drive.files().insert(fileMetadata, mediaContent);
		    MediaHttpUploader uploader = insert.getMediaHttpUploader();
		    uploader.setDirectUploadEnabled(false);
		    aResult = insert.execute();
		    
		    setProperties(aResult.getId(), sProperties);
		}
		return aResult;
	}
	
	@Override
	public File getFileByPath(String sPath) throws IOException {
		File aResult = null;
		if (drive != null) {
			String[] aSubPaths = sPath.split(PATH_SPLIT_REGEX);
			String aParentID = getRootFolderID();
			if (aSubPaths.length > 0) {
				for(int i = 0; i < (aSubPaths.length - 1); i++) {
					String aSubPath = aSubPaths[i];
					if (!aSubPath.isEmpty()) {
						ArrayList<File> aFiles = getChildrenFolders(aParentID, aSubPath);
						if (aFiles.isEmpty()) {
							throw new IOException("Folder '" + aSubPath + "' not found");
						} else {
							aResult = aFiles.get(0);
						}
						aParentID = aResult.getId();
					}
				}
				String aSubPath = aSubPaths[aSubPaths.length - 1];
				if (!aSubPath.isEmpty()) {
					ArrayList<File> aFiles = getChildren(aParentID, aSubPath);
					if (aFiles.isEmpty()) {
						throw new IOException("File '" + aSubPath + "' not found");
					} else {
						aResult = aFiles.get(0);
					}
				}
			}
		}
		return aResult;
	}

	@Override
	public File createDirectory(String sName, Map<String, String> sProperties) throws IOException {
		File aResult = null;
		Matcher aPathMatcher = PATH_EXTRACT_PATTERN.matcher(sName);
		if (aPathMatcher.matches()) {
			String aPath = aPathMatcher.group(1);
			String aName = aPathMatcher.group(2);
			if ((aName == null) || aName.isEmpty()) {
				throw new IOException("Name is mandatory.");
			}
			String aParentID = getRootFolderID();
			if ((aPath != null) && !aPath.isEmpty()) {
				aParentID = createPath(aPath).getId();
			}
			aResult = createDirectory(aParentID, aName, sProperties);
		} else {
			throw new IOException("Name does not conform to paths syntax.");
		}
		
		return aResult;
	}
	
	@Override
	public File createDirectory(String sParentID, String sName, Map<String, String> sProperties) throws IOException {
		File aResult = null;
		if (drive != null) {
			File fileMetadata = new File();
		    fileMetadata.setTitle(sName);
		    fileMetadata.setMimeType(FOLDER_MIMETYPE);
		    ParentReference aParentReference = new ParentReference();
		    aParentReference.setId(sParentID);
		    fileMetadata.setParents(Arrays.asList(aParentReference));
		    
		    Drive.Files.Insert insert = drive.files().insert(fileMetadata);
		    aResult = insert.execute();
		    
		    setProperties(aResult.getId(), sProperties);
		}
		return aResult;
	}
	
	@Override
	public File createPath(String sPath) throws IOException {
		File aResult = null;
		if (drive != null) {
			String[] aSubPaths = sPath.split(PATH_SPLIT_REGEX);
			String aParentID = getRootFolderID();
			for(String aSubPath : aSubPaths) {
				if (!aSubPath.isEmpty()) {
					ArrayList<File> aFiles = getChildrenFolders(aParentID, aSubPath);
					if (aFiles.isEmpty()) {
						aResult = createDirectory(aParentID, aSubPath, null);
					} else {
						aResult = aFiles.get(0);
					}
					aParentID = aResult.getId();
				}
			}
		}
		return aResult;
	}
	
	@Override
	public String getFolderId(String sPath) throws IOException {
		String aResult = null;
		if (drive != null) {
			String[] aSubPaths = sPath.split(PATH_SPLIT_REGEX);
			int aNbSubPaths = aSubPaths.length;
			int i = 0;
			String aParentID = getRootFolderID();
			while(i < aNbSubPaths) {
				if (!aSubPaths[i].isEmpty()) {
					ArrayList<File> aFiles = getChildrenFolders(aParentID, aSubPaths[i]);
					if (!aFiles.isEmpty()) {
						aParentID = aFiles.get(0).getId();
					} else {
						throw new IOException("Folder '" + aSubPaths[i] + "' not found.");
					}
				}
				i++;
			}
			aResult = (aNbSubPaths > 0) ? aParentID : null;
		}
		return aResult;
	}
	
	interface ChildrenFilter {
		public boolean isSelected(File sFile);
	}
	
	protected ArrayList<File> getChildrenFolders(String sFolderId, String sChildName) {
		final String aChildName = sChildName;
		return getChildren(sFolderId, new ChildrenFilter() {
			public boolean isSelected(File sFile) {
				boolean aResult = false;
				if (sFile != null) {
					String fileExtension = sFile.getFileExtension();
	                String mimeType = sFile.getMimeType();
					if (mimeType != null
	                        && FOLDER_MIMETYPE.equals(mimeType)
	                        && (fileExtension == null || fileExtension.isEmpty())) {
						aResult = ((aChildName == null) || (aChildName.equals(sFile.getTitle())));
	                }
				}
				return aResult;
			}
		});
	}
	
	protected ArrayList<File> getChildren(String sFolderId, String sChildName) {
		final String aChildName = sChildName;
		return getChildren(sFolderId, new ChildrenFilter() {
			public boolean isSelected(File sFile) {
				boolean aResult = false;
				if (sFile != null) {
					aResult = ((aChildName == null) || (aChildName.equals(sFile.getTitle())));
				}
				return aResult;
			}
		});
	}
	
	protected ArrayList<File> getChildren(String sFolderId, ChildrenFilter sFilter) {
		ArrayList<File> aResult = new ArrayList<File>();
		Children.List request = null;
	    boolean ok = true;
	    do {
	        try {
	        	request = drive.children().list(sFolderId).setMaxResults(200);
	        	ChildList files = request.execute();
	        	if (files != null) {
	        		List<ChildReference> listChildReference = files.getItems();
	                for (ChildReference childReference : listChildReference) {
	                    File file = drive.files().get(childReference.getId()).execute();
	                    if (sFilter.isSelected(file)) {
	                    	aResult.add(file);
	                    }
	                }
		            request.setPageToken(files.getNextPageToken());
	        	}
	        } catch (IOException exception) {
	            ok = false;
	        }
	    } while (ok && request.getPageToken() != null && request.getPageToken().length() > 0);

		return aResult;
	}
	
	protected void setProperties(String sNodeId, Map<String, String> sProperties) throws IOException {
		if ((sNodeId != null) && (!sNodeId.isEmpty()) && (sProperties != null)) {
	    	for(Entry<String, String>aKeyValue : sProperties.entrySet()) {
	    		Property newProperty = new Property();
	    	    newProperty.setKey(aKeyValue.getKey());
	    	    newProperty.setValue(aKeyValue.getValue());
	    	    newProperty.setVisibility("PUBLIC");
	    	    drive.properties().insert(sNodeId, newProperty).execute();
	    	}
	    }
	}
	
	@Override
	public Map<String, String> getProperties(String sFileID) throws IOException {
		Map<String, String> aResult = new HashMap<String, String>(1);
		if ((sFileID != null) && (!sFileID.isEmpty())) {
			PropertyList properties = drive.properties().list(sFileID).execute();
			for(Property aProperty : properties.getItems()) {
				aResult.put(aProperty.getKey(), aProperty.getValue());
			}
		}
		return aResult;
	}
}
