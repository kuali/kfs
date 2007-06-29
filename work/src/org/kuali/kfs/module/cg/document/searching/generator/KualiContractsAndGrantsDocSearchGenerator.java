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
package org.kuali.workflow.module.cg.docsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.module.cg.lookup.keyvalues.DocumentSearchTypeOfSearchValuesFinder;

import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.QueryComponent;
import edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent;
import edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator;
import edu.iu.uis.eden.doctype.DocumentType;

/**
 * This class...
 */
public class KualiContractsAndGrantsDocSearchGenerator extends StandardDocumentSearchGenerator {
    
    private static final String SEARCH_TYPE_ATTRIBUTE_FIELD_DEF_NAME = "searchType";
    
    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#clearSearch(edu.iu.uis.eden.docsearch.DocSearchCriteriaVO)
     */
    //@Override
    public DocSearchCriteriaVO clearSearch(DocSearchCriteriaVO searchCriteria) {
        DocSearchCriteriaVO docSearchCriteriaVO = new DocSearchCriteriaVO();
        docSearchCriteriaVO.setDocTypeFullName(searchCriteria.getDocTypeFullName());
        return docSearchCriteriaVO;
    }

    /* Need to override this method because user could have selected a document type that is a proposal type but they could also
     * then select the checkbox to "only search for award documents".  To adjust for this we change the main doc type the search
     * will use.
     * 
     * (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#getDocTypeFullNameWhereSql(java.lang.String, java.lang.String)
     */
    @Override
    protected String getDocTypeFullNameWhereSql(String docTypeFullName, String whereClausePredicatePrefix) {
        // get names from search critera attribute
        List documentTypeNames = getDocTypeNames(SEARCH_TYPE_ATTRIBUTE_FIELD_DEF_NAME);
        String docTypeToSearchOn = docTypeFullName;
        boolean hasProposalDocType = false;
        boolean hasAwardDocType = false;
        // loop through attribute doc types to see if we have at least one award type as well as at least one proposal type
        for (Iterator iter = documentTypeNames.iterator(); iter.hasNext();) {
            String docTypeName = (String) iter.next();
            hasProposalDocType |= (ArrayUtils.contains(DocumentSearchTypeOfSearchValuesFinder.PROPOSAL_DOCUMENT_TYPE_NAMES, docTypeName));
            hasAwardDocType |= (ArrayUtils.contains(DocumentSearchTypeOfSearchValuesFinder.AWARD_DOCUMENT_TYPE_NAMES, docTypeName));
            if (hasAwardDocType && hasProposalDocType) {
                break;
            }
        }
        // here we check to see if search criteria says only to search proposal documents and if the given doc type to search is an award type
        if (hasProposalDocType && (!hasAwardDocType) && (ArrayUtils.contains(DocumentSearchTypeOfSearchValuesFinder.AWARD_DOCUMENT_TYPE_NAMES, docTypeFullName))) {
            return super.getDocTypeFullNameWhereSql(DocumentSearchTypeOfSearchValuesFinder.PROPOSAL_DOCUMENT_TYPE_NAMES[0], whereClausePredicatePrefix);
        } // here we check to see if search criteria says only to search award documents and if the given doc type to search is a proposal type
        else if ((!hasProposalDocType) && hasAwardDocType && (ArrayUtils.contains(DocumentSearchTypeOfSearchValuesFinder.PROPOSAL_DOCUMENT_TYPE_NAMES, docTypeFullName))) {
            return super.getDocTypeFullNameWhereSql(DocumentSearchTypeOfSearchValuesFinder.AWARD_DOCUMENT_TYPE_NAMES[0], whereClausePredicatePrefix);
        }
        // here we are safe to use the given document type
        return super.getDocTypeFullNameWhereSql(docTypeToSearchOn, whereClausePredicatePrefix);
    }

    /* (non-Javadoc)
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#addExtraDocumentTypesToSearch(java.lang.StringBuffer, edu.iu.uis.eden.doctype.DocumentType)
     */
    @Override
    protected void addExtraDocumentTypesToSearch(StringBuffer whereSql, DocumentType docType) {
        // static name below is from xml definition of search attribute
        List documentTypeNames = getDocTypeNames(SEARCH_TYPE_ATTRIBUTE_FIELD_DEF_NAME);
        for (Iterator iter = documentTypeNames.iterator(); iter.hasNext();) {
            String docTypeName = (String) iter.next();
            if (StringUtils.isNotBlank(docTypeName)) {
                addDocumentTypeNameToSearchOn(whereSql, docTypeName);
            }
        }
    }
    
    /**
     * Method being overriden to ignore search type attribute because it has no relevance for searching of actual values at this point
     * 
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#generateSearchableAttributeSql(edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent, java.lang.String, int)
     */
    @Override
    // TODO delyea - remove this once workflow is upgraded
    protected QueryComponent generateSearchableAttributeSql(SearchAttributeCriteriaComponent criteriaComponent, String whereSqlStarter, int tableIndex) {
        QueryComponent qc = new QueryComponent();
        // the search type attribute is not used in the actual search so we must ignore it
        if (!SEARCH_TYPE_ATTRIBUTE_FIELD_DEF_NAME.equals(criteriaComponent.getFormKey())) {
            qc = super.generateSearchableAttributeSql(criteriaComponent, whereSqlStarter, tableIndex);
        }
        return qc;
    }

    private List<String> getDocTypeNames(String criteriaKeyName) {
        List documentTypes = new ArrayList();
        SearchAttributeCriteriaComponent criteriaComponent = getSearchableAttributeByFieldName(criteriaKeyName);
        if (criteriaComponent != null) {
            // we know that this particular criteria component is a multibox therefor we need to use the list of values
            for (String value : criteriaComponent.getValues()) {
                if (StringUtils.isNotBlank(value)) {
                    documentTypes.addAll(Arrays.asList(value.split(DocumentSearchTypeOfSearchValuesFinder.DOCUMENT_TYPE_SEPARATOR)));
                }
            }
        }
        return documentTypes;
    }
    
    // TODO delyea - move this to standard search processor?
    private SearchAttributeCriteriaComponent getSearchableAttributeByFieldName(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Attempted to find Searchable Attribute with blank Field name '" + name + "'");
        }
        for (Iterator iter = getCriteria().getSearchableAttributes().iterator(); iter.hasNext();) {
            SearchAttributeCriteriaComponent critComponent = (SearchAttributeCriteriaComponent) iter.next();
            if (name.equals(critComponent.getFormKey())) {
                return critComponent;
            }
        }
        return null;
    }
}
