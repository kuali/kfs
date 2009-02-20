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
package org.kuali.rice.kns.workflow.attribute;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.docsearch.DocSearchCriteriaDTO;
import org.kuali.rice.kew.docsearch.DocSearchDTO;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.DocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchGenerator;
import org.kuali.rice.kew.docsearch.StandardDocumentSearchResultProcessor;
import org.kuali.rice.kew.exception.WorkflowServiceError;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.web.ui.Row;

public class DataDictionaryDocumentSearchCustomizer extends StandardDocumentSearchResultProcessor implements SearchableAttribute, DocumentSearchGenerator {
    // SEARCH GENERATOR IMPLEMENTATION
    private StandardDocumentSearchGenerator documentSearchGenerator = new StandardDocumentSearchGenerator();

    public DocSearchCriteriaDTO clearSearch(DocSearchCriteriaDTO searchCriteria) {
        return documentSearchGenerator.clearSearch(searchCriteria);
    }

    public String generateSearchSql(DocSearchCriteriaDTO searchCriteria) {
        return documentSearchGenerator.generateSearchSql(searchCriteria);
    }

    public int getDocumentSearchResultSetLimit() {
        return documentSearchGenerator.getDocumentSearchResultSetLimit();
    }

    public List<WorkflowServiceError> performPreSearchConditions(String principalId, DocSearchCriteriaDTO searchCriteria) {
        return documentSearchGenerator.performPreSearchConditions(principalId, searchCriteria);
    }

    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria, String principalId) throws SQLException {
        return documentSearchGenerator.processResultSet(searchAttributeStatement, resultSet, searchCriteria, principalId);
    }

    @Deprecated
    public List<DocSearchDTO> processResultSet(Statement searchAttributeStatement, ResultSet resultSet, DocSearchCriteriaDTO searchCriteria) throws SQLException {
        return documentSearchGenerator.processResultSet(searchAttributeStatement, resultSet, searchCriteria);
    }

    public void setSearchableAttributes(List<SearchableAttribute> searchableAttributes) {
        documentSearchGenerator.setSearchableAttributes(searchableAttributes);
    }

    public void setSearchingUser(String principalId) {
        documentSearchGenerator.setSearchingUser(principalId);
    }

    public List<WorkflowServiceError> validateSearchableAttributes(DocSearchCriteriaDTO searchCriteria) {
        return documentSearchGenerator.validateSearchableAttributes(searchCriteria);
    }

    // SEARCHABLE ATTRIBUTE IMPLEMENTATION
    private SearchableAttribute searchableAttribute = new DataDictionarySearchableAttribute();

    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchContent(documentSearchContext);
    }

    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchingRows(documentSearchContext);
    }

    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        return searchableAttribute.getSearchStorageValues(documentSearchContext);
    }

    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext searchContext) {
        return searchableAttribute.validateUserSearchInputs(paramMap, searchContext);
    }
}
