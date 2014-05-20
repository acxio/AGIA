package fr.acxio.tools.agia.alfresco.configuration;

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
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

/**
 * <p>Abstract NodeDefinition holding common properties.</p>
 * 
 * @author pcollardez
 *
 */
public abstract class AbstractNodeDefinition implements NodeDefinition, InitializingBean {
	
	private String nodeType;
	
	private String versionOperation;
	
	private String assocTargetId;
	
	private List<PropertyDefinition> propertiesDefinitions = new ArrayList<PropertyDefinition>();
	
	private List<AspectDefinition> aspectsDefinitions = new ArrayList<AspectDefinition>();
	
	private List<AssociationDefinition> associationsDefinitions = new ArrayList<AssociationDefinition>();

	public void addPropertyDefinition(PropertyDefinition sPropertyDefinition) {
		propertiesDefinitions.add(sPropertyDefinition);
	}

	public void addAspectDefinition(AspectDefinition sAspectDefinition) {
		aspectsDefinitions.add(sAspectDefinition);
	}
	
	public void addAssociationDefinition(AssociationDefinition sAssociationDefinition) {
		associationsDefinitions.add(sAssociationDefinition);
	}
	
	public List<PropertyDefinition> getPropertiesDefinitions() {
		return propertiesDefinitions;
	}

	public List<AspectDefinition> getAspectsDefinitions() {
		return aspectsDefinitions;
	}

	public List<AssociationDefinition> getAssociationsDefinitions() {
		return associationsDefinitions;
	}

	public void afterPropertiesSet() {
		// TODO Auto-generated method stub
		
	}

	public void setNodeType(String sNodeType) {
		nodeType = sNodeType;
	}
	
	public String getNodeType() {
		return nodeType;
	}

	public String getVersionOperation() {
		return versionOperation;
	}

	public void setVersionOperation(String sVersionOperation) {
		versionOperation = sVersionOperation;
	}

	public String getAssocTargetId() {
		return assocTargetId;
	}

	public void setAssocTargetId(String sAssocTargetId) {
		assocTargetId = sAssocTargetId;
	}

}
