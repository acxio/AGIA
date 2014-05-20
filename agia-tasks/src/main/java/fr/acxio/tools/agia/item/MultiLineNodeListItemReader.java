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
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;

import fr.acxio.tools.agia.expression.DataExpressionResolver;
import fr.acxio.tools.agia.expression.EvaluationContextFactory;
import fr.acxio.tools.agia.expression.StandardDataExpressionResolver;
import fr.acxio.tools.agia.expression.StandardEvaluationContextFactory;

/**
 * <p>Item reader for records made of many lines with different mappings and
 * having non-explicit rules for detecting the ends of the records.</p>
 * <p>This item reader delegates the real reading, parsing and mapping to its
 * {@code delegate}.</p>
 * <p>The {@code newRecordCondition} is a boolean expression that can use
 * the {@code currentVariableName} and the {@code nextVariableName},
 * respectively "current" and "next" by default.</p>
 * <p>As their names are self-explanatory, these variables contain the current
 * line and the next line from the stream.</p>
 * <p>The delegate reader may return different FieldSets, having different
 * fields from one another. The condition must take this into account.</p>
 * <p>Every time the end of a record is reach in the current line, the
 * condition should evaluate to {@code true} and a list of
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldSet} is
 * returned.</p>
 * <p>The returned FieldSets may not have the same fields.</p>
 * 
 * @author pcollardez
 *
 */
public class MultiLineNodeListItemReader extends MultiLineItemReader<List<FieldSet>> {

	@Override
	public List<FieldSet> mapFieldSets(List<FieldSet> sFieldSets) {
		return sFieldSets;
	}

}
