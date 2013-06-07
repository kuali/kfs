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

import org.kuali.kfs.module.ar.businessobject.DunningCampaign;

/**
 * Fixture class for AR Dunning Campaign
 */


public enum DunningCampaignFixture {

    AR_DUNC1("DCT1", "Dunning Campaign 1", true);

    private String campaignID;
    private String campaignDescription;
    private boolean active;

    private DunningCampaignFixture(String campaignID, String campaignDescription, boolean active) {

        this.campaignID = campaignID;
        this.campaignDescription = campaignDescription;
        this.active = active;

    }

    public DunningCampaign createDunningCampaign() {
        DunningCampaign dunningCampaign = new DunningCampaign();
        dunningCampaign.setCampaignID(this.campaignID);
        dunningCampaign.setCampaignDescription(this.campaignDescription);
        dunningCampaign.setActive(this.active);

        return dunningCampaign;
    }
}
