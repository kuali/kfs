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

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MultiselectableDocSearchConversion;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchAttributeCriteriaComponent;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.datadictionary.SearchingAttribute;
import org.kuali.rice.kns.datadictionary.SearchingTypeDefinition;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Row;

public class DataDictionaryDocumentSearchCustomizer extends org.kuali.rice.kns.workflow.attribute.DataDictionaryDocumentSearchCustomizer {

    @Override
    public List<Column> constructColumnList(DocSearchCriteriaDTO criteria) {
        List<Column> tempColumns = new ArrayList<Column>();
        List<Column> customDisplayColumnNames = getAndSetUpCustomDisplayColumns(criteria);
        if ((!getShowAllStandardFields()) && (getOverrideSearchableAttributes())) {
            // use only what is contained in displayColumns
            this.addAllCustomColumns(tempColumns, criteria, customDisplayColumnNames);
        } else if (getShowAllStandardFields() && (getOverrideSearchableAttributes())) {
            // do standard fields and use displayColumns for searchable
            // attributes
            this.addStandardSearchColumns(tempColumns);
            this.addAllCustomColumns(tempColumns, criteria,customDisplayColumnNames);
        } else if ((!getShowAllStandardFields()) && (!getOverrideSearchableAttributes())) {
            // do displayColumns and then do standard searchable attributes
            this.addCustomStandardCriteriaColumns(tempColumns, criteria, customDisplayColumnNames);
            this.addSearchableAttributeColumnsNoOverrides(tempColumns,criteria);
        } else if (getShowAllStandardFields() && !getOverrideSearchableAttributes()) {
            this.addStandardSearchColumns(tempColumns);
        }

        List<Column> columns = new ArrayList<Column>();
        this.addRouteHeaderIdColumn(columns);
        columns.addAll(tempColumns);
        this.addRouteLogColumn(columns);
        return columns;
    }


    /**
     * Checks the Data Dictionary to verify the visibility of the fields and adds them to the result.
     * @param criteria used to get DocumentEntry
     * @return List of DocumentSearchColumns to be displayed
     */

    protected List<Column> getCustomDisplayColumns(DocSearchCriteriaDTO criteria) {

        SearchAttributeCriteriaComponent displayCriteria = getSearchableAttributeByFieldName("displayType");
        List<Column> columns = new ArrayList<Column>();


        boolean documentDisplay =  ((displayCriteria != null) && ("document".equals(displayCriteria.getValue())));
        if (documentDisplay) {

            DocumentType documentType = getDocumentType(criteria.getDocTypeFullName());
            DocumentEntry entry = getDocumentEntry(documentType);
            if (entry != null && entry.getWorkflowAttributes() != null) {
                DataDictionaryService ddService = SpringContext.getBean(DataDictionaryService.class);

                List<SearchingTypeDefinition> searchingTypeDefinitions = entry.getWorkflowAttributes().getSearchingTypeDefinitions();
                List<String> searchableAttributeFieldNames = new ArrayList<String>();

                for (SearchingTypeDefinition searchingTypeDefinition : searchingTypeDefinitions) {
                    SearchingAttribute searchingAttribute = searchingTypeDefinition.getSearchingAttribute();
                    if (searchingAttribute.isShowAttributeInResultSet()){
                        String label =  ddService.getAttributeLabel(searchingAttribute.getBusinessObjectClassName(), searchingAttribute.getAttributeName());
                        searchableAttributeFieldNames.add(label);
                        addColumnUsingKey(columns, searchingAttribute.getAttributeName(), label);
                    } 
                }
                addSearchableAttributeColumnsBasedOnFields(columns, getSearchCriteria(), searchableAttributeFieldNames);
            }

        }
        return columns;

    }

    @Override
    public List<Column> getAndSetUpCustomDisplayColumns(DocSearchCriteriaDTO criteria) {
        List<Column> columns = getCustomDisplayColumns(criteria);
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
    public boolean getShowAllStandardFields() {
        if (searchUsingDocumentInformationResults()) {
            return false;
        }
        return true;
    }
    
    @Override
    public boolean getOverrideSearchableAttributes() {
        // TODO Auto-generated method stub
        return false;
    }

    private boolean searchUsingDocumentInformationResults() {
        SearchAttributeCriteriaComponent displayCriteria = getSearchableAttributeByFieldName("displayType");
        return ((displayCriteria != null) && ("document".equals(displayCriteria.getValue())));
    }

    @Override
    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria) {
        DocumentEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(searchCriteria.getDocTypeFullName());
        DocSearchCriteriaDTO convertedSearchCriteria = searchCriteria;
        if (entry != null) {
            Class<? extends Document> docClass = entry.getDocumentClass();
            Document doc = null;
            try {
                doc = docClass.newInstance();
            } catch (Exception e){}
            if (doc instanceof MultiselectableDocSearchConversion) {
                MultiselectableDocSearchConversion multiselectable = (MultiselectableDocSearchConversion)doc;
                convertedSearchCriteria = multiselectable.convertSelections(searchCriteria);
            }
        }
        return super.generateSearchSql(convertedSearchCriteria);
    }

    // SEARCHABLE ATTRIBUTE IMPLEMENTATION
    private FinancialSystemSearchableAttribute searchableAttribute = new FinancialSystemSearchableAttribute();

    @Override
    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchContent(documentSearchContext);
    }

    @Override
    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
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
        // the following line can be uncommented to retain the doc type when the clear button is used on a doc search.
        //dscdto.setDocTypeFullName(searchCriteria.getDocTypeFullName());
        return dscdto;
    }
}
