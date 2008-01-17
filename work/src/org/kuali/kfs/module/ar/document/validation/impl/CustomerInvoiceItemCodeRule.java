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
package org.kuali.module.ar.rules;

import java.math.BigDecimal;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSKeyConstants.InvoiceItemCode;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;

public class CustomerInvoiceItemCodeRule extends MaintenanceDocumentRuleBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InvoiceItemCode.class);
    
    private CustomerInvoiceItemCode newInvoiceItemCode;
    
    @Override
    public void setupConvenienceObjects() {
        newInvoiceItemCode = (CustomerInvoiceItemCode) super.getNewBo();
    }
    
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success;
        success = validateItemDefaultPrice(newInvoiceItemCode);
        success &= validateItemDefaultQuantity(newInvoiceItemCode);

        return success;
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        // always return true even if there are business rule failures.
        processCustomRouteDocumentBusinessRules(document);
        return true;
    }

    private boolean validateItemDefaultPrice(CustomerInvoiceItemCode document) {
        
        boolean validEntry = true;
        KualiDecimal itemDefaultPrice = document.getItemDefaultPrice();
        
        if (ObjectUtils.isNotNull(itemDefaultPrice)) {
            validEntry = itemDefaultPrice.isPositive();
            if (!validEntry)
                putFieldError("itemDefaultPrice",KFSKeyConstants.InvoiceItemCode.NONPOSITIVE_ITEM_DEFAULT_PRICE);
        }
        return validEntry;
    }
    
    private boolean validateItemDefaultQuantity(CustomerInvoiceItemCode document) {
        
        boolean validEntry = true;
        BigDecimal itemDefaultQuantity = document.getItemDefaultQuantity();
        
        if (ObjectUtils.isNotNull(itemDefaultQuantity)) {
            if (itemDefaultQuantity.floatValue() <= 0) {
                putFieldError("itemDefaultQuantity",KFSKeyConstants.InvoiceItemCode.NONPOSITIVE_ITEM_DEFAULT_QUANTITY);
                validEntry = false;
            }
        }
        return validEntry;
    }
    
}
