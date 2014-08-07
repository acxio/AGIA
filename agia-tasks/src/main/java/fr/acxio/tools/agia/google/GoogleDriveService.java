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
import java.util.Map;

import com.google.api.services.drive.model.File;

public interface GoogleDriveService {

    void connect() throws GoogleException;

    File createFile(String sName, java.io.File sFile, String sMimeType, Map<String, String> sProperties) throws IOException;

    File createFile(String sParentID, String sName, java.io.File sFile, String sMimeType, Map<String, String> sProperties) throws IOException;

    File getFileByPath(String sPath) throws IOException;

    Map<String, String> getProperties(String sFileID) throws IOException;

    File createDirectory(String sName, Map<String, String> sProperties) throws IOException;

    File createDirectory(String sParentID, String sName, Map<String, String> sProperties) throws IOException;

    File createPath(String sPath) throws IOException;

    String getFolderId(String sPath) throws IOException;
}
