/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;

/**
 * Fixture class for CG Agency
 */


public enum DunningLetterDistributionFixture {

    AR_DLD1("DC1", new Long (1), "CURRENT", true, "CURRENT");

    private String campaignID;
    private Long dunningLetterDistributionID;
    private String daysPastDue;
    private boolean sendDunningLetterIndicator;
    private String dunningLetterTemplate;

    private DunningLetterDistributionFixture(String campaignID, Long dunningLetterDistributionID, String daysPastDue, boolean sendDunningLetterIndicator, String dunningLetterTemplate) {

        this.campaignID = campaignID;
        this.dunningLetterDistributionID = dunningLetterDistributionID;
        this.daysPastDue = daysPastDue;
        this.sendDunningLetterIndicator = sendDunningLetterIndicator;
        this.dunningLetterTemplate = dunningLetterTemplate;

    }

    public DunningLetterDistribution createDunningLetterDistribution() {
        DunningLetterDistribution dunningLetterDistribution = new DunningLetterDistribution();
        dunningLetterDistribution.setCampaignID(this.campaignID);
        dunningLetterDistribution.setDunningLetterDistributionID(this.dunningLetterDistributionID);
        dunningLetterDistribution.setDaysPastDue(this.daysPastDue);
        dunningLetterDistribution.setSendDunningLetterIndicator(this.sendDunningLetterIndicator);
        dunningLetterDistribution.setDunningLetterTemplate(this.dunningLetterTemplate);

        return dunningLetterDistribution;
    }
}
