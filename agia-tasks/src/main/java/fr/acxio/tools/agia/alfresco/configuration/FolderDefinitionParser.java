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
 
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>Spring configuration parser for
 * {@link fr.acxio.tools.agia.alfresco.configuration.FolderDefinition FolderDefinition}
 * .</p>
 * 
 * @author pcollardez
 *
 */
public class FolderDefinitionParser extends NodeDefinitionParser {

	private static final String NODEDEF_PROPERTIES = "properties";
	
	private static final String NODEDEF_ASPECTS = "aspects";
	
	private static final String NODEDEF_ASSOCIATIONS = "associations";
	
	private static final String NODEDEF_FOLDER = "folder";
	
	private static final String NODEDEF_DOCUMENT = "document";
	
	private static final String PROPDEF_CONDITION = "condition";
	
	@Override
	protected Class<?> getBeanClass(Element sElement) {
		return FolderDefinitionFactoryBean.class;
	}

	@Override
	protected void doParse(Element sElement, ParserContext sParserContext, BeanDefinitionBuilder sBuilder) {
		// sBuilder is a NodeDefinitionFactoryBean
		parseFolderElement(sElement, sParserContext, sBuilder);
	}
	
	protected AbstractBeanDefinition parseFolderElement(Element sElement, ParserContext sParserContext, BeanDefinitionBuilder sBuilder) {
		ManagedList<BeanDefinition> aChildren = new ManagedList<BeanDefinition>();
		ManagedList<BeanDefinition> aDocuments = new ManagedList<BeanDefinition>();
		
		sBuilder.addPropertyValue("parent", parseFolder(sElement));

		NodeList children = sElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element) {
				String nodeName = node.getLocalName();
				Element child = (Element) node;
				if (nodeName.equals(NODEDEF_PROPERTIES)) {
					sBuilder.addPropertyValue("propertiesDefinitions", parseProperties(child, sParserContext));
				} else if (nodeName.equals(NODEDEF_ASPECTS)) {
					sBuilder.addPropertyValue("aspectsDefinitions", parseAspects(child));
				} else if (nodeName.equals(NODEDEF_ASSOCIATIONS)) {
					sBuilder.addPropertyValue("associationsDefinitions", parseAssociations(child));
				} else if (nodeName.equals(NODEDEF_FOLDER)) {
					aChildren.add(parseFolderElement(child, sParserContext, BeanDefinitionBuilder.rootBeanDefinition(FolderDefinitionFactoryBean.class)));
				} else if (nodeName.equals(NODEDEF_DOCUMENT)) {
					DocumentDefinitionParser aDocumentDefinitionParser = new DocumentDefinitionParser();
					aDocuments.add(aDocumentDefinitionParser.parse(child, sParserContext));
				}
			}
		}
		
		sBuilder.addPropertyValue("children", aChildren);
		sBuilder.addPropertyValue("documents", aDocuments);
		
		return sBuilder.getBeanDefinition();
	}
	
	protected BeanDefinition parseFolder(Element sElement) {
		BeanDefinitionBuilder aBuilder = BeanDefinitionBuilder.rootBeanDefinition(SimpleFolderDefinition.class);
		
		aBuilder.addPropertyValue("nodeType", sElement.getAttribute(PROPDEF_NODETYPE));
		aBuilder.addPropertyValue("versionOperation", sElement.getAttribute(PROPDEF_VERSIONOP));
		aBuilder.addPropertyValue("condition", sElement.getAttribute(PROPDEF_CONDITION));
		aBuilder.addPropertyValue("assocTargetId", sElement.getAttribute(PROPDEF_ASSOC_TARGET_ID));
		
		return aBuilder.getBeanDefinition();
	}

}
