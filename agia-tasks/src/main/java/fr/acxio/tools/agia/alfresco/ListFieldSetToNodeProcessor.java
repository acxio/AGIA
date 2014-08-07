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

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

import fr.acxio.tools.agia.alfresco.domain.Document;
import fr.acxio.tools.agia.alfresco.domain.Folder;
import fr.acxio.tools.agia.alfresco.domain.Node;
import fr.acxio.tools.agia.alfresco.domain.NodeList;

/**
 * <p>
 * Process a list of
 * {@link org.springframework.batch.item.file.transform.FieldSet FieldsSet} into
 * a {@link fr.acxio.tools.agia.alfresco.domain.NodeList NodeList}.
 * </p>
 * <p>
 * The input list of FieldSets represents a coherent set of documents. Each
 * document may be related to an other in this set according to associations
 * described into a
 * {@link fr.acxio.tools.agia.alfresco.configuration.NodeFactory NodeFactory} .
 * </p>
 * <p>
 * The NodeFactory is used for each FieldSet to build a NodeList. The conditions
 * defined in the NodeFactory may help when creating these nodes.
 * </p>
 * <p>
 * Resulting NodeLists are merged to remove duplicated nodes, like parents
 * folders.
 * </p>
 * <p>
 * The returned NodeList should not contain any duplicates and parent/child
 * relationships should be correct considering the NodeFactory configuration.
 * </p>
 * 
 * @author pcollardez
 *
 */
public class ListFieldSetToNodeProcessor extends AbstractFieldSetToNodeMapping implements ItemProcessor<List<FieldSet>, NodeList> {

    @Override
    public NodeList process(List<FieldSet> sItems) throws BindException {
        NodeList aResult = null;
        if (sItems != null) {
            for (FieldSet aFieldSet : sItems) {
                if (aResult == null) {
                    aResult = objectToNodeList(aFieldSet);
                } else {
                    // Rebase new nodes on existing folders
                    NodeList aNewNodeList = objectToNodeList(aFieldSet);
                    List<String> aExistingPaths = new ArrayList<String>(aResult.size());
                    for (Node aNode : aResult) {
                        aExistingPaths.add(aNode.getPath());
                    }
                    String aNewNodePath;
                    for (Node aNewNode : aNewNodeList) {
                        aNewNodePath = aNewNode.getPath();

                        if (!aExistingPaths.contains(aNewNodePath)) {
                            aResult.add(aNewNode);
                            aExistingPaths.add(aNewNodePath);
                            if (aNewNode.getParent() != null) {
                                int aParentIndex = aExistingPaths.indexOf(aNewNode.getParent().getPath());
                                Folder aParent = (Folder) aResult.get(aParentIndex);
                                aNewNode.setParent(aParent);
                                if (aNewNode instanceof Document) {
                                    aParent.addDocument((Document) aNewNode);
                                } else if (aNewNode instanceof Folder) {
                                    aParent.addFolder((Folder) aNewNode);
                                }
                            }

                        }
                    }
                }
            }
        }
        return aResult;
    }

}
