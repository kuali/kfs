/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Prevents certain fields from being changed when editing the business object
 */
public class MileageRateMaintenanceDocumentPresentationController extends MaintenanceDocumentPresentationControllerBase {

    /**
     * If the document is editing, then only the effective to date should be editable
     * @see org.kuali.rice.kns.document.authorization.MaintenanceDocumentPresentationControllerBase#getConditionallyReadOnlyPropertyNames(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public Set<String> getConditionallyReadOnlyPropertyNames(MaintenanceDocument document) {
        if (StringUtils.equals(KRADConstants.MAINTENANCE_EDIT_ACTION, document.getNewMaintainableObject().getMaintenanceAction())) {
            Set<String> ineditableUponEditProperties = new HashSet<String>();
            ineditableUponEditProperties.add(TemPropertyConstants.EXPENSE_TYPE_CODE);
            ineditableUponEditProperties.add(TemPropertyConstants.RATE);
            ineditableUponEditProperties.add(TemPropertyConstants.ACTIVE_FROM_DATE);
            return ineditableUponEditProperties;
        }
        return super.getConditionallyReadOnlyPropertyNames(document);
    }

}
