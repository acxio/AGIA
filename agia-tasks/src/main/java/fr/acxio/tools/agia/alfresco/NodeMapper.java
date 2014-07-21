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
 
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import fr.acxio.tools.agia.alfresco.configuration.NodeFactory;
import fr.acxio.tools.agia.alfresco.domain.NodeList;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

/**
 * <p>
 * Abstract mapper which transforms any object graph into a
 * {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList}.
 * </p>
 * <p>
 * Every subclass should implement transformData to transform the object graph
 * into an appropriate object graph that will be usable into the expressions.
 * </p>
 * 
 * @author pcollardez
 *
 * @param <T> The object graph to transform
 */
public abstract class NodeMapper<T> {

	/**
	 * The node factory used to create nodes from the object graph
	 */
	private NodeFactory nodeFactory;
	
	/**
	 * The variable name of the root of the object graph.
	 * Defaulted to "in".
	 */
	private String variableName = "in";
	
	/**
	 * The evaluation context factory which may contain extra beans.
	 */
	private EvaluationContextFactory evaluationContextFactory;
	
	/**
	 * The instantiated evaluation context.
	 */
	private StandardEvaluationContext evaluationContext;

	/**
	 * Returns the node factory.
	 * 
	 * @return the node factory
	 */
	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}

	/**
	 * Sets the node factory.
	 * 
	 * @param sNodeFactory a node factory
	 */
	public void setNodeFactory(NodeFactory sNodeFactory) {
		nodeFactory = sNodeFactory;
	}
	
	/**
	 * Returns the name of the variable representing the root object.
	 * 
	 * @return the name of the variable representing the root object.
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * Sets the name of the variable representing the root object.
	 * 
	 * @param sVariableName a name for the variable representing the root
	 *        object.
	 */
	public void setVariableName(String sVariableName) {
		Assert.hasText(sVariableName, "variableName must not be empty");
		variableName = sVariableName;
	}

	/**
	 * Returns the evaluation context factory.
	 * 
	 * @return the evaluation context factory.
	 */
	public synchronized EvaluationContextFactory getEvaluationContextFactory() {
		if (evaluationContextFactory == null) {
			evaluationContextFactory = new StandardEvaluationContextFactory();
		}
		return evaluationContextFactory;
	}

	/**
	 * Sets the evaluation context factory.
	 * 
	 * @param sEvaluationContextFactory an evaluation context factory.
	 */
	public synchronized void setEvaluationContextFactory(
			EvaluationContextFactory sEvaluationContextFactory) {
		evaluationContextFactory = sEvaluationContextFactory;
	}
	
	/**
	 * Transforms the data into an object graph usable into the expressions.
	 * 
	 * @param sData the data to transform
	 * @return an object graph usable into the expressions
	 */
	public abstract Object transformData(T sData);
	
	/**
	 * <p>
	 * Transforms the data into a NodeList with the help of the node factory.
	 * </p>
	 * <p>
	 * This method calls transformData to obtain an expression friendly object
	 * graph.
	 * </p>
	 * 
	 * @param sData the data to transform
	 * @return a NodeList
	 * @throws BindException if the data cannot be used into an expression
	 */
	public synchronized NodeList objectToNodeList(T sData) throws BindException {
		NodeList aResult;
		try {
			evaluationContext = getEvaluationContextFactory().createContext(variableName, transformData(sData), evaluationContext);
			aResult = getNodeFactory().getNodes(evaluationContext);
		} catch (Exception e) {
			// FIXME : org.springframework.validation.BindException
			throw new RuntimeException("Error mapping data", e);
		}
		return aResult;
	}
}
