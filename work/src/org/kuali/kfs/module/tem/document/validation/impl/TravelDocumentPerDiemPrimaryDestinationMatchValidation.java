/*
 * Copyright 2013 The Kuali Foundation.
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
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that at least one of the per diem expenses on a document matches the location of the primary destination on the document
 */
public class TravelDocumentPerDiemPrimaryDestinationMatchValidation extends GenericValidation {

    /**
     * Loops through any per diem expenses, making sure at that least one matches the primary destination of the document
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final TravelDocument travelDoc = (TravelDocument)event.getDocument();
        final Integer primaryDestId = travelDoc.getPrimaryDestinationId();

        if (primaryDestId != null) {
            if (travelDoc.getPerDiemExpenses() != null && !travelDoc.getPerDiemExpenses().isEmpty()) {
                for (PerDiemExpense perDiemExpense : travelDoc.getPerDiemExpenses()) {
                    perDiemExpense.refreshReferenceObject(TemPropertyConstants.PER_DIEM);
                    if (!ObjectUtils.isNull(perDiemExpense.getPerDiem()) && primaryDestId.equals(perDiemExpense.getPerDiem().getPrimaryDestinationId())) {
                        return true; // skip out loop - we're fine
                    }
                }
                // still here?  then we didn't find a match...
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.DOCUMENT+"."+TemPropertyConstants.PER_DIEM_EXP+"[0]."+TemPropertyConstants.PRIMARY_DESTINATION_ID, TemKeyConstants.ERROR_TRAVEL_DOC_PRI_DEST_PER_DIEM_NO_MATCH, travelDoc.getPrimaryDestinationName());
                return false;
            }
        }
        return true;
    }
}
