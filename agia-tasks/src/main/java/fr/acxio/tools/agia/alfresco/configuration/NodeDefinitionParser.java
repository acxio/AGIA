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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedList;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * <p>Abstract Spring configuration parser for 
 * {@link fr.acxio.tools.agia.alfresco.configuration.NodeDefinition NodeDefinition}
 * .</p>
 * 
 * @author pcollardez
 *
 */
public abstract class NodeDefinitionParser extends AbstractSingleBeanDefinitionParser {
	
	protected static final String NODEDEF_PROPERTY = "property";
		
	protected static final String NODEDEF_ASPECT = "aspect";
	
	protected static final String NODEDEF_ASSOC_REF = "ref";
	
	protected static final String NODEDEF_ASSOC_QUERY = "query";
	
	protected static final String PROPDEF_LOCALNAME = "localName";
	
	protected static final String PROPDEF_CONVERTERREF = "converterRef";
	
	protected static final String PROPDEF_VALUE = "value";
	
	protected static final String PROPDEF_NODETYPE = "nodeType";
	
	protected static final String PROPDEF_VERSIONOP = "versionOperation";
	
	protected static final String PROPDEF_ASSOC_TARGET_ID = "assocTargetId";

	protected static final String PROPDEF_TYPE = "type";

	protected static final String PROPDEF_QUERY_LANGUAGE = "language";
	
	@Override
	protected void doParse(Element sElement, ParserContext sParserContext, BeanDefinitionBuilder sBuilder) {
		sBuilder.addPropertyValue("nodeType", sElement.getAttribute(PROPDEF_NODETYPE));
		sBuilder.addPropertyValue("versionOperation", sElement.getAttribute(PROPDEF_VERSIONOP));
		sBuilder.addPropertyValue("assocTargetId", sElement.getAttribute(PROPDEF_ASSOC_TARGET_ID));
	}

	protected List<AspectDefinition> parseAspects(Element sElement) {
		List<Element> childElements = DomUtils.getChildElementsByTagName(sElement, NODEDEF_ASPECT);
		
		ManagedList<AspectDefinition> children = new ManagedList<AspectDefinition>(childElements.size());

		for (Element element : childElements) {
			children.add(new SimpleAspectDefinition(element.getAttribute(NAME_ATTRIBUTE)));
		}

		return children;
	}
	
	protected List<BeanDefinition> parseAssociations(Element sElement) {
		List<Element> childRefElements = DomUtils.getChildElementsByTagName(sElement, NODEDEF_ASSOC_REF);
		List<Element> childQueryElements = DomUtils.getChildElementsByTagName(sElement, NODEDEF_ASSOC_QUERY);
		
		ManagedList<BeanDefinition> children = new ManagedList<BeanDefinition>(childRefElements.size() + childQueryElements.size());

		for (Element element : childRefElements) {
			BeanDefinitionBuilder aRefAssocBuilder = BeanDefinitionBuilder.genericBeanDefinition(RefAssociationDefinitionFactoryBean.class);
			aRefAssocBuilder.addPropertyValue("type", element.getAttribute(PROPDEF_TYPE));
			aRefAssocBuilder.addPropertyValue("reference", element.getTextContent());
			children.add(aRefAssocBuilder.getBeanDefinition());
		}
		
		for (Element element : childQueryElements) {
			BeanDefinitionBuilder aQueryAssocBuilder = BeanDefinitionBuilder.genericBeanDefinition(QueryAssociationDefinitionFactoryBean.class);
			aQueryAssocBuilder.addPropertyValue("type", element.getAttribute(PROPDEF_TYPE));
			aQueryAssocBuilder.addPropertyValue("queryLanguage", element.getAttribute(PROPDEF_QUERY_LANGUAGE));
			aQueryAssocBuilder.addPropertyValue("query", element.getTextContent());
			children.add(aQueryAssocBuilder.getBeanDefinition());
		}

		return children;
	}
	
	protected List<BeanDefinition> parseProperties(Element sElement, ParserContext sParserContext) {
		List<Element> childElements = DomUtils.getChildElementsByTagName(sElement, NODEDEF_PROPERTY);
		
		ManagedList<BeanDefinition> children = new ManagedList<BeanDefinition>(childElements.size());

		for (Element element : childElements) {
			children.add(parseProperty(element, sParserContext));
		}

		return children;
	}
	
	protected BeanDefinition parseProperty(Element sElement, ParserContext sParserContext) {
		BeanDefinitionBuilder aBuilder = BeanDefinitionBuilder.genericBeanDefinition(PropertyDefinitionFactoryBean.class);
		aBuilder.addPropertyValue("localName", sElement.getAttribute(PROPDEF_LOCALNAME));
		
		String aConverterID = sElement.getAttribute(PROPDEF_CONVERTERREF);
		if (StringUtils.hasText(aConverterID)) {
			aBuilder.addPropertyReference("converter", aConverterID);
		}

		List<Element> aValuesElements = DomUtils.getChildElementsByTagName(sElement, PROPDEF_VALUE);
		ManagedList<String> aValues = new ManagedList<String>(aValuesElements.size());

		for (Element aElement : aValuesElements) {
			aValues.add(aElement.getTextContent());
		}

		aBuilder.addPropertyValue("values", aValues);
		
		return aBuilder.getBeanDefinition();
	}
}
