/*
 * Copyright 2008-2009 The Kuali Foundation
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

import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Represents an entry in the Suspense Activity report for Contracts and Grants Invoices.
 */
public class ContractsGrantsInvoiceSuspenseActivityReport extends TransientBusinessObjectBase {

    private String fundManager;
    private String suspensionCategoryCode;
    private String categoryDescription;
    private Long totalInvoicesSuspended;
    private Long totalInvoicesCleared;
    private Long totalInvoicesRemainingToClear;

    private Person awardFundManager;
    private Person awardProjectDirector;

    private SuspensionCategory suspensionCategory;


    /**
     * Gets the fundManager attribute.
     * 
     * @return Returns the fundManager
     */
    public String getFundManager() {
        return fundManager;
    }

    /**
     * Sets the fundManager attribute value.
     * 
     * @param fundManager The fundManager to set.
     */
    public void setFundManager(String fundManager) {
        this.fundManager = fundManager;
    }

    /**
     * Gets the suspensionCategoryCode attribute.
     * 
     * @return Returns the suspensionCategoryCode
     */
    public String getSuspensionCategoryCode() {
        return suspensionCategoryCode;
    }

    /**
     * Sets the suspensionCategoryCode attribute value.
     * 
     * @param suspensionCategoryCode The suspensionCategoryCode to set.
     */
    public void setSuspensionCategoryCode(String suspensionCategoryCode) {
        this.suspensionCategoryCode = suspensionCategoryCode;
    }

    /**
     * Gets the categoryDescription attribute.
     * 
     * @return Returns the categoryDescription
     */
    public String getCategoryDescription() {
        return categoryDescription;
    }

    /**
     * Sets the categoryDescription attribute value.
     * 
     * @param categoryDescription The categoryDescription to set.
     */
    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    /**
     * Gets the totalInvoicesSuspended attribute.
     * 
     * @return Returns the totalInvoicesSuspended
     */
    public Long getTotalInvoicesSuspended() {
        return totalInvoicesSuspended;
    }

    /**
     * Sets the totalInvoicesSuspended attribute value.
     * 
     * @param totalInvoicesSuspended The totalInvoicesSuspended to set.
     */
    public void setTotalInvoicesSuspended(Long totalInvoicesSuspended) {
        this.totalInvoicesSuspended = totalInvoicesSuspended;
    }

    /**
     * Gets the suspensionCategory attribute.
     * 
     * @return Returns the suspensionCategory
     */
    public SuspensionCategory getSuspensionCategory() {
        return suspensionCategory;
    }

    /**
     * Sets the suspensionCategory attribute value.
     * 
     * @param suspensionCategory The suspensionCategory to set.
     */
    public void setSuspensionCategory(SuspensionCategory suspensionCategory) {
        this.suspensionCategory = suspensionCategory;
    }

    /**
     * Gets the totalInvoicesCleared attribute.
     * 
     * @return Returns the totalInvoicesCleared.
     */
    public Long getTotalInvoicesCleared() {
        return totalInvoicesCleared;
    }

    /**
     * Sets the totalInvoicesCleared attribute value.
     * 
     * @param totalInvoicesCleared The totalInvoicesCleared to set.
     */
    public void setTotalInvoicesCleared(Long totalInvoicesCleared) {
        this.totalInvoicesCleared = totalInvoicesCleared;
    }

    /**
     * Gets the totalInvoicesRemainingToClear attribute.
     * 
     * @return Returns the totalInvoicesRemainingToClear.
     */
    public Long getTotalInvoicesRemainingToClear() {
        return totalInvoicesRemainingToClear;
    }

    /**
     * Sets the totalInvoicesRemainingToClear attribute value.
     * 
     * @param totalInvoicesRemainingToClear The totalInvoicesRemainingToClear to set.
     */
    public void setTotalInvoicesRemainingToClear(Long totalInvoicesRemainingToClear) {
        this.totalInvoicesRemainingToClear = totalInvoicesRemainingToClear;
    }

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> toStringMap = new LinkedHashMap<String, Object>();
        toStringMap.put("fundManager", fundManager);
        toStringMap.put("suspensionCategoryCode", suspensionCategoryCode);
        toStringMap.put("categoryDescription", categoryDescription);

        if (this.totalInvoicesSuspended != null) {
            toStringMap.put("totalInvoicesSuspended", this.totalInvoicesSuspended.toString());
        }

        if (this.totalInvoicesCleared != null) {
            toStringMap.put("totalInvoicesCleared", this.totalInvoicesCleared.toString());
        }

        if (this.totalInvoicesRemainingToClear != null) {
            toStringMap.put("totalInvoicesRemainingToClear", this.totalInvoicesRemainingToClear.toString());
        }

        if (this.awardFundManager != null) {
            toStringMap.put("awardFundManager", this.awardFundManager.getPrincipalId());
        }

        if (this.awardProjectDirector != null) {
            toStringMap.put("awardProjectDirector", this.awardProjectDirector.getPrincipalId());
        }

        return toStringMap;
    }

    /**
     * Gets the awardFundManager attribute.
     * 
     * @return Returns the awardFundManager
     */
    public Person getAwardFundManager() {
        return awardFundManager;
    }

    /**
     * Sets the awardFundManager attribute value.
     * 
     * @param awardFundManager The awardFundManager to set.
     */
    public void setAwardFundManager(Person awardFundManager) {
        this.awardFundManager = awardFundManager;
    }

    /**
     * Gets the awardProjectDirector attribute.
     * 
     * @return Returns the awardProjectDirector
     */
    public Person getAwardProjectDirector() {
        return awardProjectDirector;
    }

    /**
     * Sets the awardProjectDirector attribute value.
     * 
     * @param awardProjectDirector The awardProjectDirector to set.
     */
    public void setAwardProjectDirector(Person awardProjectDirector) {
        this.awardProjectDirector = awardProjectDirector;
    }

}
