/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/coa/businessobject/HigherEdFunction.java,v $
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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
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
     * @return Returns the financialHigherEdFunctionCd
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
     * @return Returns the financialHigherEdFunctionNm
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
     * @return Returns the finUnivBdgtOfficeFunctionCd
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
     * @return Returns the finAicpaFunctionCode
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
     * @return Returns the financialFederalFunctionCode
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
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        String theString = getFinancialHigherEdFunctionCd() + " - " + getFinancialHigherEdFunctionNm();
        return theString;
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
