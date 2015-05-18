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
package org.kuali.rice.kns.datadictionary.validation;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.core.api.util.type.TypeUtils;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableItemDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableSectionDefinition;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.exception.AttributeValidationException;
import org.kuali.rice.krad.datadictionary.validation.DictionaryObjectAttributeValueReader;
import org.kuali.rice.krad.datadictionary.validation.capability.Constrainable;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class provides legacy processing for maintenance documents in the dictionary validation service implementation. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * @deprecated As of release 2.0
 */
@Deprecated
public class MaintenanceDocumentAttributeValueReader extends DictionaryObjectAttributeValueReader {

	protected Map<String, Class<?>> attributeTypeMap;
	protected Map<String, Object> attributeValueMap;
	//protected Map<String, PropertyDescriptor> beanInfo;
	
	private final static Logger LOG = Logger.getLogger(MaintenanceDocumentAttributeValueReader.class);
	
	private List<Constrainable> attributeDefinitions;
	private Map<String, AttributeDefinition> attributeDefinitionMap;
	
	public MaintenanceDocumentAttributeValueReader(Object object, String entryName, MaintenanceDocumentEntry entry, PersistenceStructureService persistenceStructureService) {
		super(object, entryName, entry);
		
		//if (object != null)
		//	this.beanInfo = getBeanInfo(object.getClass());
		
		this.attributeTypeMap = new HashMap<String, Class<?>>();
		this.attributeValueMap = new HashMap<String, Object>();		
		
		this.attributeDefinitions = new LinkedList<Constrainable>();
		this.attributeDefinitionMap = new HashMap<String, AttributeDefinition>();
		for (MaintainableSectionDefinition sectionDefinition : entry.getMaintainableSections()) {
			List<? extends MaintainableItemDefinition> itemDefinitions = sectionDefinition.getMaintainableItems();
			
			for (MaintainableItemDefinition itemDefinition : itemDefinitions) {
				if (itemDefinition instanceof MaintainableFieldDefinition) {
					String itemDefinitionName = itemDefinition.getName();
					AttributeDefinition attributeDefinition = KRADServiceLocatorWeb.getDataDictionaryService().getAttributeDefinition(object.getClass().getName(), itemDefinitionName);
						
						//entry.getAttributeDefinition(attributeName);
					boolean isAttributeDefined = attributeDefinition != null;
						//getDataDictionaryService().isAttributeDefined(businessObject.getClass(), itemDefinition.getName());
			        if (isAttributeDefined) {
			        	attributeDefinitions.add(attributeDefinition);
			        	attributeDefinitionMap.put(itemDefinitionName, attributeDefinition);
                        LOG.info("itemDefName: " + itemDefinitionName);

						try {
                            Object  attributeValue = PropertyUtils.getNestedProperty(object, itemDefinitionName);

							if (attributeValue != null && StringUtils.isNotBlank(attributeValue.toString())) {
				    			Class<?> propertyType = ObjectUtils.getPropertyType(object, itemDefinitionName, persistenceStructureService);
				    			attributeTypeMap.put(itemDefinitionName, propertyType);
                                if (TypeUtils.isStringClass(propertyType) || TypeUtils.isIntegralClass(propertyType) ||
                                        TypeUtils.isDecimalClass(propertyType) ||
                                        TypeUtils.isTemporalClass(propertyType) ||
                                        TypeUtils.isBooleanClass(propertyType)) {
                                    // check value format against dictionary
                                    if (!TypeUtils.isTemporalClass(propertyType)) {
                                        attributeValueMap.put(itemDefinitionName, attributeValue);
                                    }
                                }
				    		}
						} catch (IllegalArgumentException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
						} catch (IllegalAccessException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
						} catch (InvocationTargetException e) {
							LOG.warn("Failed to invoke read method on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);						
			        	} catch (NoSuchMethodException e) {
                            LOG.warn("Failed to find property description on object when looking for " + itemDefinitionName + " as a field of " + entry.getDocumentTypeName(), e);
                        }
			    		
			        }
				}
			}
		}
	}
	
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.AttributeValueReader#getDefinition(String)
	 */
	@Override
	public Constrainable getDefinition(String attributeName) {
		return attributeDefinitionMap != null ? attributeDefinitionMap.get(attributeName) : null;
	}

	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.AttributeValueReader#getDefinitions()
	 */
	@Override
	public List<Constrainable> getDefinitions() {
		return attributeDefinitions;
	}

	@Override
	public String getLabel(String attributeName) {
		AttributeDefinition attributeDefinition = attributeDefinitionMap != null ? attributeDefinitionMap.get(attributeName) : null;
		return attributeDefinition != null ? attributeDefinition.getLabel()  : attributeName;
	}
	
	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.AttributeValueReader#getType(String)
	 */
	@Override
	public Class<?> getType(String attributeName) {
		return attributeTypeMap != null ? attributeTypeMap.get(attributeName) : null;
	}

	/**
	 * @see org.kuali.rice.krad.datadictionary.validation.AttributeValueReader#getValue(String)
	 */
	@Override
	public <X> X getValue(String attributeName) throws AttributeValidationException {
		return (X) attributeValueMap.get(attributeName);
	}

	//private Map<String, PropertyDescriptor> getBeanInfo(Class<?> clazz) {
	//	final Map<String, PropertyDescriptor> properties = new HashMap<String, PropertyDescriptor>();
	//	for (PropertyDescriptor propertyDescriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
	//		properties.put(propertyDescriptor.getName(), propertyDescriptor);
	//	}
	//	return properties;
	//}
	
}
