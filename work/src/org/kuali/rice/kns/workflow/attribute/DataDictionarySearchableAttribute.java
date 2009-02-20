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
package org.kuali.rice.kns.workflow.attribute;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemMaintenanceDocumentEntry;
import org.kuali.kfs.sys.document.service.WorkflowAttributePropertyResolutionService;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.MaintenanceDocumentEntry;
import org.kuali.rice.kns.datadictionary.SearchingAttribute;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.datadictionary.WorkflowAttributes;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;

/**
 * This class...
 */
public class DataDictionarySearchableAttribute implements SearchableAttribute {

    private static final Logger LOG = Logger.getLogger(DataDictionarySearchableAttribute.class);
    
    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchContent(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public String getSearchContent(DocumentSearchContext documentSearchContext) {

        return "";
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchStorageValues(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        
        List<SearchableAttributeValue> saValues = new ArrayList<SearchableAttributeValue>();
        
        String docId = documentSearchContext.getDocumentId();
        DocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());

        DocumentService docService = SpringContext.getBean(DocumentService.class);
        Document doc = null;
        try  {
            doc = docService.getByDocumentHeaderIdSessionless(docId);
        } catch (WorkflowException we) {
            
        }
        
        if (doc instanceof FinancialSystemMaintenanceDocument) {
            final Class<? extends BusinessObject> businessObjectClass = getBusinessObjectClass(documentSearchContext.getDocumentTypeName());
            if (businessObjectClass != null) {
                if (GlobalBusinessObject.class.isAssignableFrom(businessObjectClass)) {
                    final String documentNumber = documentSearchContext.getDocumentId();
                    final GlobalBusinessObject globalBO = retrieveGlobalBusinessObject(documentNumber, businessObjectClass);

                    if (globalBO != null) {
                        saValues.addAll(findAllSearchableAttributesForGlobalBusinessObject(globalBO));
                    }
                } else {
                    saValues.addAll(parsePrimaryKeyValuesFromDocument(businessObjectClass, (FinancialSystemMaintenanceDocument)doc));
                }
                
            }
        }
        
        WorkflowAttributes workflowAttributes = docEntry.getWorkflowAttributes();
        WorkflowAttributePropertyResolutionService waprs = SpringContext.getBean(WorkflowAttributePropertyResolutionService.class);
        saValues.addAll(waprs.resolveSearchableAttributeValues(doc, workflowAttributes));
        return saValues;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchingRows(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        
       List<Row> docSearchRows = new ArrayList<Row>();
        
       DocumentEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentSearchContext.getDocumentTypeName());
       if (entry  == null)
           return docSearchRows;
       if (entry instanceof FinancialSystemMaintenanceDocumentEntry) {
           Class<? extends BusinessObject> businessObjectClass = getBusinessObjectClass(documentSearchContext.getDocumentTypeName());
           Class<? extends Maintainable> maintainableClass = getMaintainableClass(documentSearchContext.getDocumentTypeName());

           KualiGlobalMaintainableImpl globalMaintainable = null;
           try {
               globalMaintainable = (KualiGlobalMaintainableImpl)maintainableClass.newInstance();
               businessObjectClass = globalMaintainable.getPrimaryEditedBusinessObjectClass();
           } catch (Exception ie) {
               //was not a globalMaintainable.
           }
      
           if (businessObjectClass != null)
               docSearchRows.addAll(createFieldRowsForBusinessObject(businessObjectClass));
       }
       
       WorkflowAttributes workflowAttributes = entry.getWorkflowAttributes();
       if (workflowAttributes != null)
           docSearchRows.addAll(createFieldRowsForWorkflowAttributes(workflowAttributes));
        
       return docSearchRows;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#validateUserSearchInputs(java.util.Map, org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext searchContext) {

        return null;
    }

    /**
     * Creates a list of search fields, one for each primary key of the maintained business object 
     * @param businessObjectClass the class of the maintained business object
     * @return a List of KEW search fields
     */
    protected List<Row> createFieldRowsForWorkflowAttributes(WorkflowAttributes attrs) {
        List<Row> searchFields = new ArrayList<Row>();
        
      List<SearchingTypeDefinition> searchingTypeDefinitions = attrs.getSearchingTypeDefinitions();
      for (SearchingTypeDefinition definition: searchingTypeDefinitions) {
          SearchingAttribute attr = definition.getSearchingAttribute();
          if (attr.isShowAttributeInSearchCriteria()) {
              final String attributeName = attr.getAttributeName();
              final String businessObjectClassName = attr.getBusinessObjectClassName();
              Class boClass = null;
              BusinessObject businessObject  = null;
              try {
                  boClass = Class.forName(businessObjectClassName);
                  businessObject = (BusinessObject)boClass.newInstance();
              } catch (Exception e) {
                  throw new RuntimeException(e);
              }
              
              Field searchField = FieldUtils.getPropertyField(boClass, attributeName, false);
              List displayedFieldNames = new ArrayList();
              displayedFieldNames.add(attributeName);
              LookupUtils.setFieldQuickfinder(businessObject, attributeName, searchField, displayedFieldNames);

              List<Field> fieldList = new ArrayList<Field>();
              fieldList.add(searchField);

              searchFields.add(new Row(fieldList));
          } 
      }
        
        return searchFields;
    }

    
    /**
     * 
     * @param businessObjectClass
     * @param documentContent
     * @return
     */
    protected List<SearchableAttributeValue> parsePrimaryKeyValuesFromDocument(Class<? extends BusinessObject> businessObjectClass, FinancialSystemMaintenanceDocument document) {
        List<SearchableAttributeValue> values = new ArrayList<SearchableAttributeValue>();

        final List primaryKeyNames = SpringContext.getBean(BusinessObjectMetaDataService.class).listPrimaryKeyFieldNames(businessObjectClass);

        for (Object primaryKeyNameAsObj : primaryKeyNames) {
            final String primaryKeyName = (String)primaryKeyNameAsObj;
            final SearchableAttributeValue searchableValue = parseSearchableAttributeValueForPrimaryKey(primaryKeyName, businessObjectClass, document);
            if (searchableValue != null) {
                values.add(searchableValue);
            }
        }
        return values;
    }

    /**
     * Creates a searchable attribute value for the given property name out of the document XML
     * @param propertyName the name of the property to return
     * @param businessObjectClass the class of the business object maintained
     * @param document the document XML
     * @return a generated SearchableAttributeValue, or null if a value could not be created
     */
    protected SearchableAttributeValue parseSearchableAttributeValueForPrimaryKey(String propertyName, Class<? extends BusinessObject> businessObjectClass, FinancialSystemMaintenanceDocument document) {
        
        Maintainable maintainable  = document.getNewMaintainableObject();
        PersistableBusinessObject bo = maintainable.getBusinessObject();
        
        final Object propertyValue = ObjectUtils.getPropertyValue(bo, propertyName);
        if (propertyValue == null) return null;

        SearchableAttributeStringValue value = new SearchableAttributeStringValue();
        value.setSearchableAttributeKey(propertyName);
        value.setSearchableAttributeValue(propertyValue.toString());
        return value;
    }

    /**
     * Returns the class of the object being maintained by the given maintenance document type name
     * @param documentTypeName the name of the document type to look up the maintained business object for
     * @return the class of the maintained business object
     */
    protected Class<? extends BusinessObject> getBusinessObjectClass(String documentTypeName) {
        MaintenanceDocumentEntry entry = retrieveMaintenanceDocumentEntry(documentTypeName);
        return (entry == null ? null : entry.getBusinessObjectClass());
    }
    
    /**
     * Returns the maintainable of the object being maintained by the given maintenance document type name
     * @param documentTypeName the name of the document type to look up the maintained business object for
     * @return the Maintainable of the maintained business object
     */
    protected Class<? extends Maintainable> getMaintainableClass(String documentTypeName) {
        MaintenanceDocumentEntry entry = retrieveMaintenanceDocumentEntry(documentTypeName);
        return (entry == null ? null : entry.getMaintainableClass());
    }

    
    /**
     * Retrieves the maintenance document entry for the given document type name
     * @param documentTypeName the document type name to look up the data dictionary document entry for
     * @return the corresponding data dictionary entry for a maintenance document
     */
    protected MaintenanceDocumentEntry retrieveMaintenanceDocumentEntry(String documentTypeName) {
        return (MaintenanceDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentTypeName);
    }
    
    /**
     * 
     * @param documentNumber
     * @param businessObjectClass
     * @param document
     * @return
     */
    protected GlobalBusinessObject retrieveGlobalBusinessObject(String documentNumber, Class<? extends BusinessObject> businessObjectClass) {
        GlobalBusinessObject globalBO = null;

        Map pkMap = new LinkedHashMap();
        pkMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        List returnedBOs = (List)SpringContext.getBean(BusinessObjectService.class).findMatching(businessObjectClass, pkMap);
        if (returnedBOs.size() > 0) {
            globalBO = (GlobalBusinessObject)returnedBOs.get(0);
        }

        return globalBO;
    }
 
    /**
     * 
     * @param globalBO
     * @return
     */
    protected List<SearchableAttributeValue> findAllSearchableAttributesForGlobalBusinessObject(GlobalBusinessObject globalBO) {
        List<SearchableAttributeValue> searchValues = new ArrayList<SearchableAttributeValue>();

        for (PersistableBusinessObject bo : globalBO.generateGlobalChangesToPersist()) {
            SearchableAttributeValue value = generateSearchableAttributeFromChange(bo);
            if (value != null) {
                searchValues.add(value);
            }
        }

        return searchValues;
    }

    /**
     * 
     * @param changeToPersist
     * @return
     */
    protected SearchableAttributeValue generateSearchableAttributeFromChange(PersistableBusinessObject changeToPersist) {
        List primaryKeyNames = SpringContext.getBean(BusinessObjectMetaDataService.class).listPrimaryKeyFieldNames(changeToPersist.getClass());

        for (Object primaryKeyNameAsObject : primaryKeyNames) {
            String primaryKeyName = (String)primaryKeyNameAsObject;
            Object value = ObjectUtils.getPropertyValue(changeToPersist, primaryKeyName);

            if (value != null) {
                SearchableAttributeStringValue searchableAttributeValue = new SearchableAttributeStringValue();
                searchableAttributeValue.setSearchableAttributeKey(primaryKeyName);
                searchableAttributeValue.setSearchableAttributeValue(value.toString());
                return searchableAttributeValue;
            }
        }
        return null;
    }

    /**
     * Creates a list of search fields, one for each primary key of the maintained business object 
     * @param businessObjectClass the class of the maintained business object
     * @return a List of KEW search fields
     */
    protected List<Row> createFieldRowsForBusinessObject(Class<? extends BusinessObject> businessObjectClass) {
        List<Row> searchFields = new ArrayList<Row>();

        final List primaryKeyNamesAsObjects = SpringContext.getBean(BusinessObjectMetaDataService.class).listPrimaryKeyFieldNames(businessObjectClass);
        final BusinessObjectEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(businessObjectClass.getName());
        for (Object primaryKeyNameAsObject : primaryKeyNamesAsObjects) {
            
            String attributeName =  (String)primaryKeyNameAsObject;
            BusinessObject businessObject = null;
            try {
                businessObject = businessObjectClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
            Field searchField = FieldUtils.getPropertyField(businessObjectClass, attributeName, false);
            
            List<Field> fieldList = new ArrayList<Field>();
            
            List displayedFieldNames = new ArrayList();
            displayedFieldNames.add(attributeName);
            LookupUtils.setFieldQuickfinder(businessObject, attributeName, searchField, displayedFieldNames);

            fieldList.add(searchField);
            searchFields.add(new Row(fieldList));
        }

        return searchFields;
    }
    
}
