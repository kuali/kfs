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
package org.kuali.kfs.integration.ar.businessobject;

import org.kuali.kfs.integration.ar.AccountsReceivableDunningCampaign;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;

/**
 * Business Object for Dunning Campaigns
 */
public class DunningCampaign implements MutableInactivatable, AccountsReceivableDunningCampaign {

    private String campaignID;
    private String campaignDescription;
    private boolean active;

    /**
     * Default constructor
     */
    public DunningCampaign() {
    }

    /**
     * Gets the campaignID attribute.
     * 
     * @return Returns the campaignID.
     */
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
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#prepareForWorkflow()
     */
    @Override
    public void prepareForWorkflow() {
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObject#refresh()
     */
    @Override
    public void refresh() {
    }
}
