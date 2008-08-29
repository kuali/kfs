/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.BusinessObjectComponent;
import org.kuali.kfs.sys.businessobject.BusinessObjectProperty;
import org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.KfsBusinessObjectMetaDataService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;

public class KfsBusinessObjectMetaDataServiceImpl implements KfsBusinessObjectMetaDataService {
    private Logger LOG = Logger.getLogger(KfsBusinessObjectMetaDataServiceImpl.class);
    private DataDictionaryService dataDictionaryService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public BusinessObjectComponent getBusinessObjectComponent(String componentClass) {
        try {
            return new BusinessObjectComponent(parameterService.getNamespace(Class.forName(componentClass)), dataDictionaryService.getDataDictionary().getBusinessObjectEntry(componentClass));
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException("KfsBusinessObjectMetaDataServiceImpl unable to translate the provided componentClass String into a Class: " + componentClass, e);
        }
    }

    public BusinessObjectProperty getBusinessObjectProperty(String componentClass, String propertyName) {
        return new BusinessObjectProperty(getBusinessObjectComponent(componentClass), dataDictionaryService.getDataDictionary().getBusinessObjectEntry(componentClass).getAttributeDefinition(propertyName));
    }

    public DataMappingFieldDefinition getDataMappingFieldDefinition(String componentClass, String propertyName) {
        Map<String, String> primaryKeys = new HashMap<String, String>();
        primaryKeys.put(KFSPropertyConstants.COMPONENT_CLASS, componentClass);
        primaryKeys.put(KFSPropertyConstants.PROPERTY_NAME, propertyName);
        FunctionalFieldDescription functionalFieldDescription = (FunctionalFieldDescription) businessObjectService.findByPrimaryKey(FunctionalFieldDescription.class, primaryKeys);
        return new DataMappingFieldDefinition(functionalFieldDescription, dataDictionaryService.getDataDictionary().getBusinessObjectEntry(functionalFieldDescription.getComponentClass()), org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(functionalFieldDescription.getComponentClass()), dataDictionaryService.getDataDictionary().getBusinessObjectEntry(functionalFieldDescription.getComponentClass()).getAttributeDefinition(functionalFieldDescription.getPropertyName()));
    }

    public List<BusinessObjectComponent> findBusinessObjectComponents(String namespaceCode, String componentLabel) {
        Map<Class, BusinessObjectComponent> matchingBusinessObjectComponents = new HashMap<Class, BusinessObjectComponent>();
        Pattern componentLabelRegex = null;
        if (StringUtils.isNotBlank(componentLabel)) {
            String patternStr = componentLabel.replace("*", ".*").toUpperCase();
            try {
                componentLabelRegex = Pattern.compile(patternStr);
            }
            catch (PatternSyntaxException ex) {
                LOG.error("Unable to parse componentLabel pattern, ignoring.", ex);
            }
        }
        for (BusinessObjectEntry businessObjectEntry : dataDictionaryService.getDataDictionary().getBusinessObjectEntries().values()) {
            // TODO remove null checking on component label once that is required
            if ((StringUtils.isBlank(namespaceCode) || namespaceCode.equals(parameterService.getNamespace(businessObjectEntry.getBusinessObjectClass()))) && ((componentLabelRegex == null) || (StringUtils.isNotBlank(businessObjectEntry.getObjectLabel()) && componentLabelRegex.matcher(businessObjectEntry.getObjectLabel().toUpperCase()).matches()))) {
                matchingBusinessObjectComponents.put(businessObjectEntry.getBusinessObjectClass(), new BusinessObjectComponent(parameterService.getNamespace(businessObjectEntry.getBusinessObjectClass()), businessObjectEntry));
            }
        }
        return new ArrayList<BusinessObjectComponent>(matchingBusinessObjectComponents.values());
    }

    public List<BusinessObjectProperty> findBusinessObjectProperties(String namespaceCode, String componentLabel, String propertyLabel) {
        List<BusinessObjectComponent> businessObjectComponents = findBusinessObjectComponents(namespaceCode, componentLabel);

        Pattern propertyLabelRegex = null;
        if (StringUtils.isNotBlank(propertyLabel)) {
            String patternStr = propertyLabel.replace("*", ".*").toUpperCase();
            try {
                propertyLabelRegex = Pattern.compile(patternStr);
            }
            catch (PatternSyntaxException ex) {
                LOG.error("Unable to parse propertyLabel pattern, ignoring.", ex);
            }
        }

        List<BusinessObjectProperty> matchingBusinessObjectProperties = new ArrayList<BusinessObjectProperty>();
        for (BusinessObjectComponent businessObjectComponent : businessObjectComponents) {
            for (AttributeDefinition attributeDefinition : dataDictionaryService.getDataDictionary().getBusinessObjectEntry(businessObjectComponent.getComponentClass().toString()).getAttributes()) {
                if ((propertyLabelRegex == null) || propertyLabelRegex.matcher(attributeDefinition.getLabel().toUpperCase()).matches()) {
                    matchingBusinessObjectProperties.add(new BusinessObjectProperty(businessObjectComponent, attributeDefinition));
                }
            }
        }

        return matchingBusinessObjectProperties;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
