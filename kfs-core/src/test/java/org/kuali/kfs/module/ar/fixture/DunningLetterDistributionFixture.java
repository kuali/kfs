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
