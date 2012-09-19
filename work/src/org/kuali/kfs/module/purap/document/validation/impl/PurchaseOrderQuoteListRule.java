/*
 * Copyright 2006-2008 The Kuali Foundation
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
