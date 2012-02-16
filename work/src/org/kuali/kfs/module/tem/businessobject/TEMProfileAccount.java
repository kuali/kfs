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
package org.kuali.kfs.module.tem.businessobject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "TEM_PROFILE_ACCOUNT_T")
public class TEMProfileAccount extends BaseTemAccount {

    private Integer profileId;
    private TEMProfile profile;
    private CreditCardAgency creditCardAgency;
    private String creditCardOrAgencyName;
    private Integer cardId;

    /**
     * Gets the profileId attribute.
     * 
     * @return Returns the profileId.
     */
    @Column(name = "profile_id", nullable = false, length = 19)
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * Sets the profileId attribute value.
     * 
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the profile attribute.
     * 
     * @return Returns the profile.
     */
    @JoinColumn(name = "profile_id")
    public TEMProfile getProfile() {
        return profile;
    }

    /**
     * Sets the profile attribute value.
     * 
     * @param profile The profile to set.
     */
    public void setProfile(TEMProfile profile) {
        this.profile = profile;
    }
    
    /**
     * Gets the cardId attribute.
     * 
     * @return Returns the cardId.
     */
    @Column(name = "card_id", nullable = false, length = 19)
    public Integer getCardId() {
        return cardId;
    }

    /**
     * Sets the cardId attribute value.
     * 
     * @param cardId The cardId to set.
     */
    public void setCardId(Integer cardId) {
        this.cardId = cardId;
    }

    /**
     * Gets the creditCardAgency attribute.
     * 
     * @return Returns the creditCardAgency.
     */
    @JoinColumn(name = "card_id")
    public CreditCardAgency getCreditCardAgency() {
        return creditCardAgency;
    }

    public void setCreditCardAgency(CreditCardAgency creditCardAgency) {
        this.creditCardAgency = creditCardAgency;
    }

    public String getCreditCardOrAgencyName() {
        return creditCardOrAgencyName;
    }

    public void setCreditCardOrAgencyName(String creditCardOrAgencyName) {
        this.creditCardOrAgencyName = creditCardOrAgencyName;
    }
    
    
}
