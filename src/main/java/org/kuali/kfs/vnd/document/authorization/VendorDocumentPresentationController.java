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
package org.kuali.kfs.vnd.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

public class VendorDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> conditionallyReadOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);
        VendorDetail vendor = (VendorDetail)document.getNewMaintainableObject().getDataObject();

        if (!vendor.isVendorParentIndicator()) {
            // make some sections read only, e.g. supplier diversity cause they're on the header
            conditionallyReadOnlySectionIds.add(VendorPropertyConstants.VENDOR_SUPPLIER_DIVERSITIES);
        }

        return conditionallyReadOnlySectionIds;
    }

    /**
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.krad.maintenance.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        Set<String> conditionallyReadonlyPropertyNames = super.getConditionallyReadOnlyPropertyNames(document);
        VendorDetail vendor = (VendorDetail)document.getNewMaintainableObject().getDataObject();

        if (vendor.isVendorParentIndicator()) {
            // Vendor Parent Indicator should be readOnly if the vendor is a parent.
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_PARENT_INDICATOR);

            // For existing vendors, don't allow vendor type code to be changed if maint table indicates it shouldn't be changed
            if (ObjectUtils.isNotNull(vendor.getVendorHeaderGeneratedIdentifier()) &&
                    !vendor.getVendorHeader().getVendorType().isVendorTypeChangeAllowedIndicator()) {
                conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_TYPE_CODE);
            }
        }

        // If the vendor is not a parent, there are certain fields that should be readOnly
        else {
            // All the fields in VendorHeader should be readOnly if the vendor is not a parent.
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_TYPE_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_TAX_NUMBER);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_END_DATE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_W9_RECEIVED_INDICATOR);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_W8_BEN_RECEIVED_INDICATOR);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_DEBARRED_INDICATOR);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_FOREIGN_INDICATOR);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_W8SIGNED_DATE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_W9SIGNED_DATE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_W8_TYPE_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_GIIN_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_DOB);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_CHAPTER_3_STATUS_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_CHAPTER_4_STATUS_CODE);
            conditionallyReadonlyPropertyNames.add(VendorPropertyConstants.VENDOR_CORP_CITIZEN_CODE);
        }

        return conditionallyReadonlyPropertyNames;
    }

    @Override
    public Set<String> getConditionallyHiddenPropertyNames(BusinessObject businessObject) {
        Set<String> conditionallyHiddenPropertyNames = super.getConditionallyHiddenPropertyNames(businessObject);
        MaintenanceDocument document = (MaintenanceDocument) businessObject;
        VendorDetail vendor = (VendorDetail)document.getNewMaintainableObject().getDataObject();
        // If the vendor is a parent then the vendor parent name should be hidden.
        if (vendor.isVendorParentIndicator()) {
            conditionallyHiddenPropertyNames.add(VendorPropertyConstants.VENDOR_PARENT_NAME);
        }

        return conditionallyHiddenPropertyNames;
    }

  }
