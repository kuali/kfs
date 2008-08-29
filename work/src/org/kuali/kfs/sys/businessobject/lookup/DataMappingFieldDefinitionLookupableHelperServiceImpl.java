/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.sys.businessobject.lookup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.DataMappingFieldDefinition;
import org.kuali.kfs.sys.businessobject.FunctionalFieldDescription;
import org.kuali.kfs.sys.service.impl.KfsBusinessObjectMetaDataServiceImpl;
import org.kuali.rice.kns.bo.BusinessObject;

public class DataMappingFieldDefinitionLookupableHelperServiceImpl extends FunctionalFieldDescriptionLookupableHelperServiceImpl {
    private Logger LOG = Logger.getLogger(KfsBusinessObjectMetaDataServiceImpl.class);
    private DescriptorRepository ojbRepository;

    @Override
    public List<? extends BusinessObject> getSearchResults(java.util.Map<String, String> fieldValues) {
        fieldValues.put(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL, fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL));
        fieldValues.remove(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        fieldValues.put(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL, fieldValues.get(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL));
        fieldValues.remove(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL);
        String tableName = fieldValues.get(KFSPropertyConstants.TABLE_NAME);
        fieldValues.remove(KFSPropertyConstants.TABLE_NAME);
        String fieldName = fieldValues.get(KFSPropertyConstants.FIELD_NAME);
        fieldValues.remove(KFSPropertyConstants.FIELD_NAME);
        List<FunctionalFieldDescription> functionalFieldDescriptions = (List<FunctionalFieldDescription>) super.getSearchResults(fieldValues);
        fieldValues.put(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL, fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL));
        fieldValues.remove(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_COMPONENT_LABEL);
        fieldValues.put(KFSPropertyConstants.FUNCTIONAL_FIELD_DESCRIPTION_BUSINESS_OBJECT_PROPERTY_LABEL, fieldValues.get(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL));
        fieldValues.remove(KFSPropertyConstants.BUSINESS_OBJECT_PROPERTY_LABEL);
        fieldValues.put(KFSPropertyConstants.TABLE_NAME, tableName);
        fieldValues.put(KFSPropertyConstants.FIELD_NAME, fieldName);
        
        List<DataMappingFieldDefinition> dataMappingFieldDefinitions = new ArrayList<DataMappingFieldDefinition>();
        for (FunctionalFieldDescription functionalFieldDescription : functionalFieldDescriptions) {
            ClassDescriptor classDescriptor = getOjbRepository().getDescriptorFor(functionalFieldDescription.getComponentClass());
            if (classDescriptor.getFullTableName().equals("CA_ACCOUNT_T")) {
                LOG.debug("butt");
            }
            Pattern tableNameRegex = null;
            if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.TABLE_NAME))) {
                String patternStr = fieldValues.get(KFSPropertyConstants.TABLE_NAME).replace("*", ".*").toUpperCase();
                try {
                    tableNameRegex = Pattern.compile(patternStr);
                }
                catch (PatternSyntaxException ex) {
                    LOG.error("Unable to parse tableName pattern, ignoring.", ex);
                }
            }
            Pattern fieldNameRegex = null;
            if (StringUtils.isNotBlank(fieldValues.get(KFSPropertyConstants.FIELD_NAME))) {
                String patternStr = fieldValues.get(KFSPropertyConstants.FIELD_NAME).replace("*", ".*").toUpperCase();
                try {
                    fieldNameRegex = Pattern.compile(patternStr);
                }
                catch (PatternSyntaxException ex) {
                    LOG.error("Unable to parse fieldName pattern, ignoring.", ex);
                }
            }
            if (((tableNameRegex == null) || tableNameRegex.matcher(classDescriptor.getFullTableName().toUpperCase()).matches())
                    && ((fieldNameRegex == null) || fieldNameRegex.matcher(classDescriptor.getFieldDescriptorByName(functionalFieldDescription.getPropertyName()).getColumnName().toUpperCase()).matches())) {
                        DataMappingFieldDefinition dataMappingFieldDefinition = new DataMappingFieldDefinition(functionalFieldDescription, getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(functionalFieldDescription.getComponentClass()), classDescriptor, getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(functionalFieldDescription.getComponentClass()).getAttributeDefinition(functionalFieldDescription.getPropertyName()));
                        dataMappingFieldDefinitions.add(dataMappingFieldDefinition);
            }
        }
        return dataMappingFieldDefinitions;
    }

    private DescriptorRepository getOjbRepository() {
        if (ojbRepository == null) {
            ojbRepository = org.apache.ojb.broker.metadata.MetadataManager.getInstance().getGlobalRepository();
        }
        return ojbRepository;
    }
}
