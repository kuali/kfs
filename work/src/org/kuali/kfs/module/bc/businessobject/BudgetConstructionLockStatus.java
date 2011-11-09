/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject;

import java.util.LinkedHashMap;
import java.util.SortedSet;

import org.kuali.kfs.module.bc.BCConstants.LockStatus;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * This class defines a BudgetConstructionLockStatus object. This object is used by many of the methods in the Budget module's
 * LockService as a mechanism to pass status information associated with the various types of locks. LockStatus uses enum values
 * SUCCESS, BY_OTHER, NO_DOOR, OPTIMISTIC_EX, FLOCK_FOUND LockStatus.BY_OTHER usually means one of the *LockOwner attributes is set
 * with the uid associated with the lock. LockStatus.FLOCK_FOUND usually means the set of fundingLocks is also defined. See the
 * LockService methods JavaDoc for more details.
 */
public class BudgetConstructionLockStatus extends TransientBusinessObjectBase{

    private LockStatus lockStatus;
    private String accountLockOwner;
    private String positionLockOwner;
    private String transactionLockOwner;
    private SortedSet<BudgetConstructionFundingLock> fundingLocks;
    private BudgetConstructionHeader budgetConstructionHeader;

    /**
     * Constructs a BudgetConstructionLockStatus object.
     */
    public BudgetConstructionLockStatus() {
        lockStatus = LockStatus.NO_DOOR;
        accountLockOwner = null;
        positionLockOwner = null;
        fundingLocks = null;
        transactionLockOwner = null;
        budgetConstructionHeader = null;
    }

    /**
     * This gets the accountLockOwner attribute
     * 
     * @return accountLockOwner
     */
    public String getAccountLockOwner() {
        return accountLockOwner;
    }

    /**
     * This sets the accountLockOwner attribute
     * 
     * @param accountLockOwner
     */
    public void setAccountLockOwner(String accountLockOwner) {
        this.accountLockOwner = accountLockOwner;
    }

    /**
     * This gets the lockStatus attribute
     * 
     * @return lockStatus
     */
    public LockStatus getLockStatus() {
        return lockStatus;
    }

    /**
     * This sets the lockStatus attribute
     * 
     * @param lockStatus
     */
    public void setLockStatus(LockStatus lockStatus) {
        this.lockStatus = lockStatus;
    }

    /**
     * This gets the positionLockOwner attribute
     * 
     * @return positionLockOwner
     */
    public String getPositionLockOwner() {
        return positionLockOwner;
    }

    /**
     * This gets the positionLockOwner attribute
     * 
     * @param positionLockOwner
     */
    public void setPositionLockOwner(String positionLockOwner) {
        this.positionLockOwner = positionLockOwner;
    }

    /**
     * This gets the fundingLocks attribute
     * 
     * @return fundingLocks
     */
    public SortedSet<BudgetConstructionFundingLock> getFundingLocks() {
        return fundingLocks;
    }

    /**
     * This sets the fundingLocks attribute
     * 
     * @param fundingLocks
     */
    public void setFundingLocks(SortedSet<BudgetConstructionFundingLock> fundingLocks) {
        this.fundingLocks = fundingLocks;
    }

    /**
     * This gets the transactionLockOwner attribute
     * 
     * @return ansactionLockOwner
     */
    public String getTransactionLockOwner() {
        return transactionLockOwner;
    }

    /**
     * This gets the transactionLockOwner attribute
     * 
     * @param transactionLockOwner
     */
    public void setTransactionLockOwner(String transactionLockOwner) {
        this.transactionLockOwner = transactionLockOwner;
    }

    /**
     * Gets the budgetConstructionHeader attribute. 
     * @return Returns the budgetConstructionHeader.
     */
    public BudgetConstructionHeader getBudgetConstructionHeader() {
        return budgetConstructionHeader;
    }

    /**
     * Sets the budgetConstructionHeader attribute value.
     * @param budgetConstructionHeader The budgetConstructionHeader to set.
     */
    public void setBudgetConstructionHeader(BudgetConstructionHeader budgetConstructionHeader) {
        this.budgetConstructionHeader = budgetConstructionHeader;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String,Object> mapper = new LinkedHashMap<String,Object>();
        
        mapper.put("lockStatus", this.lockStatus);
        mapper.put("accountLockOwner", this.accountLockOwner);
        mapper.put("positionLockOwner", this.positionLockOwner);
        mapper.put("transactionLockOwner", this.transactionLockOwner);
        mapper.put("fundingLocks", this.fundingLocks);
        mapper.put("budgetConstructionHeader", this.budgetConstructionHeader);

        return mapper;
    }
}
