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
package org.kuali.kfs.module.ar.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.integration.ar.AccountsReceivableDunningCampaign;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Dunning Campaigns
 */
public class DunningCampaign extends PersistableBusinessObjectBase implements AccountsReceivableDunningCampaign, MutableInactivatable {

    private String campaignID;
    private String campaignDescription;
    private boolean active;

    private List<DunningLetterDistribution> dunningLetterDistributions;

    /**
     * Default constructor
     */
    public DunningCampaign() {
        dunningLetterDistributions = new ArrayList<DunningLetterDistribution>();
    }


    /**
     * Gets the campaignID attribute.
     *
     * @return Returns the campaignID.
     */
    @Override
    public String getCampaignID() {
        return campaignID;
    }


    /**
     * Sets the campaignID attribute value.
     *
     * @param campaignID The campaignID to set.
     */
    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;
    }


    /**
     * Gets the campaignDescription attribute.
     *
     * @return Returns the campaignDescription.
     */
    @Override
    public String getCampaignDescription() {
        return campaignDescription;
    }


    /**
     * Sets the campaignDescription attribute value.
     *
     * @param campaignDescription The campaignDescription to set.
     */
    public void setCampaignDescription(String campaignDescription) {
        this.campaignDescription = campaignDescription;
    }


    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the dunningLetterDistributions attribute.
     *
     * @return Returns the dunningLetterDistributions.
     */
    public List<DunningLetterDistribution> getDunningLetterDistributions() {
        return dunningLetterDistributions;
    }


    /**
     * Sets the dunningLetterDistributions attribute value.
     *
     * @param dunningLetterDistributions The dunningLetterDistributions to set.
     */
    public void setDunningLetterDistributions(List<DunningLetterDistribution> dunningLetterDistributions) {
        this.dunningLetterDistributions = dunningLetterDistributions;
    }



    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap toStringMap = new LinkedHashMap();
        toStringMap.put("campaignID", this.campaignID);
        toStringMap.put("campaignDescription", this.campaignDescription);
        return toStringMap;
    }

}
