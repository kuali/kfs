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
