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
 
/**
 * <p>Reference association definition.</p>
 * <p>The association is represented by a reference to an local ID of target.
 * </p>
 * <p>If many local nodes have the same ID, one association will be created
 * for each of them.</p>
 * 
 * @author pcollardez
 *
 */
public interface RefAssociationDefinition extends AssociationDefinition {

	/**
	 * <p>Returns the reference of the target of the association.</p>
	 * 
	 * @return the reference of the target of the association
	 */
	String getTargetRef();
	
}
