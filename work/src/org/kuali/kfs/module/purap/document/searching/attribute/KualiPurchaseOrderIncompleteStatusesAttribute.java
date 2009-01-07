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
package org.kuali.kfs.module.purap.document.searching.attribute;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.rice.kew.docsearch.DocumentSearchContext;
import org.kuali.rice.kew.docsearch.SearchableAttributeStringValue;
import org.kuali.rice.kew.docsearch.SearchableAttributeValue;
import org.kuali.rice.kns.workflow.attribute.KualiXmlSearchableAttributeImpl;

/**
 * This class...
 */
public class KualiPurchaseOrderIncompleteStatusesAttribute extends KualiXmlSearchableAttributeImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiPurchaseOrderIncompleteStatusesAttribute.class);

    public static final String FIELD_DEF_NAME = "purchaseOrderDocumentIncompleteStatuses";
    public static final String VALUE_FOR_YES = "Yes";
    public static final String VALUE_FOR_NO = "No";

    /**
     * This method will use the given value which should be the document's status code and translate that into
     */
    @Override
    public List getSearchStorageValues(DocumentSearchContext documentSearchContext) {
        List<SearchableAttributeValue> newSearchAttributeValues = new ArrayList();
        List<SearchableAttributeValue> superList = super.getSearchStorageValues(documentSearchContext);
        if (superList.size() != 1) {
            String errorMessage = "Exactly one field with an xpath expression that finds one status code should be defined for this attribute (found " + superList.size() + ").";
            LOG.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }
        SearchableAttributeValue superSearchAttValue = superList.get(0);
        SearchableAttributeStringValue sav = new SearchableAttributeStringValue();
        sav.setSearchableAttributeKey(superSearchAttValue.getSearchableAttributeKey());
        if (PurapConstants.PurchaseOrderStatuses.INCOMPLETE_STATUSES.contains((String) superSearchAttValue.getSearchableAttributeValue())) {
            sav.setSearchableAttributeValue(VALUE_FOR_YES);
        }
        else {
            sav.setSearchableAttributeValue(VALUE_FOR_NO);
        }
        newSearchAttributeValues.add(sav);
        return newSearchAttributeValues;
    }

}
