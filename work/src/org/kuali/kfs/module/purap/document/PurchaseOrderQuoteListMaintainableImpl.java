/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
