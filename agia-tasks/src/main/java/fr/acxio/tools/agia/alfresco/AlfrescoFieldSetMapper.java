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
 
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>Maps a
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldsSet}
 * into a {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList}.</p>
 * 
 * @author pcollardez
 *
 */
public class AlfrescoFieldSetMapper extends AbstractFieldSetToNodeMapping implements FieldSetMapper<NodeList> {
	
	@Override
	public NodeList mapFieldSet(FieldSet sData) throws BindException {
		return objectToNodeList(sData);
	}

}
