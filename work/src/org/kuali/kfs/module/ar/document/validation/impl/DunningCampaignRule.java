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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;

public class DunningCampaignRule extends MaintenanceDocumentRuleBase {

    @Override
    public boolean processSaveDocument(Document document) {
        super.processSaveDocument(document);
        processRouteDocument(document);
        return true;
    }

    @Override
    public boolean processRouteDocument(Document document) {
        boolean isValid = super.processRouteDocument(document);
        isValid &= validateDuplicatePastDue((MaintenanceDocument) document);

        return isValid;
    }

    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean isValid = super.processAddCollectionLineBusinessRules(document, collectionName, bo);

        if ( collectionName.equalsIgnoreCase(ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS) ) {

            DunningLetterDistribution newLine = (DunningLetterDistribution) document.getNewMaintainableObject().getNewCollectionLine(ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS);
            DunningCampaign dunningCampaign = (DunningCampaign) document.getNewMaintainableObject().getDataObject();
            Set<String> daysPastDueSet = new HashSet<String>();
            daysPastDueSet.add(newLine.getDaysPastDue());

            isValid &= isDuplicatePastDue(daysPastDueSet, dunningCampaign.getDunningLetterDistributions(), true);
        }

        return isValid;
    }

    /**
     * checks if the existing set of past due days contains duplicates
     *
     * @param document
     * @return
     */
    private boolean validateDuplicatePastDue(MaintenanceDocument document) {
        MaintenanceDocument maintDoc = document;
        DunningCampaign dunningCampaign = (DunningCampaign) maintDoc.getNewMaintainableObject().getDataObject();
        Set<String> daysPastDueSet = new HashSet<String>();

        return isDuplicatePastDue(daysPastDueSet, dunningCampaign.getDunningLetterDistributions(), false);
    }

    private boolean isDuplicatePastDue(Set<String> daysPastDueSet, List<DunningLetterDistribution> dunningLetterDistributions, boolean isAddLine ) {
        int lineNumber = 0;
        for (DunningLetterDistribution dld : dunningLetterDistributions ) {
            if (!daysPastDueSet.add(dld.getDaysPastDue())) {
                if (isAddLine) {
                    putFieldError("add." + ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS + "." + ArPropertyConstants.DunningLetterDistributionFields.DAYS_PAST_DUE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_DAYS_PAST_DUE_DUPLICATE);
                } else {
                    putFieldError(ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS + "[" +lineNumber + "]." + ArPropertyConstants.DunningLetterDistributionFields.DAYS_PAST_DUE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_DAYS_PAST_DUE_DUPLICATE);
                }
                return false;
            }
            lineNumber++;
        }

        return true;
    }
}
