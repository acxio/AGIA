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

import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * <p>
 * Abstract factory of
 * {@link fr.acxio.tools.agia.alfresco.configuration.NodeDefinition
 * NodeDefinition} .
 * </p>
 * 
 * @author pcollardez
 */
public abstract class NodeDefinitionFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private String nodeType;

    private String versionOperation;

    private String assocTargetId;

    private List<PropertyDefinition> propertiesDefinitions;

    private List<AspectDefinition> aspectsDefinitions;

    private List<AssociationDefinition> associationsDefinitions;

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String sNodeType) {
        nodeType = sNodeType;
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

    public List<PropertyDefinition> getPropertiesDefinitions() {
        return propertiesDefinitions;
    }

    public void setPropertiesDefinitions(List<PropertyDefinition> sPropertiesDefinitions) {
        propertiesDefinitions = sPropertiesDefinitions;
    }

    public List<AspectDefinition> getAspectsDefinitions() {
        return aspectsDefinitions;
    }

    public void setAspectsDefinitions(List<AspectDefinition> sAspectsDefinitions) {
        aspectsDefinitions = sAspectsDefinitions;
    }

    public List<AssociationDefinition> getAssociationsDefinitions() {
        return associationsDefinitions;
    }

    public void setAssociationsDefinitions(List<AssociationDefinition> sAssociationsDefinitions) {
        associationsDefinitions = sAssociationsDefinitions;
    }

    public void afterPropertiesSet() {
        Assert.notEmpty(propertiesDefinitions, "The node definition must have some properties");
    }

    public boolean isSingleton() {
        return true;
    }

    protected void addPropertiesToNodeDefinition(NodeDefinition sNodeDefinition) {
        if (propertiesDefinitions != null && propertiesDefinitions.size() > 0) {
            for (PropertyDefinition aProperty : propertiesDefinitions) {
                sNodeDefinition.addPropertyDefinition(aProperty);
            }
        }
    }

    protected void addAspectsToNodeDefinition(NodeDefinition sNodeDefinition) {
        if (aspectsDefinitions != null && aspectsDefinitions.size() > 0) {
            for (AspectDefinition aAspect : aspectsDefinitions) {
                sNodeDefinition.addAspectDefinition(aAspect);
            }
        }
    }

    protected void addAssociationsToNodeDefinition(NodeDefinition sNodeDefinition) {
        if (associationsDefinitions != null && associationsDefinitions.size() > 0) {
            for (AssociationDefinition aAssociation : associationsDefinitions) {
                sNodeDefinition.addAssociationDefinition(aAssociation);
            }
        }
    }

}
