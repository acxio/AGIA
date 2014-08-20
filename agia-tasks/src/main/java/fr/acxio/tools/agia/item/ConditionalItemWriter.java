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
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.support.AbstractSingleVariableExpressionEvaluator;

/**
 * <p>Expression based filtering wrapper for any ItemWriter.</p>
 * <p>The condition is evaluated for each item to write. If the result of the
 * evaluation is true, the item is passed to the delegate writer.</p>
 * 
 * @author pcollardez
 *
 * @param <T> type of items to write
 */
public class ConditionalItemWriter<T> extends AbstractSingleVariableExpressionEvaluator implements ItemStreamWriter<T>, InitializingBean {

    private ItemWriter<? super T> delegate;
    private String condition;

    public void setDelegate(ItemWriter<? super T> sDelegate) {
        delegate = sDelegate;
    }

    public void setCondition(String sCondition) {
        condition = sCondition;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText(condition, "Condition must be set");
        Assert.notNull(delegate, "Delegate must be set");
    }

    @Override
    public void open(ExecutionContext sExecutionContext) throws ItemStreamException {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).open(sExecutionContext);
        }
    }

    @Override
    public void update(ExecutionContext sExecutionContext) throws ItemStreamException {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).update(sExecutionContext);
        }
    }

    @Override
    public void close() throws ItemStreamException {
        if (delegate instanceof ItemStream) {
            ((ItemStream) delegate).close();
        }
    }

    @Override
    public void write(List<? extends T> sItems) throws Exception {
        if (!sItems.isEmpty()) {
            try {
                List<T> aItems = new ArrayList<T>();
                for(T aItem : sItems) {
                    updateContext(getVariableName(), aItem, getEvaluationContext());
                    Boolean aConditionResult = getExpressionResolver().evaluate(condition, getEvaluationContext(), Boolean.class);
                    if (aConditionResult) {
                        aItems.add(aItem);
                    }
                }
                if (!aItems.isEmpty()) {
                    delegate.write(aItems);
                }
            } catch (Exception e) {
                throw new ConditionalItemWriterException(e);
            }
        }
    }

}
