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

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for Dunning Letter Distribution
 */
public class DunningLetterDistribution extends PersistableBusinessObjectBase {

    private String campaignID;
    private Long dunningLetterDistributionID;
    private String daysPastDue;
    private boolean sendDunningLetterIndicator;
    private String dunningLetterTemplate;
    private boolean activeIndicator;
    private DunningCampaign dunningCampaign;


    /**
     * Default constructor
     */
    public DunningLetterDistribution() {
        super();
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
     * Gets the dunningLetterDistributionID attribute.
     * 
     * @return Returns the dunningLetterDistributionID.
     */
    public Long getDunningLetterDistributionID() {
        return dunningLetterDistributionID;
    }


    /**
     * Sets the dunningLetterDistributionID attribute value.
     * 
     * @param dunningLetterDistributionID The dunningLetterDistributionID to set.
     */
    public void setDunningLetterDistributionID(Long dunningLetterDistributionID) {
        this.dunningLetterDistributionID = dunningLetterDistributionID;
    }


    /**
     * Gets the daysPastDue attribute.
     * 
     * @return Returns the daysPastDue.
     */
    public String getDaysPastDue() {
        return daysPastDue;
    }


    /**
     * Sets the daysPastDue attribute value.
     * 
     * @param daysPastDue The daysPastDue to set.
     */
    public void setDaysPastDue(String daysPastDue) {
        this.daysPastDue = daysPastDue;
    }


    /**
     * Gets the sendDunningLetterIndicator attribute.
     * 
     * @return Returns the sendDunningLetterIndicator.
     */
    public boolean isSendDunningLetterIndicator() {
        return sendDunningLetterIndicator;
    }


    /**
     * Sets the sendDunningLetterIndicator attribute value.
     * 
     * @param sendDunningLetterIndicator The sendDunningLetterIndicator to set.
     */
    public void setSendDunningLetterIndicator(boolean sendDunningLetterIndicator) {
        this.sendDunningLetterIndicator = sendDunningLetterIndicator;
    }


    /**
     * Gets the dunningLetterTemplate attribute.
     * 
     * @return Returns the dunningLetterTemplate.
     */
    public String getDunningLetterTemplate() {
        return dunningLetterTemplate;
    }


    /**
     * Sets the dunningLetterTemplate attribute value.
     * 
     * @param dunningLetterTemplate The dunningLetterTemplate to set.
     */
    public void setDunningLetterTemplate(String dunningLetterTemplate) {
        this.dunningLetterTemplate = dunningLetterTemplate;
    }


    /**
     * Gets the dunningCampaign attribute.
     * 
     * @return Returns the dunningCampaign.
     */
    public DunningCampaign getDunningCampaign() {
        return dunningCampaign;
    }


    /**
     * Sets the dunningCampaign attribute value.
     * 
     * @param dunningCampaign The dunningCampaign to set.
     */
    public void setDunningCampaign(DunningCampaign dunningCampaign) {
        this.dunningCampaign = dunningCampaign;
    }

    /**
     * Gets the activeIndicator attribute.
     * 
     * @return Returns the activeIndicator.
     */
    public boolean isActiveIndicator() {
        return activeIndicator;
    }


    /**
     * Sets the activeIndicator attribute value.
     * 
     * @param activeIndicator The activeIndicator to set.
     */
    public void setActiveIndicator(boolean activeIndicator) {
        this.activeIndicator = activeIndicator;
    }


    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap toStringMap = new LinkedHashMap();
        toStringMap.put("dunningLetterDistributionID", this.dunningLetterDistributionID);
        return toStringMap;
    }

}
