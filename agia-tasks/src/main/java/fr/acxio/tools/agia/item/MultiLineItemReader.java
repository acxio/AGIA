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
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.support.AbstractExpressionEvaluator;

/**
 * <p>
 * Item reader for records made of many lines with different mappings and having
 * non-explicit rules for detecting the ends of the records.
 * </p>
 * <p>
 * This item reader delegates the real reading, parsing and mapping to its
 * {@code delegate}.
 * </p>
 * <p>
 * The {@code newRecordCondition} is a boolean expression that can use the
 * {@code currentVariableName} and the {@code nextVariableName}, respectively
 * "current" and "next" by default.
 * </p>
 * <p>
 * As their names are self-explanatory, these variables contain the current line
 * and the next line from the stream.
 * </p>
 * <p>
 * The delegate reader may return different FieldSets, having different fields
 * from one another. The condition must take this into account.
 * </p>
 * <p>
 * Every time the end of a record is reach in the current line, the condition
 * should evaluate to {@code true} and a list of
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldSet} is
 * returned.
 * </p>
 * <p>
 * The returned FieldSets by the delegate reader may not have the same fields.
 * </p>
 * <p>Subclasses must implement {@code mapFieldSets} to transform the list
 * of read FieldSet into items.</p>
 * 
 * @param <T> The type of the returned items
 * 
 * @author pcollardez
 *
 */
public abstract class MultiLineItemReader<T> extends AbstractExpressionEvaluator implements ItemStreamReader<T> {

    private ItemReader<FieldSet> delegate;
    private FieldSet nextItem = null;
    private boolean delegateExhausted = false;

    private String newRecordCondition;

    private String currentVariableName = "current";
    private String nextVariableName = "next";

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


    @Override
    public synchronized T read() {

        List<FieldSet> aTmpResult = new ArrayList<FieldSet>();
        boolean aConditionResult = false;

        FieldSet line = readNextFieldSet();
        while (!aConditionResult && (line != null)) {
            aTmpResult.add(line);
            if (nextItem != null) {
                updateContext(currentVariableName, (line.hasNames()) ? line.getProperties() : line.getValues(), getEvaluationContext());
                updateContext(nextVariableName, (nextItem.hasNames()) ? nextItem.getProperties() : nextItem.getValues(), getEvaluationContext());
                aConditionResult = getExpressionResolver().evaluate(newRecordCondition, getEvaluationContext(), Boolean.class);
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
        ((ItemStream) delegate).open(sExecutionContext);
    }

    @Override
    public synchronized void update(ExecutionContext sExecutionContext) {
        ((ItemStream) delegate).update(sExecutionContext);
    }

    @Override
    public synchronized void close() {
        delegateExhausted = false;
        ((ItemStream) delegate).close();
    }
}
