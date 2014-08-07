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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <p>
 * Spring configuration parser for
 * {@link fr.acxio.tools.agia.alfresco.configuration.DocumentDefinition
 * DocumentDefinition} .
 * </p>
 * 
 * @author pcollardez
 *
 */
public class DocumentDefinitionParser extends NodeDefinitionParser {

    private static final String NODEDEF_PROPERTIES = "properties";

    private static final String NODEDEF_ASPECTS = "aspects";

    private static final String NODEDEF_ASSOCIATIONS = "associations";

    private static final String PROPDEF_CONTENTPATH = "contentPath";

    private static final String PROPDEF_MIMETYPE = "mimeType";

    private static final String PROPDEF_ENCODING = "encoding";

    @Override
    protected Class<?> getBeanClass(Element sElement) {
        return DocumentDefinitionFactoryBean.class;
    }

    @Override
    protected void doParse(Element sElement, ParserContext sParserContext, BeanDefinitionBuilder sBuilder) {
        // sBuilder is a NodeDefinitionFactoryBean
        super.doParse(sElement, sParserContext, sBuilder);

        sBuilder.addPropertyValue("contentPath", sElement.getAttribute(PROPDEF_CONTENTPATH));
        sBuilder.addPropertyValue("mimeType", sElement.getAttribute(PROPDEF_MIMETYPE));
        sBuilder.addPropertyValue("encoding", sElement.getAttribute(PROPDEF_ENCODING));

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
                }
            }
        }
    }

}
