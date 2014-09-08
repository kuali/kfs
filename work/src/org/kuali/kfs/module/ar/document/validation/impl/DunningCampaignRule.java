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
        boolean isValid = super.processSaveDocument(document);
        isValid &= validateDuplicatePastDue((MaintenanceDocument) document);

        return isValid;
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
