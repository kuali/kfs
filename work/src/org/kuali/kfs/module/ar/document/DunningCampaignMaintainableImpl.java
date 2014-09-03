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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;

/**
 * Maintainable class for Dunning Campaign.
 */
public class DunningCampaignMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> requestParameters) {

        DunningCampaign dunningCampaign = (DunningCampaign) getBusinessObject();
        List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();

        for (DunningLetterDistribution dunningLetterDistribution : dunningLetterDistributions) {
            dunningLetterDistribution.setDunningLetterDistributionID(null);
        }

        super.processAfterCopy(document, requestParameters);
    }


}
