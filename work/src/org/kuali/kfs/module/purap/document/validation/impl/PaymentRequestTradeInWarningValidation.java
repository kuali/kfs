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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentRequestTradeInWarningValidation extends GenericValidation {

    private PurApItem itemForValidation;
    
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        
        if (itemForValidation.getItemType().isLineItemIndicator() && itemForValidation.getItemAssignedToTradeInIndicator()) {
            PaymentRequestItem tradeInItem = (PaymentRequestItem) ((PaymentRequestDocument)event.getDocument()).getTradeInItem();
            if (ObjectUtils.isNotNull(tradeInItem)) {
                if (ObjectUtils.isNull(tradeInItem.getItemUnitPrice()) && tradeInItem.getPoOutstandingAmount().isLessThan(new KualiDecimal(0))) {
                    GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_ITEM_TRADE_IN_AMOUNT_UNUSED);
                    valid &= false;                    
                }
            }
        }
        
        return valid;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

}
