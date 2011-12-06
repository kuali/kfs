/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.businessobject;

import org.kuali.rice.kns.bo.PersistableBusinessObjectExtensionBase;

public class BenefitsCalculationExtension extends PersistableBusinessObjectExtensionBase {
    private String accountCodeOffset;
    private String objectCodeOffset;
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String positionBenefitTypeCode;
    private String laborBenefitRateCategoryCode;
    
    public BenefitsCalculationExtension() {
        
    }

    /**
     * Gets the accountCodeOffset attribute. 
     * @return Returns the accountCodeOffset.
     */
    public String getAccountCodeOffset() {
        return accountCodeOffset;
    }

    /**
     * Sets the accountCodeOffset attribute value.
     * @param accountCodeOffset The accountCodeOffset to set.
     */
    public void setAccountCodeOffset(String accountCodeOffset) {
        this.accountCodeOffset = accountCodeOffset;
    }

    /**
     * Gets the objectCodeOffset attribute. 
     * @return Returns the objectCodeOffset.
     */
    public String getObjectCodeOffset() {
        return objectCodeOffset;
    }

    /**
     * Sets the objectCodeOffset attribute value.
     * @param objectCodeOffset The objectCodeOffset to set.
     */
    public void setObjectCodeOffset(String objectCodeOffset) {
        this.objectCodeOffset = objectCodeOffset;
    }

    /**
     * Gets the universityFiscalYear attribute. 
     * @return Returns the universityFiscalYear.
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute value.
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    /**
     * Gets the chartOfAccountsCode attribute. 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the positionBenefitTypeCode attribute. 
     * @return Returns the positionBenefitTypeCode.
     */
    public String getPositionBenefitTypeCode() {
        return positionBenefitTypeCode;
    }

    /**
     * Sets the positionBenefitTypeCode attribute value.
     * @param positionBenefitTypeCode The positionBenefitTypeCode to set.
     */
    public void setPositionBenefitTypeCode(String positionBenefitTypeCode) {
        this.positionBenefitTypeCode = positionBenefitTypeCode;
    }

    /**
     * Gets the laborBenefitRateCategoryCode attribute. 
     * @return Returns the laborBenefitRateCategoryCode.
     */
    public String getLaborBenefitRateCategoryCode() {
        return laborBenefitRateCategoryCode;
    }

    /**
     * Sets the laborBenefitRateCategoryCode attribute value.
     * @param laborBenefitRateCategoryCode The laborBenefitRateCategoryCode to set.
     */
    public void setLaborBenefitRateCategoryCode(String laborBenefitRateCategoryCode) {
        this.laborBenefitRateCategoryCode = laborBenefitRateCategoryCode;
    }
    
}
