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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants.TravelDocTypes;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.workflow.FinancialSystemSearchableAttribute;
import org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.document.attribute.DocumentAttribute;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeString;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kew.api.document.search.DocumentSearchResult;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultSetConfiguration;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValue;
import org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues;
import org.kuali.rice.kns.web.ui.Column;

public class TravelDocumentSearchCustomizer extends KFSDocumentSearchCustomizer {

    public static Logger LOG = Logger.getLogger(TravelDocumentSearchCustomizer.class);

    TravelDocumentSearchResultsCustomizer searchCustomizer = null;

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#customizeResults(org.kuali.rice.kew.api.document.search.DocumentSearchCriteria, java.util.List)
     */
    @Override
    public DocumentSearchResultValues customizeResults(DocumentSearchCriteria documentSearchCriteria, List<DocumentSearchResult> defaultResults) {

        org.kuali.rice.kew.framework.document.search.DocumentSearchResultValues.Builder customResultsBuilder = DocumentSearchResultValues.Builder.create();
        List<DocumentSearchResultValue.Builder> customResultValueBuilders = new ArrayList<DocumentSearchResultValue.Builder>();

        String documentTypeName = documentSearchCriteria.getDocumentTypeName();

        if (TravelDocTypes.getAuthorizationDocTypes().contains(documentTypeName)){
            searchCustomizer = new TravelAuthorizationDocumentSearchResultProcessor();
        }
        else if (TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT.equals(documentTypeName)){
              searchCustomizer = new TravelReimbursementDocumentSearchResultProcessor();
        }
        else if (TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT.equals(documentTypeName)){
              searchCustomizer = new TravelEntertainmentDocumentSearchResultProcessor();
        }
        else if (TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT.equals(documentTypeName)){
              searchCustomizer = new TravelRelocationDocumentSearchResultProcessor();
        }

        boolean isAuthorizedToViewPurapDocId = false;
        if ( defaultResults.size() > 0 ) {
            for (DocumentAttribute documentAttribute : defaultResults.get(0).getDocumentAttributes()) {
                if (KFSPropertyConstants.PURAP_DOC_ID.equals(documentAttribute.getName())) {
                    isAuthorizedToViewPurapDocId = isAuthorizedToViewPurapDocId(documentSearchCriteria.getDocSearchUserId());
                }
            }
        }
        for (DocumentSearchResult result : defaultResults) {
            List<DocumentAttribute.AbstractBuilder<?>> custAttrBuilders = new ArrayList<DocumentAttribute.AbstractBuilder<?>>();
            Document document = result.getDocument();

            for (DocumentAttribute documentAttribute : result.getDocumentAttributes()) {
                if (KFSPropertyConstants.PURAP_DOC_ID.equals(documentAttribute.getName())) {
                    if (!isAuthorizedToViewPurapDocId && !document.getStatus().getCategory().equals(DocumentStatusCategory.SUCCESSFUL) ) {
                        DocumentAttributeString.Builder builder = DocumentAttributeString.Builder.create(KFSPropertyConstants.PURAP_DOC_ID);
                        builder.setValue("********");
                        custAttrBuilders.add(builder);
                        break;
                    }
                }
            }
            DocumentSearchResultValue.Builder builder = DocumentSearchResultValue.Builder.create(document.getDocumentId());
            builder.setDocumentAttributes(custAttrBuilders);
            customResultValueBuilders.add(builder);
        }
        customResultsBuilder.setResultValues(customResultValueBuilders);

        return customResultsBuilder.build();
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#customizeResultSetConfiguration(org.kuali.rice.kew.api.document.search.DocumentSearchCriteria)
     */
    @Override
    public DocumentSearchResultSetConfiguration customizeResultSetConfiguration(DocumentSearchCriteria documentSearchCriteria) {
        
        DocumentSearchResultSetConfiguration.Builder config = DocumentSearchResultSetConfiguration.Builder.create(super.customizeResultSetConfiguration(documentSearchCriteria));

        List<String> displayTypeList = documentSearchCriteria.getSearchOptions().get(FinancialSystemSearchableAttribute.DISPLAY_TYPE_SEARCH_ATTRIBUTE_NAME);
        if ( displayTypeList != null && !displayTypeList.isEmpty() ) {

            String displayType =  displayTypeList.get(0);
            if ( StringUtils.equals(displayType, FinancialSystemSearchableAttribute.WORKFLOW_DISPLAY_TYPE_VALUE)) {
                config.setOverrideSearchableAttributes(true);
                config.setStandardResultFieldsToRemove(null);
            }
        }
        return config.build();
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.KFSDocumentSearchCustomizer#isCustomizeResultsEnabled(java.lang.String)
     */
    @Override
    public boolean isCustomizeResultsEnabled(String documentTypeName) {
        return true;
    }



    @Override
    public DocumentSearchResultComponents processIntoFinalResults(final List<DocSearchDTO> docSearchResultRows,
            final DocSearchCriteriaDTO criteria,
            final String principalId) {

//        if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT)
//                || criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT)
//                || criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT)){
//            searchCustomizer = new TravelAuthorizationDocumentSearchResultProcessor();
//        }
//        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_DOCUMENT)){
//            searchCustomizer = new TravelReimbursementDocumentSearchResultProcessor();
//        }
//        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_ENTERTAINMENT_DOCUMENT)){
//            searchCustomizer = new TravelEntertainmentDocumentSearchResultProcessor();
//        }
//        else if (criteria.getDocTypeFullName().equals(TemConstants.TravelDocTypes.TRAVEL_RELOCATION_DOCUMENT)){
//            searchCustomizer = new TravelRelocationDocumentSearchResultProcessor();
//        }
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
//
//    @Override
//    public List<Column> constructColumnList(final DocSearchCriteriaDTO criteria, final List<DocSearchDTO> docSearchResultRows) {
//        List<Column> columns = super.constructColumnList(criteria, docSearchResultRows);
//        columns.add(0, constructColumnUsingKey(TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS, null,null));
//        return columns;
//    }
//
//
//    /*
//     * Below methods should probably not be overridden by overriding classes but
//     * could be if desired
//     */
//    @Override
//    public Column constructColumnUsingKey(String key, String label,
//            Boolean sortable) {
//        LOG.debug("Constructing column with key "+ key);
//        if (sortable == null) {
//            // sortable = getSortableByKey().get(key);
//        }
//        if (label == null) {
//            label = getLabelsByKey().get(key);
//        }
//        Column c = new Column(
//                label,key);
//
//        return c;
//    }
//
//    public DocumentSearchResult generateSearchResults(final DocSearchDTO docCriteriaDTO, final List<Column> columns) {
//        LOG.debug("Searching with "+ docCriteriaDTO);
//        boolean isFilteredOut = searchProcessor.filterSearchResult(docCriteriaDTO);
//
//        if (isFilteredOut){
//            return null;
//        }
//        final Map<String, Object> alternateSortValues = getSortValuesMap(docCriteriaDTO);
//
//        for (final Column column: columns) {
//            LOG.debug("Handling the column "+ column.getPropertyName());
//            if (column.getPropertyName().equals(KEWPropertyConstants.DOC_SEARCH_RESULT_PROPERTY_NAME_ROUTE_HEADER_ID)){
//                column.setColumnTitle(TemConstants.DOCUMENT_NUMBER);
//            }
//            final KeyValueSort kvs = generateSearchResult(docCriteriaDTO, column, alternateSortValues);
//            LOG.debug("Got kvs result "+ kvs);
//        }
//
//        final DocumentSearchResult result = super.generateSearchResult(docCriteriaDTO, columns);
//
//        LOG.debug("Got search results retval "+ result);
//        return searchProcessor.addActionsColumn(docCriteriaDTO, result);
//
//    }
//
//    @Override
//    public Map<String, String> getLabelsByKey() {
//        Map<String, String> retval = new HashMap<String, String>(); // super.getLabelsByKey();
//        retval.put(TRVL_DOC_SEARCH_RESULT_PROPERTY_NAME_ACTIONS, "Actions");
//        return retval;
//    }
}
