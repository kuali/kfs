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
package org.kuali.module.purap.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.PurchaseOrderQuoteStatus;
import org.kuali.module.purap.service.PurchaseOrderService;

/**
 * Value Finder for Purchase Order Vendor Quote Statuses.
 * 
 * THIS CODE IS NOT USED IN RELEASE 2 BUT THE CODE WAS LEFT IN TO
 * FACILITATE TURNING IT BACK ON EARLY IN THE DEVELOPMENT CYCLE OF RELEASE 3.
 * 
 */
public class PurchaseOrderVendorQuoteStatusCodeValuesFinder extends KeyValuesBase {

    /**
     * Returns code/description pairs of all Purchase Order Vendor Quote Statuses.
     * 
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List keyValues = new ArrayList();
        keyValues.add(new KeyLabelPair(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        ArrayList<PurchaseOrderQuoteStatus> poQuoteStatuses = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderQuoteStatusCodes();
        for (PurchaseOrderQuoteStatus status : poQuoteStatuses) {
            keyValues.add(new KeyLabelPair(status.getPurchaseOrderQuoteStatusCode(), status.getPurchaseOrderQuoteStatusDescription()));
        }
        return keyValues;
    }
}