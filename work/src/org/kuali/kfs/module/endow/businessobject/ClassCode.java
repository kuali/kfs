/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.KualiCodeBase;

/**
 * Business Object for Class Code table.
 */
public class ClassCode extends KualiCodeBase {

    private String securityReportingGrp;
    private String securityAccrualMethod;
    private String securityEndowmentTransactionCode;
    private String securityIncomeEndowmentTransactionPostCode;
    private boolean taxLotIndicator;
    private String classCodeType;
    private String valuationMethod;

    private SecurityReportingGroup reportingGroup;
    private AccrualMethod accrualMethod;
    private EndowmentTransactionCode endowmentTransactionCode;
    private EndowmentTransactionCode incomeEndowmentTransactionPost;
    private ClassCodeType codeType;
    private SecurityValuationMethod securityValuationMethod;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSConstants.GENERIC_CODE_PROPERTY_NAME, this.code);
        return m;
    }

    /**
     * This method gets the classCodeType.
     * 
     * @return classCodeType
     */
    public String getClassCodeType() {
        return classCodeType;
    }

    /**
     * This method sets the classCodeType.
     * 
     * @param classCodeType
     */
    public void setClassCodeType(String classCodeType) {
        this.classCodeType = classCodeType;
    }

    /**
     * This method gets the securityAccrualMethod
     * 
     * @return securityAccrualMethod
     */
    public String getSecurityAccrualMethod() {
        return securityAccrualMethod;
    }

    /**
     * This method sets securityAccrualMethod.
     * 
     * @param securityAccrualMethod
     */
    public void setSecurityAccrualMethod(String securityAccrualMethod) {
        this.securityAccrualMethod = securityAccrualMethod;
    }

    /**
     * This method gets the securityEndowmentTransactionCode.
     * 
     * @return securityEndowmentTransactionCode
     */
    public String getSecurityEndowmentTransactionCode() {
        return securityEndowmentTransactionCode;
    }

    /**
     * This method sets the securityEndowmentTransactionCode.
     * 
     * @param securityEndowmentTransactionCode
     */
    public void setSecurityEndowmentTransactionCode(String securityEndowmentTransactionCode) {
        this.securityEndowmentTransactionCode = securityEndowmentTransactionCode;
    }

    /**
     * This method gets the securityIncomeEndowmentTransactionPostCode.
     * 
     * @return securityIncomeEndowmentTransactionPostCode
     */
    public String getSecurityIncomeEndowmentTransactionPostCode() {
        return securityIncomeEndowmentTransactionPostCode;
    }

    /**
     * This method sets the securityIncomeEndowmentTransactionPostCode.
     * 
     * @param securityIncomeEndowmentTransactionPostCode
     */
    public void setSecurityIncomeEndowmentTransactionPostCode(String securityIncomeEndowmentTransactionPostCode) {
        this.securityIncomeEndowmentTransactionPostCode = securityIncomeEndowmentTransactionPostCode;
    }

    /**
     * This method gets the securityReportingGrp
     * 
     * @return securityReportingGrp
     */
    public String getSecurityReportingGrp() {
        return securityReportingGrp;
    }

    /**
     * This method sets the securityReportingGrp.
     * 
     * @param securityReportingGrp
     */
    public void setSecurityReportingGrp(String securityReportingGrp) {
        this.securityReportingGrp = securityReportingGrp;
    }

    /**
     * This method gets the taxLotIndicator.
     * 
     * @return taxLotIndicator
     */
    public boolean isTaxLotIndicator() {
        return taxLotIndicator;
    }

    /**
     * This method sets the taxLotIndicator.
     * 
     * @param taxLotIndicator
     */
    public void setTaxLotIndicator(boolean taxLotIndicator) {
        this.taxLotIndicator = taxLotIndicator;
    }

    /**
     * This method gets the valuationMethod.
     * 
     * @return valuationMethod
     */
    public String getValuationMethod() {
        return valuationMethod;
    }

    /**
     * This method sets the valuationMethod.
     * 
     * @param valuationMethod
     */
    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    /**
     * Gets the accrualMethod.
     * 
     * @return accrualMethod
     */
    public AccrualMethod getAccrualMethod() {
        return accrualMethod;
    }

    /**
     * Sets the accrualMethod.
     * 
     * @param accrualMethod
     */
    public void setAccrualMethod(AccrualMethod accrualMethod) {
        this.accrualMethod = accrualMethod;
    }

    /**
     * Gets the codeType.
     * 
     * @return codeType
     */
    public ClassCodeType getCodeType() {
        return codeType;
    }

    /**
     * Sets the codeType.
     * 
     * @param codeType
     */
    public void setCodeType(ClassCodeType codeType) {
        this.codeType = codeType;
    }

    /**
     * Gets the endowmentTransactionCode.
     * 
     * @return endowmentTransactionCode
     */
    public EndowmentTransactionCode getEndowmentTransactionCode() {
        return endowmentTransactionCode;
    }

    /**
     * Sets the endowmentTransactionCode.
     * 
     * @param endowmentTransactionCode
     */
    public void setEndowmentTransactionCode(EndowmentTransactionCode endowmentTransactionCode) {
        this.endowmentTransactionCode = endowmentTransactionCode;
    }

    /**
     * Gets the incomeEndowmentTransactionPost.
     * 
     * @return incomeEndowmentTransactionPost
     */
    public EndowmentTransactionCode getIncomeEndowmentTransactionPost() {
        return incomeEndowmentTransactionPost;
    }

    /**
     * Sets the incomeEndowmentTransactionPost.
     * 
     * @param incomeEndowmentTransactionPost
     */
    public void setIncomeEndowmentTransactionPost(EndowmentTransactionCode incomeEndowmentTransactionPost) {
        this.incomeEndowmentTransactionPost = incomeEndowmentTransactionPost;
    }

    /**
     * Gets the reportingGroup.
     * 
     * @return reportingGroup
     */
    public SecurityReportingGroup getReportingGroup() {
        return reportingGroup;
    }

    /**
     * Sets the reportingGroup.
     * 
     * @param reportingGroup
     */
    public void setReportingGroup(SecurityReportingGroup reportingGroup) {
        this.reportingGroup = reportingGroup;
    }

    /**
     * Gets the securityValuationMethod.
     * 
     * @return securityValuationMethod
     */
    public SecurityValuationMethod getSecurityValuationMethod() {
        return securityValuationMethod;
    }

    /**
     * Sets the securityValuationMethod.
     * 
     * @param securityValuationMethod
     */
    public void setSecurityValuationMethod(SecurityValuationMethod securityValuationMethod) {
        this.securityValuationMethod = securityValuationMethod;
    }

    /**
     * Gets the reportingGroup description.
     * 
     * @return reportingGroup description
     */
    public String getSecurityReportingGrpDesc() {

        if (reportingGroup != null) {
            return reportingGroup.getName();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the accrualMethod description.
     * 
     * @return
     */
    public String getAccrualMethodDesc() {
        if (accrualMethod != null) {
            return accrualMethod.getName();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Get endowmentTransactionCode description.
     * 
     * @return endowmentTransactionCode description
     */
    public String getEndowmentTransactionDesc() {
        if (endowmentTransactionCode != null) {
            return endowmentTransactionCode.getCodeAndDescription();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * Gets the incomeEndowmentTransactionPost description.
     * 
     * @return incomeEndowmentTransactionPost
     */
    public String getIncomeEndowmentTransactionPostDesc() {
        if (incomeEndowmentTransactionPost != null) {
            return incomeEndowmentTransactionPost.getCodeAndDescription();
        }
        else
            return KFSConstants.EMPTY_STRING;
    }

    /**
     * @see org.kuali.rice.krad.bo.KualiCodeBase#getCodeAndDescription()
     */
    @Override
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(code)) {
            return KFSConstants.EMPTY_STRING;
        }
        return super.getCodeAndDescription();
    }

}
