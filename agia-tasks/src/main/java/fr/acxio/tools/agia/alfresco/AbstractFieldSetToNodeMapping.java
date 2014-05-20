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
 
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import fr.acxio.tools.agia.alfresco.configuration.NodeFactory;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

/**
 * <p>Common processing which transform a
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldSet} into
 * a {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList}.</p>
 * 
 * @author pcollardez
 *
 */
public class AbstractFieldSetToNodeMapping extends NodeMapper<FieldSet> {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractFieldSetToNodeMapping.class);

	@Override
	public Object transformData(FieldSet sData) {
		return (sData != null) ? sData.getProperties() : null;
	}
	
}
