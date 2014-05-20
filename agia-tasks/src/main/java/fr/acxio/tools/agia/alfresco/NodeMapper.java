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

public abstract class NodeMapper<T> {

	private NodeFactory nodeFactory;
	private String variableName = "in";
	
	private EvaluationContextFactory evaluationContextFactory;
	private StandardEvaluationContext evaluationContext;

	public NodeFactory getNodeFactory() {
		return nodeFactory;
	}

	public void setNodeFactory(NodeFactory sNodeFactory) {
		nodeFactory = sNodeFactory;
	}
	
	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String sVariableName) {
		Assert.hasText(sVariableName, "variableName must not be empty");
		variableName = sVariableName;
	}

	public synchronized EvaluationContextFactory getEvaluationContextFactory() {
		if (evaluationContextFactory == null) {
			evaluationContextFactory = new StandardEvaluationContextFactory();
		}
		return evaluationContextFactory;
	}

	public synchronized void setEvaluationContextFactory(
			EvaluationContextFactory sEvaluationContextFactory) {
		evaluationContextFactory = sEvaluationContextFactory;
	}
	
	public abstract Object transformData(T sData);
	
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
