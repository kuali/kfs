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
package org.kuali.kfs.module.tem.document.validation.impl;

import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.service.PerDiemService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Determines if a mileage rate for all days of a trip using the default per diem mileage rate expense type
 */
public class TravelDocumentMileageRateAvailableForPerDiemValidation extends GenericValidation {
    protected PerDiemService perDiemService;

    /**
     * Uses the PerDiemService to check if default mileage rates are available for each day of the trip
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean success = true;
        final TravelDocument document = (TravelDocument)event.getDocument();

        if (!getPerDiemService().isMileageRateAvailableForAllPerDiem(document)) {
            final String defaultPerDiemMileageRate = getPerDiemService().getDefaultPerDiemMileageRateExpenseType();
            GlobalVariables.getMessageMap().putError(TemPropertyConstants.PER_DIEM_EXPENSES, TemKeyConstants.ERROR_DOCUMENT_PER_DIEM_EXPENSE_MISSING_MILEAGE_RATE, new String[] { defaultPerDiemMileageRate });
            success = false;
        }
        return success;
    }

    public PerDiemService getPerDiemService() {
        return perDiemService;
    }

    public void setPerDiemService(PerDiemService perDiemService) {
        this.perDiemService = perDiemService;
    }

}
