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

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_PROFILE_ARRANGER_T")
public class TEMProfileArranger extends PersistableBusinessObjectBase {

    private Integer arrangerId;
    private String principalId;
    private String principalName;
    private Integer profileId;
    private TEMProfile profile;
    private Boolean taInd = Boolean.FALSE;
    private Boolean trInd = Boolean.FALSE;
    private Boolean active = Boolean.TRUE;
    private Boolean primary = Boolean.FALSE;

    private Person principal;

    /**
     * Gets the arrangerId attribute.
     * @return Returns the arrangerId.
     */
    @Id
    @GeneratedValue(generator = "TEM_PROFILE_ARRANGER_ID_SEQ")
    @SequenceGenerator(name = "TEM_PROFILE_ARRANGER_ID_SEQ", sequenceName = "TEM_PROFILE_ARRANGER_ID_SEQ", allocationSize = 5)
    @Column(name = "arranger_id", nullable = false)
    public Integer getArrangerId() {
        return arrangerId;
    }


    /**
     * Sets the arrangerId attribute value.
     * @param arrangerId The arrangerId to set.
     */
    public void setArrangerId(Integer arrangerId) {
        this.arrangerId = arrangerId;
    }



    /**
     * Gets the taInd attribute.
     * @return Returns the taInd.
     */
    public Boolean getTaInd() {
        return taInd;
    }


    /**
     * Sets the taInd attribute value.
     * @param taInd The taInd to set.
     */
    public void setTaInd(Boolean taInd) {
        this.taInd = taInd;
    }


    /**
     * Gets the trInd attribute.
     * @return Returns the trInd.
     */
    public Boolean getTrInd() {
        return trInd;
    }


    /**
     * Sets the trInd attribute value.
     * @param trInd The trInd to set.
     */
    public void setTrInd(Boolean trInd) {
        this.trInd = trInd;
    }


    /**
     * Gets the principalId attribute.
     * @return Returns the principalId.
     */
    @Column(name = "prncpl_id", length = 40, nullable = true)
    public String getPrincipalId() {
        setupPrincipal();
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Column(name="ACTV_IND",nullable=false,length=1)
    public boolean getActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the primary attribute.
     * @return Returns the primary.
     */
    @Column(name="primary_ind",nullable=false,length=1)
    public boolean getPrimary() {
        return primary;
    }


    /**
     * Sets the primary attribute value.
     * @param primary The primary to set.
     */
    public void setPrimary(boolean primary) {
        this.primary = primary;
    }


    /**
     * Gets the profileId attribute.
     * @return Returns the profileId.
     */
    @Column(name = "profile_id", nullable = false, length=19)
    public Integer getProfileId() {
        return profileId;
    }


    /**
     * Sets the profileId attribute value.
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }


    /**
     * Gets the profile attribute.
     * @return Returns the profile.
     */
    @JoinColumn(name = "profile_id")
    public TEMProfile getProfile() {
        return profile;
    }


    /**
     * Sets the profile attribute value.
     * @param profile The profile to set.
     */
    public void setProfile(TEMProfile profile) {
        this.profile = profile;
    }


    /**
     * Gets the principalName attribute.
     * @return Returns the principalName.
     */
    public String getPrincipalName() {
        setupPrincipal();
        return principalName;
    }


    /**
     * Sets the principalName attribute value.
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }


    /**
     * Sets the principal attribute value.
     * @param principal The principal to set.
     */
    public void setPrincipal(Person principal) {
        this.principal = principal;
        setPrincipalId(principal.getPrincipalId());
        setPrincipalName(principal.getPrincipalName());
    }


    /**
     * Gets the principal attribute.
     * @return Returns the principal.
     */
    public Person getPrincipal() {
        return principal;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     *
     * This method sets up the principal data based on whether the id or name is currently set.
     */
    private void setupPrincipal() {
        if (getPrincipal() == null) {
            Person person = null;
            if(this.principalName != null){
                person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(this.principalName);
            }else if(this.principalId != null){
                person = SpringContext.getBean(PersonService.class).getPerson(this.principalId);
            }
            if (person != null) {
                setPrincipal(person);
            }
        }
    }
}