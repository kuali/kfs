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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteList;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteListVendor;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.VendorUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.rule.AddCollectionLineRule;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Business rule(s) applicable to Purchase Order Contract Language maintenance document.
 */
public class PurchaseOrderQuoteListRule extends MaintenanceDocumentRuleBase implements AddCollectionLineRule {

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomApproveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        this.setupConvenienceObjects();
        boolean success = this.validateVendor();
        return success && super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * Check to see if data duplicates existing data
     * 
     * @return boolean indicating if validation succeeded
     */
    protected boolean validateVendor() {
        LOG.info("validateVendor called");
        boolean success = true;
        
        PurchaseOrderQuoteList newBo = (PurchaseOrderQuoteList)super.getNewBo();
        if (newBo.getQuoteListVendors() == null || newBo.getQuoteListVendors().size() == 0) {
            success = false;
            putFieldError("add.quoteListVendors.vendorDetail.vendorNumber", PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_LIST_NO_VENDOR);
        }
        return success;
    }
    
    /**
     * Overrides the superclass method to check whether the vendor existed in the
     * database if the user typed in the vendor name instead of selecting from the
     * lookup, and if there are more than one vendors match the name entered,
     * return error to inform the user about it so that they would select from
     * the lookup.
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        PurchaseOrderQuoteListVendor vendor = (PurchaseOrderQuoteListVendor)line;
        // We have to check whether a vendor with the name entered really existed
        // and have to set the vendor detail with what we find in the database.
        Map fieldValues = new HashMap();
        String vendorNumber = vendor.getVendorDetail().getVendorNumber();
        Integer headerId = VendorUtils.getVendorHeaderId(vendorNumber);
        Integer detailId = VendorUtils.getVendorDetailId(vendorNumber);
        fieldValues.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, headerId);
        fieldValues.put(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, detailId);
        Collection result = SpringContext.getBean(BusinessObjectService.class).findMatching(VendorDetail.class, fieldValues);
        if (result == null || result.size() == 0) {
            // This means vendor is not found.
            putFieldError("add.quoteListVendors.vendorDetail.vendorNumber", PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_LIST_NON_EXISTENCE_VENDOR);
            return false;
        }
        VendorDetail resultVendor = (VendorDetail) result.iterator().next();
        vendor.setVendorHeaderGeneratedIdentifier(resultVendor.getVendorHeaderGeneratedIdentifier());
        vendor.setVendorDetailAssignedIdentifier(resultVendor.getVendorDetailAssignedIdentifier());
        return super.processAddCollectionLineBusinessRules(document, collectionName, line);
    }
}
