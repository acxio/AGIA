package fr.acxio.tools.agia.alfresco;

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
 
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.webservice.types.CML;

/**
 * <p>Alfresco CML factory that use collections instead of arrays.</p>
 * 
 * @author pcollardez
 *
 */
public class CMLHelper {

	private List<org.alfresco.webservice.types.CMLCreate> create = new ArrayList<org.alfresco.webservice.types.CMLCreate>();
	
	private List<org.alfresco.webservice.types.CMLAddAspect> addAspect = new ArrayList<org.alfresco.webservice.types.CMLAddAspect>();
	
	private List<org.alfresco.webservice.types.CMLUpdate> update = new ArrayList<org.alfresco.webservice.types.CMLUpdate>();
	
	private List<org.alfresco.webservice.types.CMLWriteContent> writeContent = new ArrayList<org.alfresco.webservice.types.CMLWriteContent>();
	
	private List<org.alfresco.webservice.types.CMLDelete> delete = new ArrayList<org.alfresco.webservice.types.CMLDelete>();
	
	private List<org.alfresco.webservice.types.CMLCreateAssociation> createAssociations = new ArrayList<org.alfresco.webservice.types.CMLCreateAssociation>();
	
	private Set<String> existingPaths = new HashSet<String>();
	
	/**
	 * <p>Creates an empty helper, ready to accept CML commands.</p>
	 */
	public CMLHelper() {
	}
	
	/**
	 * <p>Creates a helper with the given CML commands.</p>
	 * <p>The given commands are added to the internal lists.</p>
	 * 
	 * @param sCreate an array of CMLCreate commands
	 * @param sAddAspect an array of CMLAddAspect commands
	 * @param sUpdate an array of CMLUpdate commands
	 * @param sWriteContent an array of CMLWriteContent commands
	 * @param sDelete an array of CMLDelete commands
	 * @param sCreateAssociation an array of CMLCreateAssociation commands
	 */
	public CMLHelper(org.alfresco.webservice.types.CMLCreate[] sCreate,
			org.alfresco.webservice.types.CMLAddAspect[] sAddAspect,
			org.alfresco.webservice.types.CMLUpdate[] sUpdate,
			org.alfresco.webservice.types.CMLWriteContent[] sWriteContent,
			org.alfresco.webservice.types.CMLDelete[] sDelete,
			org.alfresco.webservice.types.CMLCreateAssociation[] sCreateAssociation) {
		create.addAll(Arrays.asList(sCreate));
		addAspect.addAll(Arrays.asList(sAddAspect));
		update.addAll(Arrays.asList(sUpdate));
		writeContent.addAll(Arrays.asList(sWriteContent));
		delete.addAll(Arrays.asList(sDelete));
		createAssociations.addAll(Arrays.asList(sCreateAssociation));
	}
	
	/**
	 * <p>Adds a CMLCreate command to the list</p>
	 * 
	 * @param sCreate a CMLCreate command
	 */
	public void addCreate(org.alfresco.webservice.types.CMLCreate sCreate) {
		create.add(sCreate);
	}
	
	/**
	 * <p>Adds a CMLAddAspect command to the list</p>
	 * 
	 * @param sAddAspect a CMLAddAspect command
	 */
	public void addAddAspect(org.alfresco.webservice.types.CMLAddAspect sAddAspect) {
		addAspect.add(sAddAspect);
	}
	
	/**
	 * <p>Adds a CMLUpdate command to the list</p>
	 * 
	 * @param sUpdate a CMLUpdate command
	 */
	public void addUpdate(org.alfresco.webservice.types.CMLUpdate sUpdate) {
		update.add(sUpdate);
	}
	
	/**
	 * <p>Adds a CMLWriteContent command to the list</p>
	 * 
	 * @param sWriteContent a CMLWriteContent command
	 */
	public void addWriteContent(org.alfresco.webservice.types.CMLWriteContent sWriteContent) {
		writeContent.add(sWriteContent);
	}
	
	/**
	 * <p>Adds a CMLDelete command to the list</p>
	 * 
	 * @param sDelete a CMLDelete command
	 */
	public void addDelete(org.alfresco.webservice.types.CMLDelete sDelete) {
		delete.add(sDelete);
	}
	
	/**
	 * <p>Adds a CMLCreateAssociation command to the list</p>
	 * 
	 * @param sCreateAssociation a CMLCreateAssociation command
	 */
	public void addCreateAssociation(org.alfresco.webservice.types.CMLCreateAssociation sCreateAssociation) {
		createAssociations.add(sCreateAssociation);
	}	
	
	/**
	 * <p>Adds a path to the internal cache</p>
	 * 
	 * @param sPath a path
	 */
	public boolean addExistingPath(String sPath) {
		return existingPaths.add(sPath);
	}
	
	/**
	 * <p>Checks if the given path exists in the internal cache</p>
	 * 
	 * @param sPath a path
	 * @return {@code true} if the path is cached, {@code false} otherwise.
	 */
	public boolean isPathExist(String sPath) {
		return existingPaths.contains(sPath);
	}
	
	/**
	 * <p>Builds the Alfresco CML object from the lists of CML commands.</p>
	 * 
	 * @return a new CML object
	 */
	public CML getCML() {
		CML aCML = new CML();
		aCML.setCreate(create.toArray(new org.alfresco.webservice.types.CMLCreate[] {}));
		aCML.setAddAspect(addAspect.toArray(new org.alfresco.webservice.types.CMLAddAspect[] {}));
		aCML.setUpdate(update.toArray(new org.alfresco.webservice.types.CMLUpdate[] {}));
		aCML.setWriteContent(writeContent.toArray(new org.alfresco.webservice.types.CMLWriteContent[] {}));
		aCML.setDelete(delete.toArray(new org.alfresco.webservice.types.CMLDelete[] {}));
		aCML.setCreateAssociation(createAssociations.toArray(new org.alfresco.webservice.types.CMLCreateAssociation[] {}));
		return aCML;
		
	}
}
