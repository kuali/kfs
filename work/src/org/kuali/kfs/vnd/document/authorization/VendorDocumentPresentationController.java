/*
 * Copyright 2009 The Kuali Foundation
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
