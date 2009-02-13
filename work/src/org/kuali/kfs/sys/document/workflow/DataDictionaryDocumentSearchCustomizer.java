/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.exception.RiceRuntimeException;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchUtils;
import org.kuali.rice.kew.docsearch.DocumentSearchColumn;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.DocumentSearchField;
import org.kuali.rice.kew.docsearch.DocumentSearchRow;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.SearchingAttribute;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Field;

public class DataDictionaryDocumentSearchCustomizer extends org.kuali.rice.kns.workflow.attribute.DataDictionaryDocumentSearchCustomizer {
    
    /**
     * Checks the Data Dictionary to verify the visibility of the fields and adds them to the result.
     * @param criteria used to get DocumentEntry
     * @return List of DocumentSearchColumns to be displayed
     */
    
    protected List<DocumentSearchColumn> getCustomDisplayColumns(DocSearchCriteriaDTO criteria) {
        List<DocumentSearchColumn> columns = new ArrayList<DocumentSearchColumn>();
        DocumentType documentType = getDocumentType(criteria.getDocTypeFullName());
        DocumentEntry entry = getDocumentEntry(documentType);
        if (entry != null && entry.getWorkflowAttributes() != null) {
            DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

            List<SearchingTypeDefinition> searchingTypeDefinitions = entry.getWorkflowAttributes().getSearchingTypeDefinitions();
            List<String> searchableAttributeFieldNames = new ArrayList<String>();
            
            for (SearchingTypeDefinition searchingTypeDefinition : searchingTypeDefinitions) {
                SearchingAttribute searchingAttribute = searchingTypeDefinition.getSearchingAttribute();
                if (searchingAttribute.isShowAttributeInResultSet()){
                    String label =  ddService.getAttributeLabel(documentType.getName(), searchingAttribute.getAttributeName());
                    searchableAttributeFieldNames.add(label);
                    addColumnUsingKey(columns, new HashMap<String,String>(), searchingAttribute.getAttributeName(), label);
                } 
            }
            addSearchableAttributeColumnsBasedOnFields(columns, getSearchCriteria(), searchableAttributeFieldNames);
        }
        
        return columns;
    }
    
    @Override
    public List<DocumentSearchColumn> getAndSetUpCustomDisplayColumns(DocSearchCriteriaDTO criteria) {
        List<DocumentSearchColumn> columns = getCustomDisplayColumns(criteria);
        return super.setUpCustomDisplayColumns(criteria, columns);
    }
    

    /**
     * Retrieves the data dictionary entry for the document being operated on by the given route context
     * @param context the current route context
     * @return the data dictionary document entry
     */
    protected DocumentEntry getDocumentEntry(DocumentType documentType) {
        return SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(documentType.getName());
    }
   
    @Override
    public boolean getOverrideSearchableAttributes() {
        return true;
    }
   

    // SEARCHABLE ATTRIBUTE IMPLEMENTATION
    private FinancialSystemSearchableAttribute searchableAttribute = new FinancialSystemSearchableAttribute();

    @Override
    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchContent(documentSearchContext);
    }
    
    @Override
    public List<DocumentSearchRow> getSearchingRows(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchingRows(documentSearchContext);
    }

    @Override
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchStorageValues(documentSearchContext);
    }

    @Override
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext searchContext) {
        return searchableAttribute.validateUserSearchInputs(paramMap, searchContext);
    }
    
    @Override
    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        DocSearchCriteriaDTO dscdto = new DocSearchCriteriaDTO();
        dscdto.setDocTypeFullName(searchCriteria.getDocTypeFullName());
        return dscdto;
    }
}
