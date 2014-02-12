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
