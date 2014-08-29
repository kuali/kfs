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
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.ObjectUtils;

public class DunningCampaignRule extends MaintenanceDocumentRuleBase {

    @Override
    public boolean processSaveDocument(Document document) {
        return validateDuplicatePastDue((MaintenanceDocument) document);
    }

    @Override
    public boolean processRouteDocument(Document document) {
        return validateDuplicatePastDue((MaintenanceDocument) document);
    }

    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        
        if ( collectionName.equalsIgnoreCase(ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS) ) return validateDuplicatePastDue(document);

        return true;
    }

    private boolean validateDuplicatePastDue(MaintenanceDocument document) {
        MaintenanceDocument maintDoc = document;
        DunningCampaign dunningCampaign = (DunningCampaign) maintDoc.getNewMaintainableObject().getDataObject();
        Set<String> daysPastDueSet = new HashSet<String>();

        if (ObjectUtils.isNotNull(dunningCampaign) && CollectionUtils.isNotEmpty(dunningCampaign.getDunningLetterDistributions())) {
            for (DunningLetterDistribution dld : dunningCampaign.getDunningLetterDistributions()) {
                if (!daysPastDueSet.add(dld.getDaysPastDue())) {
                    putFieldError(ArPropertyConstants.DunningLetterDistributionFields.DAYS_PAST_DUE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_DAYS_PAST_DUE_DUPLICATE);
                    return false;
                }
            }
        }

        return true;
    }
}
