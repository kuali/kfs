/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.workflow;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.MultiselectableDocSearchConversion;
import org.kuali.kfs.sys.document.workflow.FinancialSystemSearchableAttribute;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.datadictionary.DocumentEntry;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.web.ui.Row;

public class PurAPDataDictionaryDocumentSearchCustomizer extends org.kuali.rice.kns.workflow.attribute.DataDictionaryDocumentSearchCustomizer {

    /**
     * Constructs a DataDictionaryDocumentSearchCustomizer.java.
     */
    public PurAPDataDictionaryDocumentSearchCustomizer() {
        setProcessResultSet(false);
        setSearchResultProcessor( new PurAPDocumentSearchResultProcessor() );
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
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, Object> paramMap, DocumentSearchContext searchContext) {
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
