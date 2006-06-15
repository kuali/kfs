package org.kuali.module.chart.bo;

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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class HigherEdFunction extends BusinessObjectBase {

    private String financialHigherEdFunctionCd;
    private String financialHigherEdFunctionNm;
    private String finUnivBdgtOfficeFunctionCd;
    private String finAicpaFunctionCode;
    private String financialFederalFunctionCode;

    private UniversityBudgetOfficeFunction universityBudgetOfficeFunction;
    private FederalFunction federalFunction;
    private AicpaFunction aicpaFunction; // American Institute of Certified Public Accountants

    /**
     * Default no-arg constructor.
     */
    public HigherEdFunction() {

    }

    /**
     * Gets the financialHigherEdFunctionCd attribute.
     * 
     * @return - Returns the financialHigherEdFunctionCd
     * 
     */
    public String getFinancialHigherEdFunctionCd() {
        return financialHigherEdFunctionCd;
    }

    /**
     * Sets the financialHigherEdFunctionCd attribute.
     * 
     * @param financialHigherEdFunctionCd The financialHigherEdFunctionCd to set.
     * 
     */
    public void setFinancialHigherEdFunctionCd(String financialHigherEdFunctionCd) {
        this.financialHigherEdFunctionCd = financialHigherEdFunctionCd;
    }

    /**
     * Gets the financialHigherEdFunctionNm attribute.
     * 
     * @return - Returns the financialHigherEdFunctionNm
     * 
     */
    public String getFinancialHigherEdFunctionNm() {
        return financialHigherEdFunctionNm;
    }

    /**
     * Sets the financialHigherEdFunctionNm attribute.
     * 
     * @param financialHigherEdFunctionNm The financialHigherEdFunctionNm to set.
     * 
     */
    public void setFinancialHigherEdFunctionNm(String financialHigherEdFunctionNm) {
        this.financialHigherEdFunctionNm = financialHigherEdFunctionNm;
    }

    /**
     * Gets the finUnivBdgtOfficeFunctionCd attribute.
     * 
     * @return - Returns the finUnivBdgtOfficeFunctionCd
     * 
     */
    public String getFinUnivBdgtOfficeFunctionCd() {
        return finUnivBdgtOfficeFunctionCd;
    }

    /**
     * Sets the finUnivBdgtOfficeFunctionCd attribute.
     * 
     * @param finUnivBdgtOfficeFunctionCd The finUnivBdgtOfficeFunctionCd to set.
     * 
     */
    public void setFinUnivBdgtOfficeFunctionCd(String finUnivBdgtOfficeFunctionCd) {
        this.finUnivBdgtOfficeFunctionCd = finUnivBdgtOfficeFunctionCd;
    }

    /**
     * Gets the finAicpaFunctionCode attribute.
     * 
     * @return - Returns the finAicpaFunctionCode
     * 
     */
    public String getFinAicpaFunctionCode() {
        return finAicpaFunctionCode;
    }

    /**
     * Sets the finAicpaFunctionCode attribute.
     * 
     * @param finAicpaFunctionCode The finAicpaFunctionCode to set.
     * 
     */
    public void setFinAicpaFunctionCode(String finAicpaFunctionCode) {
        this.finAicpaFunctionCode = finAicpaFunctionCode;
    }

    /**
     * Gets the financialFederalFunctionCode attribute.
     * 
     * @return - Returns the financialFederalFunctionCode
     * 
     */
    public String getFinancialFederalFunctionCode() {
        return financialFederalFunctionCode;
    }

    /**
     * Sets the financialFederalFunctionCode attribute.
     * 
     * @param financialFederalFunctionCode The financialFederalFunctionCode to set.
     * 
     */
    public void setFinancialFederalFunctionCode(String financialFederalFunctionCode) {
        this.financialFederalFunctionCode = financialFederalFunctionCode;
    }

    /**
     * @return Returns the universityBudgetOfficeFunction.
     */
    public UniversityBudgetOfficeFunction getUniversityBudgetOfficeFunction() {
        return universityBudgetOfficeFunction;
    }

    /**
     * @param universityBudgetOfficeFunction The universityBudgetOfficeFunction to set.
     * @deprecated
     */
    public void setUniversityBudgetOfficeFunction(UniversityBudgetOfficeFunction universityBudgetOfficeFunction) {
        this.universityBudgetOfficeFunction = universityBudgetOfficeFunction;
    }

    /**
     * @return Returns the federalFunction.
     */
    public FederalFunction getFederalFunction() {
        return federalFunction;
    }

    /**
     * @param federalFunction The federalFunction to set.
     * @deprecated
     */
    public void setFederalFunction(FederalFunction federalFunction) {
        this.federalFunction = federalFunction;
    }

    /**
     * @return Returns the aicpaFunction.
     */
    public AicpaFunction getAicpaFunction() {
        return aicpaFunction;
    }

    /**
     * @param aicpaFunction The aicpaFunction to set.
     * @deprecated
     */
    public void setAicpaFunction(AicpaFunction aicpaFunction) {
        this.aicpaFunction = aicpaFunction;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialHigherEdFunctionCd", this.financialHigherEdFunctionCd);
        return m;
    }


}