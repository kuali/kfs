/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.labor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class LaborObject extends BusinessObjectBase {

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
    
	/**
	 * Default constructor.
	 */
	public LaborObject() {

	}

	/**
	 * Gets the universityFiscalYear attribute.
	 * 
	 * @return - Returns the universityFiscalYear
	 * 
	 */
	public Integer getUniversityFiscalYear() { 
		return universityFiscalYear;
	}

	/**
	 * Sets the universityFiscalYear attribute.
	 * 
	 * @param - universityFiscalYear The universityFiscalYear to set.
	 * 
	 */
	public void setUniversityFiscalYear(Integer universityFiscalYear) {
		this.universityFiscalYear = universityFiscalYear;
	}


	/**
	 * Gets the chartOfAccountsCode attribute.
	 * 
	 * @return - Returns the chartOfAccountsCode
	 * 
	 */
	public String getChartOfAccountsCode() { 
		return chartOfAccountsCode;
	}

	/**
	 * Sets the chartOfAccountsCode attribute.
	 * 
	 * @param - chartOfAccountsCode The chartOfAccountsCode to set.
	 * 
	 */
	public void setChartOfAccountsCode(String chartOfAccountsCode) {
		this.chartOfAccountsCode = chartOfAccountsCode;
	}


	/**
	 * Gets the financialObjectCode attribute.
	 * 
	 * @return - Returns the financialObjectCode
	 * 
	 */
	public String getFinancialObjectCode() { 
		return financialObjectCode;
	}

	/**
	 * Sets the financialObjectCode attribute.
	 * 
	 * @param - financialObjectCode The financialObjectCode to set.
	 * 
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		this.financialObjectCode = financialObjectCode;
	}


	/**
	 * Gets the detailPositionRequiredIndicator attribute.
	 * 
	 * @return - Returns the detailPositionRequiredIndicator
	 * 
	 */
	public boolean isDetailPositionRequiredIndicator() { 
		return detailPositionRequiredIndicator;
	}
	

	/**
	 * Sets the detailPositionRequiredIndicator attribute.
	 * 
	 * @param - detailPositionRequiredIndicator The detailPositionRequiredIndicator to set.
	 * 
	 */
	public void setDetailPositionRequiredIndicator(boolean detailPositionRequiredIndicator) {
		this.detailPositionRequiredIndicator = detailPositionRequiredIndicator;
	}


	/**
	 * Gets the financialObjectHoursRequiredIndicator attribute.
	 * 
	 * @return - Returns the financialObjectHoursRequiredIndicator
	 * 
	 */
	public boolean isFinancialObjectHoursRequiredIndicator() { 
		return financialObjectHoursRequiredIndicator;
	}
	

	/**
	 * Sets the financialObjectHoursRequiredIndicator attribute.
	 * 
	 * @param - financialObjectHoursRequiredIndicator The financialObjectHoursRequiredIndicator to set.
	 * 
	 */
	public void setFinancialObjectHoursRequiredIndicator(boolean financialObjectHoursRequiredIndicator) {
		this.financialObjectHoursRequiredIndicator = financialObjectHoursRequiredIndicator;
	}


	/**
	 * Gets the financialObjectPayTypeCode attribute.
	 * 
	 * @return - Returns the financialObjectPayTypeCode
	 * 
	 */
	public String getFinancialObjectPayTypeCode() { 
		return financialObjectPayTypeCode;
	}

	/**
	 * Sets the financialObjectPayTypeCode attribute.
	 * 
	 * @param - financialObjectPayTypeCode The financialObjectPayTypeCode to set.
	 * 
	 */
	public void setFinancialObjectPayTypeCode(String financialObjectPayTypeCode) {
		this.financialObjectPayTypeCode = financialObjectPayTypeCode;
	}


	/**
	 * Gets the financialObjectFringeOrSalaryCode attribute.
	 * 
	 * @return - Returns the financialObjectFringeOrSalaryCode
	 * 
	 */
	public String getFinancialObjectFringeOrSalaryCode() { 
		return financialObjectFringeOrSalaryCode;
	}

	/**
	 * Sets the financialObjectFringeOrSalaryCode attribute.
	 * 
	 * @param - financialObjectFringeOrSalaryCode The financialObjectFringeOrSalaryCode to set.
	 * 
	 */
	public void setFinancialObjectFringeOrSalaryCode(String financialObjectFringeOrSalaryCode) {
		this.financialObjectFringeOrSalaryCode = financialObjectFringeOrSalaryCode;
	}


	/**
	 * Gets the positionObjectGroupCode attribute.
	 * 
	 * @return - Returns the positionObjectGroupCode
	 * 
	 */
	public String getPositionObjectGroupCode() { 
		return positionObjectGroupCode;
	}

	/**
	 * Sets the positionObjectGroupCode attribute.
	 * 
	 * @param - positionObjectGroupCode The positionObjectGroupCode to set.
	 * 
	 */
	public void setPositionObjectGroupCode(String positionObjectGroupCode) {
		this.positionObjectGroupCode = positionObjectGroupCode;
	}


	/**
	 * Gets the financialObject attribute.
	 * 
	 * @return - Returns the financialObject
	 * 
	 */
	public ObjectCode getFinancialObject() { 
		return financialObject;
	}

	/**
	 * Sets the financialObject attribute.
	 * 
	 * @param - financialObject The financialObject to set.
	 * @deprecated
	 */
	public void setFinancialObject(ObjectCode financialObject) {
		this.financialObject = financialObject;
	}

	/**
	 * Gets the chartOfAccounts attribute.
	 * 
	 * @return - Returns the chartOfAccounts
	 * 
	 */
	public Chart getChartOfAccounts() { 
		return chartOfAccounts;
	}

	/**
	 * Sets the chartOfAccounts attribute.
	 * 
	 * @param - chartOfAccounts The chartOfAccounts to set.
	 * @deprecated
	 */
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
     * @deprecated
     */
    public void setPositionObjectGroup(PositionObjectGroup positionObjectGroup) {
        this.positionObjectGroup = positionObjectGroup;
    }

    /**
     * @see org.kuali.bo.BusinessObjectBase#toStringMapper()
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
