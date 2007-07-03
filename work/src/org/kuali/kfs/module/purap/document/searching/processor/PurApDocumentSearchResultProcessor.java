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
package org.kuali.workflow.module.purap.docsearch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.iu.uis.eden.docsearch.DocSearchCriteriaVO;
import edu.iu.uis.eden.docsearch.DocSearchVO;
import edu.iu.uis.eden.docsearch.DocumentSearchResultComponents;
import edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent;
import edu.iu.uis.eden.docsearch.StandardDocumentSearchResultProcessor;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;
import edu.iu.uis.eden.user.WorkflowUser;

public abstract class PurApDocumentSearchResultProcessor extends StandardDocumentSearchResultProcessor {

    private static final String DOC_SEARCH_RESULTS_STANDARD_WORKFLOW = "workflow_standard";
    private static final String DOC_SEARCH_RESULTS_CUSTOM_DOCUMENT_INFO = "document_info";

    /**
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchResultProcessor#getShowAllStandardFields()
     */
    @Override
    public boolean getShowAllStandardFields() {
        if (searchUsingDocumentInformationResults()) {
            return false;
        }
        return true;
    }
    
    /**
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchResultProcessor#getOverrideSearchableAttributes()
     */
    @Override
    public boolean getOverrideSearchableAttributes() {
        return true;
    }
    
    /**
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchResultProcessor#getCustomDisplayColumns()
     */
    @Override
    public List<Column> getCustomDisplayColumns() {
        List<Column> columns = new ArrayList<Column>();
        if (searchUsingDocumentInformationResults()) {
            columns = getDocumentSpecificCustomColumns();
        }
        return columns;
    }
    
    public abstract List<Column> getDocumentSpecificCustomColumns();
    
    private boolean searchUsingDocumentInformationResults() {
        SearchAttributeCriteriaComponent displayCriteria = getSearchableAttributeByFieldName("displayType");
        return ( (displayCriteria != null) && (DOC_SEARCH_RESULTS_CUSTOM_DOCUMENT_INFO.equals(displayCriteria.getValue())) );
    }
        
}
