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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineAction;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.sys.context.SpringContext;

public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;

    public void setPurApItemAssets(PurApLineForm purApLineForm) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, purApLineForm.getPurchaseOrderIdentifier());
        Collection<PurchasingAccountsPayableDocument> purApDocs = getBusinessObjectService().findMatching(PurchasingAccountsPayableDocument.class, cols);
        purApLineForm.getPurApDocList().addAll(purApDocs);
        
        setPurApLineItemNumber(purApDocs);

    }

    private void setPurApLineItemNumber(Collection<PurchasingAccountsPayableDocument> purApDocs) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        for (PurchasingAccountsPayableDocument purchasingAccountsPayableDocument : purApDocs) {
            for (PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset : purchasingAccountsPayableDocument.getPurchasingAccountsPayableItemAssets()) {
                pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());
                // TODO: refactoring?
                if (CabConstants.PREQ.equalsIgnoreCase(purchasingAccountsPayableDocument.getDocumentTypeCode())) {
                    PaymentRequestItem item = (PaymentRequestItem) getBusinessObjectService().findByPrimaryKey(PaymentRequestItem.class, pKeys);
                    purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
                }
                else {
                    CreditMemoItem item = (CreditMemoItem) getBusinessObjectService().findByPrimaryKey(CreditMemoItem.class, pKeys);
                    purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
                }
                pKeys.clear();
            }
        }
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


}
