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
