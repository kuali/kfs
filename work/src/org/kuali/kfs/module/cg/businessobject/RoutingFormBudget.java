/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.kra.routingform.bo;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */
public class RoutingFormBudget extends PersistableBusinessObjectBase {

	private String documentNumber;
	private Integer routingFormBudgetMaximumPeriodNumber;
	private Integer routingFormBudgetMinimumPeriodNumber;
    private String routingFormBudgetIndirectCostDescription;
    
    private KualiInteger routingFormBudgetDirectAmount;
    private KualiInteger routingFormBudgetTotalDirectAmount;
    private KualiInteger routingFormBudgetIndirectCostAmount;
    private KualiInteger routingFormBudgetTotalIndirectCostAmount;
    
    private Date routingFormBudgetEndDate;
    private Date routingFormBudgetStartDate;
    private Date routingFormBudgetTotalEndDate;
    private Date routingFormBudgetTotalStartDate;
   
	/**
	 * Default constructor.
	 */
	public RoutingFormBudget() {

	}

	/**
	 * Gets the documentNumber attribute.
	 * 
	 * @return Returns the documentNumber
	 * 
	 */
	public String getDocumentNumber() { 
		return documentNumber;
	}

	/**
	 * Sets the documentNumber attribute.
	 * 
	 * @param documentNumber The documentNumber to set.
	 * 
	 */
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}


	/**
	 * Gets the routingFormBudgetMaximumPeriodNumber attribute.
	 * 
	 * @return Returns the routingFormBudgetMaximumPeriodNumber
	 * 
	 */
	public Integer getRoutingFormBudgetMaximumPeriodNumber() { 
		return routingFormBudgetMaximumPeriodNumber;
	}

	/**
	 * Sets the routingFormBudgetMaximumPeriodNumber attribute.
	 * 
	 * @param routingFormBudgetMaximumPeriodNumber The routingFormBudgetMaximumPeriodNumber to set.
	 * 
	 */
	public void setRoutingFormBudgetMaximumPeriodNumber(Integer routingFormBudgetMaximumPeriodNumber) {
		this.routingFormBudgetMaximumPeriodNumber = routingFormBudgetMaximumPeriodNumber;
	}


	/**
	 * Gets the routingFormBudgetMinimumPeriodNumber attribute.
	 * 
	 * @return Returns the routingFormBudgetMinimumPeriodNumber
	 * 
	 */
	public Integer getRoutingFormBudgetMinimumPeriodNumber() { 
		return routingFormBudgetMinimumPeriodNumber;
	}

	/**
	 * Sets the routingFormBudgetMinimumPeriodNumber attribute.
	 * 
	 * @param routingFormBudgetMinimumPeriodNumber The routingFormBudgetMinimumPeriodNumber to set.
	 * 
	 */
	public void setRoutingFormBudgetMinimumPeriodNumber(Integer routingFormBudgetMinimumPeriodNumber) {
		this.routingFormBudgetMinimumPeriodNumber = routingFormBudgetMinimumPeriodNumber;
	}


	/**
	 * Gets the routingFormBudgetDirectAmount attribute.
	 * 
	 * @return Returns the routingFormBudgetDirectAmount
	 * 
	 */
	public KualiInteger getRoutingFormBudgetDirectAmount() { 
		return routingFormBudgetDirectAmount;
	}

	/**
	 * Sets the routingFormBudgetDirectAmount attribute.
	 * 
	 * @param routingFormBudgetDirectAmount The routingFormBudgetDirectAmount to set.
	 * 
	 */
	public void setRoutingFormBudgetDirectAmount(KualiInteger routingFormBudgetDirectAmount) {
		this.routingFormBudgetDirectAmount = routingFormBudgetDirectAmount;
	}


	/**
	 * Gets the routingFormBudgetEndDate attribute.
	 * 
	 * @return Returns the routingFormBudgetEndDate
	 * 
	 */
	public Date getRoutingFormBudgetEndDate() { 
		return routingFormBudgetEndDate;
	}

	/**
	 * Sets the routingFormBudgetEndDate attribute.
	 * 
	 * @param routingFormBudgetEndDate The routingFormBudgetEndDate to set.
	 * 
	 */
	public void setRoutingFormBudgetEndDate(Date routingFormBudgetEndDate) {
		this.routingFormBudgetEndDate = routingFormBudgetEndDate;
	}


	/**
	 * Gets the routingFormBudgetIndirectCostAmount attribute.
	 * 
	 * @return Returns the routingFormBudgetIndirectCostAmount
	 * 
	 */
	public KualiInteger getRoutingFormBudgetIndirectCostAmount() { 
		return routingFormBudgetIndirectCostAmount;
	}

	/**
	 * Sets the routingFormBudgetIndirectCostAmount attribute.
	 * 
	 * @param routingFormBudgetIndirectCostAmount The routingFormBudgetIndirectCostAmount to set.
	 * 
	 */
	public void setRoutingFormBudgetIndirectCostAmount(KualiInteger routingFormBudgetIndirectCostAmount) {
		this.routingFormBudgetIndirectCostAmount = routingFormBudgetIndirectCostAmount;
	}


	/**
	 * Gets the routingFormBudgetIndirectCostDescription attribute.
	 * 
	 * @return Returns the routingFormBudgetIndirectCostDescription
	 * 
	 */
	public String getRoutingFormBudgetIndirectCostDescription() { 
		return routingFormBudgetIndirectCostDescription;
	}

	/**
	 * Sets the routingFormBudgetIndirectCostDescription attribute.
	 * 
	 * @param routingFormBudgetIndirectCostDescription The routingFormBudgetIndirectCostDescription to set.
	 * 
	 */
	public void setRoutingFormBudgetIndirectCostDescription(String routingFormBudgetIndirectCostDescription) {
		this.routingFormBudgetIndirectCostDescription = routingFormBudgetIndirectCostDescription;
	}


	/**
	 * Gets the routingFormBudgetStartDate attribute.
	 * 
	 * @return Returns the routingFormBudgetStartDate
	 * 
	 */
	public Date getRoutingFormBudgetStartDate() { 
		return routingFormBudgetStartDate;
	}

	/**
	 * Sets the routingFormBudgetStartDate attribute.
	 * 
	 * @param routingFormBudgetStartDate The routingFormBudgetStartDate to set.
	 * 
	 */
	public void setRoutingFormBudgetStartDate(Date routingFormBudgetStartDate) {
		this.routingFormBudgetStartDate = routingFormBudgetStartDate;
	}

    /**
     * Gets the routingFormBudgetTotalDirectAmount attribute. 
     * @return Returns the routingFormBudgetTotalDirectAmount.
     */
    public KualiInteger getRoutingFormBudgetTotalDirectAmount() {
        return routingFormBudgetTotalDirectAmount;
    }

    /**
     * Sets the routingFormBudgetTotalDirectAmount attribute value.
     * @param routingFormBudgetTotalDirectAmount The routingFormBudgetTotalDirectAmount to set.
     */
    public void setRoutingFormBudgetTotalDirectAmount(KualiInteger routingFormBudgetTotalDirectAmount) {
        this.routingFormBudgetTotalDirectAmount = routingFormBudgetTotalDirectAmount;
    }

    /**
     * Gets the routingFormBudgetTotalEndDate attribute. 
     * @return Returns the routingFormBudgetTotalEndDate.
     */
    public Date getRoutingFormBudgetTotalEndDate() {
        return routingFormBudgetTotalEndDate;
    }

    /**
     * Sets the routingFormBudgetTotalEndDate attribute value.
     * @param routingFormBudgetTotalEndDate The routingFormBudgetTotalEndDate to set.
     */
    public void setRoutingFormBudgetTotalEndDate(Date routingFormBudgetTotalEndDate) {
        this.routingFormBudgetTotalEndDate = routingFormBudgetTotalEndDate;
    }

    /**
     * Gets the routingFormBudgetTotalIndirectCostAmount attribute. 
     * @return Returns the routingFormBudgetTotalIndirectCostAmount.
     */
    public KualiInteger getRoutingFormBudgetTotalIndirectCostAmount() {
        return routingFormBudgetTotalIndirectCostAmount;
    }

    /**
     * Sets the routingFormBudgetTotalIndirectCostAmount attribute value.
     * @param routingFormBudgetTotalIndirectCostAmount The routingFormBudgetTotalIndirectCostAmount to set.
     */
    public void setRoutingFormBudgetTotalIndirectCostAmount(KualiInteger routingFormBudgetTotalIndirectCostAmount) {
        this.routingFormBudgetTotalIndirectCostAmount = routingFormBudgetTotalIndirectCostAmount;
    }

    /**
     * Gets the routingFormBudgetTotalStartDate attribute. 
     * @return Returns the routingFormBudgetTotalStartDate.
     */
    public Date getRoutingFormBudgetTotalStartDate() {
        return routingFormBudgetTotalStartDate;
    }

    /**
     * Sets the routingFormBudgetTotalStartDate attribute value.
     * @param routingFormBudgetTotalStartDate The routingFormBudgetTotalStartDate to set.
     */
    public void setRoutingFormBudgetTotalStartDate(Date routingFormBudgetTotalStartDate) {
        this.routingFormBudgetTotalStartDate = routingFormBudgetTotalStartDate;
    }

    /**
     * Returns the sum of routingFormBudgetDirectAmount and routingFormBudgetIndirectCostAmount.
     * @return
     */
    public KualiInteger getTotalCostsCurrentPeriod() {
        KualiInteger totalCosts = new KualiInteger(0);
        if (this.getRoutingFormBudgetDirectAmount() != null) totalCosts = totalCosts.add(this.getRoutingFormBudgetDirectAmount());
        if (this.getRoutingFormBudgetIndirectCostAmount() != null) totalCosts = totalCosts.add(this.getRoutingFormBudgetIndirectCostAmount());
        
        return totalCosts;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }
    
    public void resetRoutingFormBudgetData() {
        this.setRoutingFormBudgetDirectAmount(KualiInteger.ZERO);
        this.setRoutingFormBudgetIndirectCostAmount(KualiInteger.ZERO);
        this.setRoutingFormBudgetTotalDirectAmount(KualiInteger.ZERO);
        this.setRoutingFormBudgetTotalIndirectCostAmount(KualiInteger.ZERO);
        this.setRoutingFormBudgetStartDate(null);
        this.setRoutingFormBudgetEndDate(null);
        this.setRoutingFormBudgetTotalStartDate(null);
        this.setRoutingFormBudgetTotalEndDate(null);

    }

}
