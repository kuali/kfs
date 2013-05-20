/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Maintainable class for Dunning Campaign.
 */
public class DunningCampaignMaintainableImpl extends FinancialSystemMaintainable {

    /**
     * Check for duplicate daysPastDue value in collection of dunning letter distribution.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        if (collectionName.equalsIgnoreCase(ArPropertyConstants.DunningCampaignFields.DUNNING_LETTER_DISTRIBUTIONS)) {
            DunningLetterDistribution dunningLetterDistribution = (DunningLetterDistribution) newCollectionLines.get(collectionName);
            if (ObjectUtils.isNotNull(dunningLetterDistribution.getDaysPastDue())) {
                DunningCampaign dunningCampaign = (DunningCampaign) this.businessObject;
                if (CollectionUtils.isNotEmpty(dunningCampaign.getDunningLetterDistributions())) {
                    for (DunningLetterDistribution dld : dunningCampaign.getDunningLetterDistributions()) {
                        if (ObjectUtils.isNotNull(dld.getDaysPastDue()) && dld.getDaysPastDue().equals(dunningLetterDistribution.getDaysPastDue())) {
                            GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.DAYS_PAST_DUE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_DAYS_PAST_DUE_DUPLICATE);
                            return;
                        }
                    }
                }
            }
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * Checks for duplicate daysPastDue value in collection of dunning letter distribution before it gets saved.
     * 
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        DunningCampaign dunningCampaign = (DunningCampaign) this.businessObject;
        Set<String> daysPastDueSet = new HashSet<String>();
        if (CollectionUtils.isNotEmpty(dunningCampaign.getDunningLetterDistributions())) {
            for (DunningLetterDistribution dld : dunningCampaign.getDunningLetterDistributions()) {
                if (!daysPastDueSet.add(dld.getDaysPastDue())) {
                    GlobalVariables.getMessageMap().putError(ArPropertyConstants.DunningLetterDistributionFields.DAYS_PAST_DUE, ArKeyConstants.DunningLetterDistributionErrors.ERROR_DAYS_PAST_DUE_DUPLICATE);
                    return;
                }
            }
        }
        super.prepareForSave();
    }
}
