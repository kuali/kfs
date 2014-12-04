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
