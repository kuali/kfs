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
package org.kuali.kfs.vnd.document.authorization;

import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemMaintenanceDocumentPresentationControllerBase;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.document.MaintenanceDocument;

public class VendorDocumentPresentationController extends FinancialSystemMaintenanceDocumentPresentationControllerBase {

    @Override
    public Set<String> getConditionallyReadOnlySectionIds(MaintenanceDocument document) {
        Set<String> conditionallyReadOnlySectionIds = super.getConditionallyReadOnlySectionIds(document);
        VendorDetail vendor = (VendorDetail)document.getNewMaintainableObject().getBusinessObject();
        if (!vendor.isVendorParentIndicator()) {
            // make some sections read only, e.g. supplier diversity cause they're on the header
            conditionallyReadOnlySectionIds.add("Supplier Diversity");
        }
        return conditionallyReadOnlySectionIds;
    }
}
