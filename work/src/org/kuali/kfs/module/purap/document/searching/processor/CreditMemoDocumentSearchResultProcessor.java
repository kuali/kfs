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
import java.util.List;

import edu.iu.uis.eden.docsearch.DocumentSearchResult;
import edu.iu.uis.eden.lookupable.Column;
import edu.iu.uis.eden.lookupable.Field;

public class CreditMemoDocumentSearchResultProcessor extends PurApDocumentSearchResultProcessor {

    /**
     * @see org.kuali.workflow.module.purap.docsearch.KualiPurApDocumentSearchResultProcessor#getDocumentSpecificCustomColumns()
     */
    @Override
    public List<Column> getDocumentSpecificCustomColumns() {
        List<Column> columns = new ArrayList<Column>();
        List<String> searchableAttributeFieldNames = new ArrayList<String>();
        searchableAttributeFieldNames.add("creditMemoCreditMemoId");// cm id
        searchableAttributeFieldNames.add("creditMemoVendorCreditMemoNumber");// vendor cm #
        searchableAttributeFieldNames.add("creditMemoPaymentRequestId");// PREQ #
        searchableAttributeFieldNames.add("creditMemoPurchaseOrderId");// PO #
        searchableAttributeFieldNames.add("creditMemoStatusDescription");// Status Desc
        searchableAttributeFieldNames.add("creditMemoHoldIndicator");// Hold Yes/No
        searchableAttributeFieldNames.add("creditMemoVendorName");// Vendor Name
        searchableAttributeFieldNames.add("creditMemoCustomerNumber");// customer number
        searchableAttributeFieldNames.add("documentTotalAmount");
        searchableAttributeFieldNames.add("creditMemoAccountsPayableDate");// ap approval date
        searchableAttributeFieldNames.add("creditMemoExtractedDate");// extracted date
        searchableAttributeFieldNames.add("creditMemoPaidIndicator");// paid indicator
        addSearchableAttributeColumnsBasedOnFields(columns, getSearchCriteria(), searchableAttributeFieldNames);
        return columns;
    }

}
