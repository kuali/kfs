/*
 * Copyright 2008-2009 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.bo.types.dto;

import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

/**
 * Specialization of HashMap to facilitate web services and simplify API definitions.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class AttributeDefinitionMap extends TreeMap<String,AttributeDefinition> {

	public AttributeDefinitionMap() {
		super();
	}
	
	public AttributeDefinitionMap( Map<String,AttributeDefinition> map ) {
		super( map );
	}
	
	public AttributeDefinition getByAttributeName(String attributeName) {
		for (AttributeDefinition definition : values()) {
			if (StringUtils.equals(attributeName, definition.getName())) 
				return definition;
		}
		return null;
	}
}
