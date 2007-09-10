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

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * Labor Object Code Business Object.
 */
public class LaborObject extends PersistableBusinessObjectBase implements Inactivateable {

	private Integer universityFiscalYear;
	private String chartOfAccountsCode;
	private String financialObjectCode;
	private boolean detailPositionRequiredIndicator;
	private boolean financialObjectHoursRequiredIndicator;
	private String financialObjectPayTypeCode;
	private String financialObjectFringeOrSalaryCode;
	private String positionObjectGroupCode;

    private ObjectCode financialObject;
	private Chart chartOfAccounts;
    private PositionObjectGroup positionObjectGroup;
    private Options option;
    private boolean active;
    
	/**
	 * Default constructor.
	 */
	public LaborObject() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the detailPositionRequiredIndicator attribute.
	 * 
	 * @return Returns the detailPositionRequiredIndicator
	 * 
	 */
	public boolean isDetailPositionRequiredIndicator() { 
		return detailPositionRequiredIndicator;
	}
	

	/**
	 * Sets the detailPositionRequiredIndicator attribute.
	 * 
	 * @param detailPositionRequiredIndicator The detailPositionRequiredIndicator to set.
	 * 
	 */
	public void setDetailPositionRequiredIndicator(boolean detailPositionRequiredIndicator) {
		this.detailPositionRequiredIndicator = detailPositionRequiredIndicator;
	}


	/**
	 * Gets the financialObjectHoursRequiredIndicator attribute.
	 * 
	 * @return Returns the financialObjectHoursRequiredIndicator
	 * 
	 */
	public boolean isFinancialObjectHoursRequiredIndicator() { 
		return financialObjectHoursRequiredIndicator;
	}
	

	/**
	 * Sets the financialObjectHoursRequiredIndicator attribute.
	 * 
	 * @param financialObjectHoursRequiredIndicator The financialObjectHoursRequiredIndicator to set.
	 * 
	 */
	public void setFinancialObjectHoursRequiredIndicator(boolean financialObjectHoursRequiredIndicator) {
		this.financialObjectHoursRequiredIndicator = financialObjectHoursRequiredIndicator;
	}


	/**
	 * Gets the financialObjectPayTypeCode attribute.
	 * 
	 * @return Returns the financialObjectPayTypeCode
	 * 
	 */
	public String getFinancialObjectPayTypeCode() { 
		return financialObjectPayTypeCode;
	}

	/**
	 * Sets the financialObjectPayTypeCode attribute.
	 * 
	 * @param financialObjectPayTypeCode The financialObjectPayTypeCode to set.
	 * 
	 */
	public void setFinancialObjectPayTypeCode(String financialObjectPayTypeCode) {
		this.financialObjectPayTypeCode = financialObjectPayTypeCode;
	}


	/**
	 * Gets the financialObjectFringeOrSalaryCode attribute.
	 * 
	 * @return Returns the financialObjectFringeOrSalaryCode
	 * 
	 */
	public String getFinancialObjectFringeOrSalaryCode() { 
		return financialObjectFringeOrSalaryCode;
	}

	/**
	 * Sets the financialObjectFringeOrSalaryCode attribute.
	 * 
	 * @param financialObjectFringeOrSalaryCode The financialObjectFringeOrSalaryCode to set.
	 * 
	 */
	public void setFinancialObjectFringeOrSalaryCode(String financialObjectFringeOrSalaryCode) {
		this.financialObjectFringeOrSalaryCode = financialObjectFringeOrSalaryCode;
	}


	/**
	 * Gets the positionObjectGroupCode attribute.
	 * 
	 * @return Returns the positionObjectGroupCode
	 * 
	 */
	public String getPositionObjectGroupCode() { 
		return positionObjectGroupCode;
	}

	/**
	 * Sets the positionObjectGroupCode attribute.
	 * 
	 * @param positionObjectGroupCode The positionObjectGroupCode to set.
	 * 
	 */
	public void setPositionObjectGroupCode(String positionObjectGroupCode) {
		this.positionObjectGroupCode = positionObjectGroupCode;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param financialObject The financialObject to set.
	 */
    @Deprecated
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param chartOfAccounts The chartOfAccounts to set.
	 */
    @Deprecated
	public void setChartOfAccounts(Chart chartOfAccounts) {
		this.chartOfAccounts = chartOfAccounts;
	}

    /**
     * Gets the positionObjectGroup attribute. 
     * @return Returns the positionObjectGroup.
     */
    public PositionObjectGroup getPositionObjectGroup() {
        return positionObjectGroup;
    }

    /**
     * Sets the positionObjectGroup attribute value.
     * @param positionObjectGroup The positionObjectGroup to set.
     */
    public void setPositionObjectGroup(PositionObjectGroup positionObjectGroup) {
        this.positionObjectGroup = positionObjectGroup;
    }
    
    /**
     * @see org.kuali.core.bo.Inactivateable#isActive()
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * @see org.kuali.core.bo.Inactivateable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the option attribute. 
     * @return Returns the option.
     */
    public Options getOption() {
        return option;
    }

    /**
     * Sets the option attribute value.
     * @param option The option to set.
     */
    public void setOption(Options option) {
        this.option = option;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("chartOfAccountsCode", this.chartOfAccountsCode);
        m.put("financialObjectCode", this.financialObjectCode);
        return m;
    }    

}
