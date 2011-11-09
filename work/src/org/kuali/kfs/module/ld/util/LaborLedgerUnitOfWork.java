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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is a working unit of labor origin entry. It is formed by a group of slimilar labor origin entries. If any two entries
 * have the same values for the given fields, then they are similar and can be grouped.
 */
public class LaborLedgerUnitOfWork {
    private LaborOriginEntry workingEntry;
    private List<String> keyFields;
    private int numOfMember;

    /**
     * Constructs a LaborLedgerUnitOfWork.java.
     */
    public LaborLedgerUnitOfWork() {
        this(null);
    }

    /**
     * Constructs a LaborLedgerUnitOfWork.java.
     * 
     * @param laborOriginEntry the given origin entry
     */
    public LaborLedgerUnitOfWork(LaborOriginEntry laborOriginEntry) {
        this.resetLaborLedgerUnitOfWork(laborOriginEntry);
    }

    /**
     * Intialize the default key fields of the labor ledger unit of work with the given origin entry
     * 
     * @param laborOriginEntry the given origin entry
     */
    public void resetLaborLedgerUnitOfWork(LaborOriginEntry laborOriginEntry) {
        this.resetLaborLedgerUnitOfWork(laborOriginEntry, null);
    }

    /**
     * Intialize the specified key fields of the labor ledger unit of work with the given origin entry
     * 
     * @param laborOriginEntry the given origin entry
     * @param keyFields the keys to which values will be assigned
     */
    public void resetLaborLedgerUnitOfWork(LaborOriginEntry laborOriginEntry, List<String> keyFields) {
        this.workingEntry = new LaborOriginEntry();
        this.setKeyFields(keyFields);

        if (laborOriginEntry != null) {
            ObjectUtil.buildObject(workingEntry, laborOriginEntry, this.getKeyFields());

            boolean creditIndicator = KFSConstants.GL_CREDIT_CODE.equals(laborOriginEntry.getTransactionDebitCreditCode());
            KualiDecimal entryAmount = laborOriginEntry.getTransactionLedgerEntryAmount();
            KualiDecimal unitAmount = creditIndicator ? entryAmount.negated() : entryAmount;

            workingEntry.setTransactionLedgerEntryAmount(unitAmount);
            workingEntry.setTransactionDebitCreditCode(laborOriginEntry.getTransactionDebitCreditCode());
            numOfMember = 1;
        }
    }

    /**
     * add the given origin entry as a member of the working unit
     * 
     * @param laborOriginEntry the given origin entry
     * @return true if the given origin entry is successfully added into the current unit of work; otherwise, false
     */
    public boolean addEntryIntoUnit(LaborOriginEntry laborOriginEntry) {
        if (this.hasSameKey(laborOriginEntry)) {
            KualiDecimal unitAmount = workingEntry.getTransactionLedgerEntryAmount();
            KualiDecimal entryAmount = laborOriginEntry.getTransactionLedgerEntryAmount();

            // if the input entry has a "credit" code , then subtract its amount from the unit total amount
            boolean creditIndicator = KFSConstants.GL_CREDIT_CODE.equals(laborOriginEntry.getTransactionDebitCreditCode());
            unitAmount = creditIndicator ? unitAmount.subtract(entryAmount) : unitAmount.add(entryAmount);

            workingEntry.setTransactionLedgerEntryAmount(unitAmount);
            numOfMember++;

            return true;
        }
        return false;
    }

    /**
     * Determine if the given origin entry belongs to the current unit of work
     * 
     * @param laborOriginEntry the given origin entry
     * @return true if the given origin entry belongs to the current unit of work; otherwise, false
     */
    public boolean canContain(LaborOriginEntry laborOriginEntry) {
        return this.hasSameKey(laborOriginEntry);
    }

    /**
     * Determine if the given origin entry has the same key as the current unit of work
     * 
     * @param laborOriginEntry the given origin entry
     * @return true if the given origin entry has the same key as the current unit of work; otherwise, false
     */
    public boolean hasSameKey(LaborOriginEntry laborOriginEntry) {
        if(ObjectUtils.isNull(laborOriginEntry)) {
            return false;
        }
        
        return ObjectUtil.equals(workingEntry, laborOriginEntry, this.getKeyFields());
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        List<String> printablekeyFields = new ArrayList<String>(this.getKeyFields());
        printablekeyFields.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        return ObjectUtil.buildPropertyMap(workingEntry, printablekeyFields).toString();
    }

    /**
     * Gets the workingEntry attribute.
     * 
     * @return Returns the workingEntry.
     */
    public LaborOriginEntry getWorkingEntry() {
        return this.workingEntry;
    }

    /**
     * Sets the workingEntry attribute value.
     * 
     * @param workingEntry The workingEntry to set.
     */
    public void setWorkingEntry(LaborOriginEntry workingEntry) {
        this.workingEntry = workingEntry;
    }

    /**
     * Gets the keyFields attribute.
     * 
     * @return Returns the keyFields.
     */
    public List<String> getKeyFields() {
        return keyFields;
    }

    /**
     * Sets the keyFields attribute value.
     * 
     * @param keyFields The keyFields to set.
     */
    public void setKeyFields(List<String> keyFields) {
        this.keyFields = (keyFields != null) ? keyFields : this.getDefaultKeyFields();
    }

    /**
     * Gets the numOfMember attribute.
     * 
     * @return Returns the numOfMember.
     */
    public int getNumOfMember() {
        return numOfMember;
    }

    /**
     * Sets the numOfMember attribute value.
     * 
     * @param numOfMember The numOfMember to set.
     */
    public void setNumOfMember(int numOfMember) {
        this.numOfMember = numOfMember;
    }

    /**
     * Get the default key fields as a list
     */
    protected List<String> getDefaultKeyFields() {
        List<String> defaultKeyFields = new ArrayList<String>(LaborConstants.consolidationAttributesOfOriginEntry());
        defaultKeyFields.remove(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        defaultKeyFields.remove(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);

        return defaultKeyFields;
    }
}
