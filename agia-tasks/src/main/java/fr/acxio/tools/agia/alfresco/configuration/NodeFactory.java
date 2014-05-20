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
 
import javax.xml.namespace.NamespaceContext;

import org.springframework.expression.EvaluationContext;

import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * Alfresco local representation of nodes factory.</br>
 * This factory takes a template of a tree of nodes to create
 * and build a tree of instances of nodes using an evaluation
 * context. </br>
 * The evaluation context is used to evaluate expressions
 * that can be used in every fields of the template.</br>
 * Note that the factory uses a namespace context to resolve
 * prefixes into URIs to match Alfresco namespaces, especially
 * for custom namespaces.
 * 
 * @author pcollardez
 *
 */
public interface NodeFactory {

	// FIXME : remove specific getters and setters
	
	void setNodeDefinition(NodeDefinition sNodeDefinition);

	NodeDefinition getNodeDefinition();
	
	void setNamespaceContext(NamespaceContext sNamespaceContext);
	
	NamespaceContext getNamespaceContext();
	
	/**
	 * Creates a tree of instances of nodes give an evaluation
	 * context.</br>
	 * The context can hold variables that can be used in SpEL
	 * expressions in the template set in a NodeDefinition.</br>
	 * The result is a list of nodes having the same sort order
	 * than the template tree hierarchy.
	 * 
	 * @param sContext an evaluation context for SpEL expressions
	 * @return a list of instance of nodes
	 * @throws Exception if the template tree is malformed or if
	 *         an expression cannot be evaluated.
	 */
	NodeList getNodes(EvaluationContext sContext) throws Exception;
	
}
