package fr.acxio.tools.agia.item;

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
 
import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.DataExpressionResolver;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardDataExpressionResolver;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

public abstract class MultiLineItemReader<T> implements ItemStreamReader<T> {

	private ItemReader<FieldSet> delegate;
	private FieldSet nextItem = null;
	private boolean delegateExhausted = false;
	
	private String newRecordCondition;
	
	private String currentVariableName = "current";
	private String nextVariableName = "next";
	
	private EvaluationContextFactory evaluationContextFactory;
	private StandardEvaluationContext evaluationContext;
	
	private DataExpressionResolver expressionResolver = new StandardDataExpressionResolver();
	
	public synchronized void setDelegate(ItemReader<FieldSet> sDelegate) {
		delegate = sDelegate;
	}
	
	public synchronized void setCurrentVariableName(String sCurrentVariableName) {
		Assert.hasText(sCurrentVariableName, "currentVariableName must not be empty");
		currentVariableName = sCurrentVariableName;
	}
	
	public synchronized void setNextVariableName(String sNextVariableName) {
		Assert.hasText(sNextVariableName, "nextVariableName must not be empty");
		nextVariableName = sNextVariableName;
	}

	public synchronized void setNewRecordCondition(String sNewRecordCondition) {
		newRecordCondition = sNewRecordCondition;
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

	@Override
	public synchronized T read() {

		List<FieldSet> aTmpResult = new ArrayList<FieldSet>();
		boolean aConditionResult = false;
		
		FieldSet line = readNextFieldSet();
		while (!aConditionResult && (line != null)) {
			aTmpResult.add(line);
			if (nextItem != null) {
				evaluationContext = getEvaluationContextFactory().createContext(currentVariableName, (line.hasNames())?line.getProperties():line.getValues(), evaluationContext);
				evaluationContext = getEvaluationContextFactory().createContext(nextVariableName, (nextItem.hasNames())?nextItem.getProperties():nextItem.getValues(), evaluationContext);
				aConditionResult = expressionResolver.evaluate(newRecordCondition, evaluationContext, Boolean.class);
			}
			if (!aConditionResult) {
				line = readNextFieldSet();
			}
		}
		
		return (aTmpResult.isEmpty() ? null : mapFieldSets(aTmpResult));
	}
	
	public abstract T mapFieldSets(List<FieldSet> sFieldSets);

	private FieldSet readNextFieldSet() {
		FieldSet returnItem = null;

		try {
			if (!delegateExhausted) {
				if (nextItem != null) {
					returnItem = nextItem;
					nextItem = null;
				} else {
					returnItem = delegate.read();
				}
	
				nextItem = delegate.read();
				if (nextItem == null) {
					delegateExhausted = true;
				}
			}
		} catch (Exception e) {
			throw new ItemReaderReadException("Cannot read next FieldSet", e);
		}
		
		return returnItem;
	}

	@Override
	public synchronized void open(ExecutionContext sExecutionContext) {
		delegateExhausted = false;
		((ItemStream)delegate).open(sExecutionContext);
	}

	@Override
	public synchronized void update(ExecutionContext sExecutionContext) {
		((ItemStream)delegate).update(sExecutionContext);
	}

	@Override
	public synchronized void close() {
		delegateExhausted = false;
		((ItemStream)delegate).close();
	}
}
