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

import java.util.List;
import java.util.Map;

import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttribute;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kew.lookupable.Row;
import org.kuali.rice.kew.rule.WorkflowAttributeValidationError;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.GlobalBusinessObject;
import org.w3c.dom.Document;

/**
 * This class...
 */
public class DataDictionarySearchableAttribute implements SearchableAttribute {

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchContent(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public String getSearchContent(DocumentSearchContext documentSearchContext) {
        return null;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchStorageValues(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<SearchableAttributeValue> getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        List<SearchableAttributeValue> searchValues = null;
      //  final Class<? extends BusinessObject> businessObjectClass = getBusinessObjectClass(documentSearchContext.getDocumentTypeName());
       // if (businessObjectClass != null) {
            
        //}
        return searchValues;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#getSearchingRows(org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<Row> getSearchingRows(DocumentSearchContext documentSearchContext) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.rice.kew.docsearch.SearchableAttribute#validateUserSearchInputs(java.util.Map, org.kuali.rice.kew.docsearch.DocumentSearchContext)
     */
    public List<WorkflowAttributeValidationError> validateUserSearchInputs(Map<Object, String> paramMap, DocumentSearchContext searchContext) {
        // TODO Auto-generated method stub
        return null;
    }

}
