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
 * Alfresco local representation of aspects.</br>
 * The AspectDefinition describes how an aspect will be added to a node in
 * Alfresco:</br>
 * <ul>
 * <li>An aspect has a name (name) which is a qualified name.</li>
 * </ul>
 * 
 * @author pcollardez
 *
 */
public interface AspectDefinition {

	/**
	 * <p>Returns the name of the aspect</p>
	 * 
	 * @return the name of the aspect
	 */
	String getName();
}
