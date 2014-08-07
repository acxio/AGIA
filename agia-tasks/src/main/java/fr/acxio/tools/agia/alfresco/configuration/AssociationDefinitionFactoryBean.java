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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * <p>
 * Abstract factory of
 * {@link fr.acxio.tools.agia.alfresco.configuration.AssociationDefinition
 * AssociationDefinition} .
 * </p>
 * 
 * @author pcollardez
 *
 * @param <T>
 *            a AssociationDefinition subclass
 */
public abstract class AssociationDefinitionFactoryBean<T> implements FactoryBean<T>, InitializingBean {

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String sType) {
        type = sType;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.hasText(type, "'type' must not be empty.");
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
