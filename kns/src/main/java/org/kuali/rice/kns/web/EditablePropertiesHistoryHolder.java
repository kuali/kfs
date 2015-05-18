/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web;

import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KRADServiceLocator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;


/**
 * A class which will hold a Map of editable properties, dropping editable properties when too many
 * are filled in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Deprecated
public class EditablePropertiesHistoryHolder implements java.io.Serializable {
	private Map<String, Set<String>> editablePropertiesMap;
	private Integer maxLength = null;
	private Queue<String> historyOrder;
	private static final String EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME = "kns.editable.properties.history.size";
	private transient ConfigurationService configurationService;
	
	/**
	 * Constructs the EditablePropertiesHistoryHolder
	 *
	 */
	public EditablePropertiesHistoryHolder() {
		editablePropertiesMap = new HashMap<String, Set<String>>();
		historyOrder = new LinkedList<String>();
	}
	
	/**
	 * @return the maximum length of the history that this will hold
	 */
	public int getMaxHistoryLength() {
		if (maxLength == null) {
			final String historyLengthAsString = getConfigurationService().getPropertyValueAsString(
                    EditablePropertiesHistoryHolder.EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME);
			if (historyLengthAsString == null) {
				maxLength = new Integer(20);
			} else {
				try {
					maxLength = new Integer(historyLengthAsString);
				} catch (NumberFormatException nfe) {
					throw new RuntimeException("Cannot convert property "+EditablePropertiesHistoryHolder.EDITABLE_PROPERTIES_HISTORY_SIZE_PROPERTY_NAME+" with value "+historyLengthAsString+" to integer", nfe);
				}
			}
		}
		return maxLength.intValue();
	}
	
	/**
	 * Adds a Set of editable property names to the history, keyed with the given guid String.  If the editable properties exceeds the buffer size,
	 * the earliest editable properties will be bumped
	 * @param editableProperties the Set of editable property names to save in the history
	 * @return a String to act as a key (or guid) to the editable properties
	 */
	public String addEditablePropertiesToHistory(Set<String> editableProperties) {
		String guid = generateNewGuid();
		
		if (getHistoryOrder().size() > getMaxHistoryLength()) {
			final String guidForRemoval = getHistoryOrder().remove();
			getEditablePropertiesMap().remove(guidForRemoval);
		}
		getHistoryOrder().add(guid);
		getEditablePropertiesMap().put(guid, editableProperties);
		
		return guid;
	}
	
	/**
	 * 
	 * @return a newly generated Guid to act as a key to an editable properties Set
	 */
	public String generateNewGuid() {
		final String guid = UUID.randomUUID().toString();
		return guid;
	}
	
	/**
	 * Returns the editable properties registered with the current guid
	 * @param guid the guid to find editable properties for
	 * @return a Set<String> of editable properties
	 */
	public Set<String> getEditableProperties(String guid) {
		return getEditablePropertiesMap().get(guid);
	}
	
	/**
	 * Clears out the editable properties associated with the given guid
	 * @param guid the guid to clear out editable properties for
	 */
	public void clearEditableProperties(String guid) {
		getEditablePropertiesMap().put(guid, createNewEditablePropertiesEntry());
	}
	
	/**
	 * @return the order of the entries as they chronologically were created
	 */
	protected Queue<String> getHistoryOrder() {
		return historyOrder;
	}
	
	/**
	 * @return the Map which associates editable property guids with Sets of editable property names
	 */
	protected Map<String, Set<String>> getEditablePropertiesMap() {
		return editablePropertiesMap;
	}
	
	/**
	 * @return a new Entry to hold the names of editable properties
	 */
	protected Set<String> createNewEditablePropertiesEntry() {
		return new HashSet<String>();
	}
	
	/**
	 * @return an implementation of the ConfigurationService
	 */
	protected ConfigurationService getConfigurationService() {
		if (configurationService == null) {
			configurationService = KRADServiceLocator.getKualiConfigurationService();
		}
		return configurationService;
	}
}
