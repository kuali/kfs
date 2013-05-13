/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sec.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecPropertyConstants;
import org.kuali.kfs.sec.businessobject.SecurityAttributeMetadata;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;


/**
 * Calls the access security service to simulate validation for the specified user, attribute, and action
 */
public class AccessSecuritySimulationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    protected AccessSecurityService accessSecurityService;
    protected UniversityDateService universityDateService;

    protected List<Row> rows;

    public AccessSecuritySimulationLookupableHelperServiceImpl() {
        rows = null;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     *
     * KRAD Conversion: Lookupable performs customization of the search results.
     * by retrieving the default sort columns using data dictionary service.
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        String principalName = fieldValues.get(SecPropertyConstants.SECURITY_PERSON_PRINCIPAL_NAME);
        Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(principalName);

        String attributeName = fieldValues.get(SecPropertyConstants.ATTRIBUTE_NAME);
        String templateId = fieldValues.get(SecPropertyConstants.TEMPLATE_ID);

        Map<String,String> additionalPermissionDetails = new HashMap<String,String>();
        if (accessSecurityService.getInquiryWithFieldValueTemplate().getId().equals(templateId)) {
            String namespaceCode = fieldValues.get(SecPropertyConstants.INQUIRY_NAMESPACE_CODE);
            additionalPermissionDetails.put(KimConstants.AttributeConstants.NAMESPACE_CODE, namespaceCode);
        }
        else if (!accessSecurityService.getLookupWithFieldValueTemplate().getId().equals(templateId)) {
            String documentTypeCode = fieldValues.get(SecPropertyConstants.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
            additionalPermissionDetails.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeCode);
        }

        return runSimulation(person, attributeName, templateId, additionalPermissionDetails);
    }

    /**
     * @param person
     * @param attribute
     * @param templateId
     * @param additionalPermissionDetails
     * @return
     *
     * KRAD Conversion: Lookupable performs customization of the search results.
     * by retrieving the columns using data dictionary service.
     *
     */
    protected List<? extends BusinessObject> runSimulation(Person person, String attributeName, String templateId, Map<String,String> additionalPermissionDetails) {
        List<BusinessObject> resultRecords = new ArrayList<BusinessObject>();

        if (!SecConstants.ATTRIBUTE_SIMULATION_MAP.containsKey(attributeName)) {
            throw new RuntimeException("Unable to find attribute metadata for attribute: " + attributeName);
        }
        SecurityAttributeMetadata attributeMetadata = SecConstants.ATTRIBUTE_SIMULATION_MAP.get(attributeName);
        Class attributeClass = attributeMetadata.getAttributeClass();

        // build criteria for retrieving attribute records
        Map<String, Object> searchCriteria = new HashMap<String, Object>();
        List<String> fieldNames = getPersistenceStructureService().listFieldNames(attributeClass);
        if (fieldNames.contains(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
        }

        if (fieldNames.contains(KFSPropertyConstants.ACTIVE)) {
            searchCriteria.put(KFSPropertyConstants.ACTIVE, true);
        }

        // retrieve records for this attribute to iterate over and call security service
        List allAttributeData = (List) getBusinessObjectService().findMatching(attributeClass, searchCriteria);
        accessSecurityService.applySecurityRestrictions(allAttributeData, person, KimApiServiceLocator.getPermissionService().getPermissionTemplate(templateId), additionalPermissionDetails);

        // iterate through business object instances and construct simulation info result objects
        // for (Iterator iterator = allAttributeData.iterator(); iterator.hasNext();) {
        // BusinessObject businessObject = (BusinessObject) iterator.next();
        //
        // AccessSecuritySimulation securitySimulation = new AccessSecuritySimulation();
        //
        // Object boKeyFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeMetadata.getAttributeField());
        // Object boNameFieldValue = ObjectUtils.getPropertyValue(businessObject, attributeMetadata.getAttributeNameField());
        //
        // securitySimulation.setAttributeValue(boKeyFieldValue.toString());
        // securitySimulation.setAttributeValueName(boNameFieldValue.toString());
        //
        // resultRecords.add(securitySimulation);
        // }

        return allAttributeData;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        List<String> lookupFieldAttributeList = new ArrayList<String>();
        if (getParameters().containsKey(SecPropertyConstants.TEMPLATE_ID)) {
            String templateId = ((String[]) getParameters().get(SecPropertyConstants.TEMPLATE_ID))[0];

            if (accessSecurityService.getInquiryWithFieldValueTemplate().getId().equals(templateId)) {
                lookupFieldAttributeList = getInquiryTemplateFields();
            }
            else if (accessSecurityService.getLookupWithFieldValueTemplate().getId().equals(templateId)) {
                lookupFieldAttributeList = getLookupTemplateFields();
            }
            else {
                lookupFieldAttributeList = getDocumentTemplateFields();
            }
        }
        else {
            lookupFieldAttributeList = getLookupTemplateFields();
        }

        // construct field object for each search attribute
        List<Field> fields = new ArrayList<Field>();
        int numCols;
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(), getBusinessObjectClass());

            BusinessObjectEntry boe = (BusinessObjectEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(this.getBusinessObjectClass().getName());
            numCols = boe.getLookupDefinition().getNumOfColumns();

        }
        catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        if (numCols == 0) {
            numCols = KRADConstants.DEFAULT_NUM_OF_COLUMNS;
        }

        rows = FieldUtils.wrapFields(fields, numCols);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getRows()
     */
    @Override
    public List<Row> getRows() {
        return rows;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getColumns()
     *
     * KRAD Conversion: Lookupable performs the customization of columns
     * by  using data dictionary service for attribute properties.
     */
    @Override
    public List<Column> getColumns() {
        String searchAttributeName = ((String[]) getParameters().get(SecPropertyConstants.ATTRIBUTE_NAME))[0];

        SecurityAttributeMetadata attributeMetadata = SecConstants.ATTRIBUTE_SIMULATION_MAP.get(searchAttributeName);
        Class attributeClass = attributeMetadata.getAttributeClass();

        ArrayList<Column> columns = new ArrayList<Column>();
        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(attributeClass)) {
            Column column = new Column();
            column.setPropertyName(attributeName);

            String columnTitle = getDataDictionaryService().getAttributeLabel(attributeClass, attributeName);
            Boolean useShortLabel = getBusinessObjectDictionaryService().getLookupResultFieldUseShortLabel(attributeClass, attributeName);
            if (useShortLabel != null && useShortLabel) {
                columnTitle = getDataDictionaryService().getAttributeShortLabel(attributeClass, attributeName);
            }
            if (StringUtils.isBlank(columnTitle)) {
                columnTitle = getDataDictionaryService().getCollectionLabel(attributeClass, attributeName);
            }
            column.setColumnTitle(columnTitle);

            Integer fieldDefinedMaxLength = getBusinessObjectDictionaryService().getLookupResultFieldMaxLength(attributeClass, attributeName);
            if (fieldDefinedMaxLength == null) {
                try {
                    fieldDefinedMaxLength = Integer.valueOf(getParameterService().getParameterValueAsString(KRADConstants.KNS_NAMESPACE, KRADConstants.DetailTypes.LOOKUP_PARM_DETAIL_TYPE, KRADConstants.RESULTS_DEFAULT_MAX_COLUMN_LENGTH));
                }
                catch (NumberFormatException ex) {
                    LOG.error("Lookup field max length parameter not found for " + attributeClass.getName() + "." + attributeName + " -- and unable to parse default set in system parameters (RESULTS_DEFAULT_MAX_COLUMN_LENGTH).");
                    fieldDefinedMaxLength = 50;
                }
            }
            column.setMaxLength(fieldDefinedMaxLength.intValue());

            Class formatterClass = getDataDictionaryService().getAttributeFormatter(attributeClass, attributeName);
            if (formatterClass != null) {
                try {
                    column.setFormatter((Formatter) formatterClass.newInstance());
                }
                catch (InstantiationException e) {
                    LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                    throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                }
                catch (IllegalAccessException e) {
                    LOG.error("Unable to get new instance of formatter class: " + formatterClass.getName());
                    throw new RuntimeException("Unable to get new instance of formatter class: " + formatterClass.getName());
                }
            }

            columns.add(column);
        }

        return columns;
    }

    /**
     * Builds List of search field names for searching the inquiry template
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getInquiryTemplateFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.SECURITY_PERSON_PRINCIPAL_NAME);
        lookupFields.add(SecPropertyConstants.ATTRIBUTE_NAME);
        lookupFields.add(SecPropertyConstants.TEMPLATE_ID);
        lookupFields.add(SecPropertyConstants.INQUIRY_NAMESPACE_CODE);

        return lookupFields;
    }

    /**
     * Builds List of search field names for searching the lookup template
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getLookupTemplateFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.SECURITY_PERSON_PRINCIPAL_NAME);
        lookupFields.add(SecPropertyConstants.ATTRIBUTE_NAME);
        lookupFields.add(SecPropertyConstants.TEMPLATE_ID);

        return lookupFields;
    }

    /**
     * Builds List of search field names for searching document templates
     *
     * @return List<String> containing lookup field names
     */
    protected List<String> getDocumentTemplateFields() {
        List<String> lookupFields = new ArrayList<String>();

        lookupFields.add(SecPropertyConstants.SECURITY_PERSON_PRINCIPAL_NAME);
        lookupFields.add(SecPropertyConstants.ATTRIBUTE_NAME);
        lookupFields.add(SecPropertyConstants.TEMPLATE_ID);
        lookupFields.add(SecPropertyConstants.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);

        return lookupFields;
    }

    /**
     * Sets the accessSecurityService attribute value.
     *
     * @param accessSecurityService The accessSecurityService to set.
     */
    public void setAccessSecurityService(AccessSecurityService accessSecurityService) {
        this.accessSecurityService = accessSecurityService;
    }

    /**
     * Sets the universityDateService attribute value.
     *
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

}
