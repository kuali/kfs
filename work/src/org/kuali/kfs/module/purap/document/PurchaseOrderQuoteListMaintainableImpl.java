/*
 * Copyright 2007-2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/* 
 * A special implementation of Maintainable specifically for PurchaseOrderQuoteLanguage
 * maintenance page to override the behavior when the PurchaseOrderQuoteLanguage 
 * maintenance document is copied.
*/
public class PurchaseOrderQuoteListMaintainableImpl extends FinancialSystemMaintainable {
    @Override
    public Map<String, String> populateNewCollectionLines( Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall ) {
        String collName = "quoteListVendors.vendorDetail.vendorNumber";
        String vendorNumber = (String)fieldValues.get(collName);
        if (StringUtils.isNotBlank(vendorNumber)) {
            if (!VendorUtils.validVendorNumberFormat(vendorNumber)) {
                GlobalVariables.getMessageMap().putError(KRADConstants.MAINTENANCE_ADD_PREFIX + collName , PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_LIST_NON_EXISTENCE_VENDOR);
                return fieldValues;
            }
            else {
                Integer headerId = VendorUtils.getVendorHeaderId(vendorNumber);
                Integer detailId = VendorUtils.getVendorDetailId(vendorNumber);
                resetPreviousVendorInformationOnAddLine(fieldValues, headerId, detailId);
            }
        }
        
        return super.populateNewCollectionLines(fieldValues, maintenanceDocument, methodToCall);
    }
    
    private void resetPreviousVendorInformationOnAddLine(Map fieldValues, Integer headerId, Integer detailId) {        
        if (fieldValues.get("quoteListVendors.vendorHeaderGeneratedIdentifier") != null) {
            fieldValues.put("quoteListVendors.vendorHeaderGeneratedIdentifier", headerId);
        }
        
        if (fieldValues.get("quoteListVendors.vendorDetailAssignedIdentifier") != null) {
            fieldValues.put("quoteListVendors.vendorDetailAssignedIdentifier", detailId);
        }
        
    }
}
