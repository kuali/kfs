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
package org.kuali.kfs.module.tem.document.maintenance;

import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;

/**
 * Overrides maintainable so that if the threshhold is not null, turns receipted required to true (if it isn't)
 */
public class ExpenseTypeObjectCodeMaintainable extends FinancialSystemMaintainable {

    /**
     * Overridden to turn receipt required to true if the receipt required threshold is not null
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#processAfterPost(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        // let's be careful and get the new maintainable and business object from the maint doc directly
        ExpenseTypeObjectCode etoc = (ExpenseTypeObjectCode)document.getNewMaintainableObject().getBusinessObject();
        if (!etoc.isReceiptRequired() && etoc.getReceiptRequirementThreshold() != null && (etoc.getReceiptRequirementThreshold().isPositive() || etoc.getReceiptRequirementThreshold().isZero())) {
            etoc.setReceiptRequired(true);
        }
    }

}
