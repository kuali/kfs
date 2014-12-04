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
