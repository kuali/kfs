/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.cg.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents an association between an award and a fund manager. It's like a reference to the fund manager from the
 * award. This way an award can maintain a collection of these references instead of owning fund managers directly.
 */
public class AwardFundManager extends PersistableBusinessObjectBase implements Primaryable, CGFundManager, MutableInactivatable, ContractsAndGrantsFundManager {

    private String principalId;
    private Long proposalNumber;
    private boolean awardPrimaryFundManagerIndicator;
    private String awardFundManagerProjectTitle;
    private boolean active = true;

    private Person fundManager;

    /**
     * Default no-args constructor.
     */
    public AwardFundManager() {
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#getPrincipalId()
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#setPrincipalId(java.lang.String)
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#getProposalNumber()
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#setProposalNumber(java.lang.Long)
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }


    /**
     * Gets the awardPrimaryFundManagerIndicator attribute.
     * 
     * @return Returns the awardPrimaryFundManagerIndicator
     */
    public boolean isAwardPrimaryFundManagerIndicator() {
        return awardPrimaryFundManagerIndicator;
    }


    /**
     * Sets the awardPrimaryFundManagerIndicator attribute.
     * 
     * @param awardPrimaryFundManagerIndicator The awardPrimaryFundManagerIndicator to set.
     */
    public void setAwardPrimaryFundManagerIndicator(boolean awardPrimaryFundManagerIndicator) {
        this.awardPrimaryFundManagerIndicator = awardPrimaryFundManagerIndicator;
    }


    /**
     * Gets the awardFundManagerProjectTitle attribute.
     * 
     * @return Returns the awardFundManagerProjectTitle
     */
    public String getAwardFundManagerProjectTitle() {
        return awardFundManagerProjectTitle;
    }

    /**
     * Sets the awardFundManagerProjectTitle attribute.
     * 
     * @param awardFundManagerProjectTitle The awardFundManagerProjectTitle to set.
     */
    public void setAwardFundManagerProjectTitle(String awardFundManagerProjectTitle) {
        this.awardFundManagerProjectTitle = awardFundManagerProjectTitle;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#getFundManager()
     */
    public Person getFundManager() {
        fundManager = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalId, fundManager);
        return fundManager;
    }

    /**
     * @see org.kuali.kfs.module.cg.businessobject.CGFundManager#setFundManager(org.kuali.kfs.module.cg.businessobject.FundManager)
     */
    public void setFundManager(Person fundManager) {
        this.fundManager = fundManager;
    }

    /**
     * @see Primaryable#isPrimary()
     */
    public boolean isPrimary() {
        return isAwardPrimaryFundManagerIndicator();
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUserLookupRoleName(String userLookupRoleName) {
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, this.principalId);
        m.put("awardFundManagerProjectTitle", this.awardFundManagerProjectTitle);
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
        return m;
    }

}
