/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.cg.document;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Instances of this class are used to signal to the CloseBatchStep that a close should occur on a particular day.
 */
public class ProposalAwardCloseDocument extends FinancialSystemTransactionalDocumentBase {

    protected Date closeOnOrBeforeDate;
    protected Date userInitiatedCloseDate;
    protected Long awardClosedCount;
    protected Long proposalClosedCount;
    protected String principalName;

    protected Person personUser;

    /**
     * Default constructor.
     */
    public ProposalAwardCloseDocument() {
    }

    /**
     * @return whether or not this document has been approved.
     */
    public boolean isApproved() {
        return KFSConstants.DocumentStatusCodes.APPROVED.equals(getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
    }

    /**
     * Gets the closeOnOrBeforeDate attribute.
     * 
     * @return Returns the closeOnOrBeforeDate
     */
    public Date getCloseOnOrBeforeDate() {
        return closeOnOrBeforeDate;
    }

    /**
     * Sets the closeOnOrBeforeDate attribute.
     * 
     * @param closeOnOrBeforeDate The closeOnOrBeforeDate to set.
     */
    public void setCloseOnOrBeforeDate(Date closeOnOrBeforeDate) {
        this.closeOnOrBeforeDate = closeOnOrBeforeDate;
    }


    /**
     * Gets the awardClosedCount attribute.
     * 
     * @return Returns the awardClosedCount
     */
    public Long getAwardClosedCount() {
        return awardClosedCount;
    }

    /**
     * Sets the awardClosedCount attribute.
     * 
     * @param awardClosedCount The awardClosedCount to set.
     */
    public void setAwardClosedCount(Long awardClosedCount) {
        this.awardClosedCount = awardClosedCount;
    }


    /**
     * Gets the proposalClosedCount attribute.
     * 
     * @return Returns the proposalClosedCount
     */
    public Long getProposalClosedCount() {
        return proposalClosedCount;
    }

    /**
     * Sets the proposalClosedCount attribute.
     * 
     * @param proposalClosedCount The proposalClosedCount to set.
     */
    public void setProposalClosedCount(Long proposalClosedCount) {
        this.proposalClosedCount = proposalClosedCount;
    }


    /**
     * Gets the principalName attribute.
     * 
     * @return Returns the principalName
     */
    public String getPrincipalName() {
        return principalName;
    }

    /**
     * Sets the principalName attribute.
     * 
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }


    /**
     * Gets the userInitiatedCloseDate attribute.
     * 
     * @return Returns the userInitiatedCloseDate
     */
    public Date getUserInitiatedCloseDate() {
        return userInitiatedCloseDate;
    }

    /**
     * Sets the userInitiatedCloseDate attribute.
     * 
     * @param userInitiatedCloseDate The userInitiatedCloseDate to set.
     */
    public void setUserInitiatedCloseDate(Date userInitiatedCloseDate) {
        this.userInitiatedCloseDate = userInitiatedCloseDate;
    }

    /**
     * @return the {@link Person} for the personUser
     */
    public Person getPersonUser() {
        personUser = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).updatePersonIfNecessary(principalName, personUser);
        return personUser;
    }

    /**
     * @param personUser The personUser to set.
     * @deprecated
     */
    public void setPersonUser(Person personUser) {
        this.personUser = personUser;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.documentNumber != null) {
            m.put("documentNumber", this.documentNumber.toString());
        }
        return m;
    }
}

