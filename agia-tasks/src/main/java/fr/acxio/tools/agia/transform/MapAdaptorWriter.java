package fr.acxio.tools.agia.transform;

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
import java.util.Map;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.item.ItemWriter;

public class MapAdaptorWriter<T> implements ItemStreamWriter<Map<String, Object>> {

    private ItemWriter<? super T> delegate;
    private String key;

    public void setDelegate(ItemWriter<? super T> sDelegate) {
        delegate = sDelegate;
    }

    public void setKey(String sKey) {
        key = sKey;
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
    public void write(List<? extends Map<String, Object>> sItems) throws Exception {
        if ((delegate != null) && (sItems != null) && !sItems.isEmpty()) {
            List<T> aValues = new ArrayList<T>(sItems.size());
            for(Map<String, Object> aItem : sItems) {
                aValues.add((T)aItem.get(key));
            }
            delegate.write(aValues);
        }
    }
    
   

}
