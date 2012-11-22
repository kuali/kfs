/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.lookup;

import static org.kuali.kfs.module.tem.TemPropertyConstants.TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchResult;
import org.kuali.rice.kew.docsearch.DocumentSearchResultComponents;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.util.KEWPropertyConstants;
import org.kuali.rice.kew.web.KeyValueSort;
import org.kuali.rice.kns.web.ui.Column;

public class TravelCustomDocumentSearchResultsProcessor extends StandardDocumentSearchResultProcessor {
    
    public static Logger LOG = Logger.getLogger(TravelCustomDocumentSearchResultsProcessor.class);
    
    TravelDocumentSearchResultsCustomizer searchProcessor = null;
    
    @Override
    public DocumentSearchResultComponents processIntoFinalResults(final List<DocSearchDTO> docSearchResultRows,
            final DocSearchCriteriaDTO criteria, 
            final String principalId) {
        
        if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT)
                || criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT)
                || criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT)){
            searchProcessor = new TravelAuthorizationDocumentSearchResultProcessor(); 
        }
        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)){
            searchProcessor = new TravelReimbursementDocumentSearchResultProcessor(); 
        }
        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT)){
            searchProcessor = new TravelEntertainmentDocumentSearchResultProcessor(); 
        }
        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT)){
            searchProcessor = new TravelRelocationDocumentSearchResultProcessor(); 
        }
        try {
            
         // this.setSearchCriteria(criteria);
            this.setSearchingUser(principalId);
            final List<Column> columns = constructColumnList(criteria, docSearchResultRows);

            final List<DocumentSearchResult> documentSearchResults = new ArrayList<DocumentSearchResult>();
            for (DocSearchDTO docCriteriaDTO : docSearchResultRows) {
                DocumentSearchResult docSearchResult = generateSearchResults(docCriteriaDTO, columns);
                if (docSearchResult != null) {
                    documentSearchResults.add(docSearchResult);
                }
            }
            return new DocumentSearchResultComponents(columns, documentSearchResults);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return new DocumentSearchResultComponents(new ArrayList<Column>(), new ArrayList<DocumentSearchResult>());
        
    }
    
    @Override
    public List<Column> constructColumnList(final DocSearchCriteriaDTO criteria, final List<DocSearchDTO> docSearchResultRows) {
        List<Column> columns = super.constructColumnList(criteria, docSearchResultRows);
        columns.add(0, constructColumnUsingKey(TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS, null,null));
        return columns;
    }
    
    
    /*
     * Below methods should probably not be overridden by overriding classes but
     * could be if desired
     */
    @Override
    public Column constructColumnUsingKey(String key, String label,
            Boolean sortable) {
        LOG.debug("Constructing column with key "+ key);
        if (sortable == null) {
            // sortable = getSortableByKey().get(key);
        }
        if (label == null) {
            label = getLabelsByKey().get(key);
        }
        Column c = new Column(
                label,key);
        
        return c;
    }
    
    public DocumentSearchResult generateSearchResults(final DocSearchDTO docCriteriaDTO, final List<Column> columns) {
        LOG.debug("Searching with "+ docCriteriaDTO);
        boolean isFilteredOut = searchProcessor.filterSearchResult(docCriteriaDTO);
        
        if (isFilteredOut){
            return null;
        }
        final Map<String, Object> alternateSortValues = getSortValuesMap(docCriteriaDTO);
        
        for (final Column column: columns) {
            LOG.debug("Handling the column "+ column.getPropertyName());
            if (column.getPropertyName().equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID)){
                column.setColumnTitle(TemConstants.DOCUMENT_NUMBER);
            }
            final KeyValueSort kvs = generateSearchResult(docCriteriaDTO, column, alternateSortValues);
            LOG.debug("Got kvs result "+ kvs);
        }
        
        final DocumentSearchResult result = super.generateSearchResult(docCriteriaDTO, columns);
        
        LOG.debug("Got search results retval "+ result);
        return searchProcessor.addActionsColumn(docCriteriaDTO, result);
        
    }
    
    @Override
    public Map<String, String> getLabelsByKey() {
        Map<String, String> retval = new HashMap<String, String>(); // super.getLabelsByKey();
        retval.put(TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS, "Actions");
        return retval;
    }
}
