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

import org.kuali.KeyConstants;
import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.service.LaborBalanceInquiryService;
import org.springframework.beans.factory.BeanFactory;

public class AccountStatusCurrentFunds extends Balance {

    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialBalanceTypeCode;
    private String financialObjectTypeCode;

    private String emplid;
    private String personName;
    private String positionNumber;
    private KualiDecimal financialBeginningBalanceLineAmount;
    private KualiDecimal ytdActualAmount;
    private KualiDecimal outstandingEncum;

    private Chart chartOfAccounts;
    private ObjectType financialObjectType;
    private Balance financialBalance;
    private UniversalUserService universalUserService;
    private LaborBalanceInquiryService laborBalanceInquiryService;

    /**
     * Constructs an AccountStatusCurrentFunds.java.
     */
    public AccountStatusCurrentFunds() {
        super();
        this.setYtdActualAmount(KualiDecimal.ZERO);
        this.setOutstandingEncum(KualiDecimal.ZERO);
    }

    /**
     * Gets the financialBalance attribute.
     * 
     * @return Returns the financialBalance.
     */
    public Balance getFinancialBalance() {
        return financialBalance;
    }

    /**
     * Sets the financialBalance attribute value.
     * 
     * @param financialBalance The financialBalance to set.
     */
    public void setFinancialBalance(Balance financialBalance) {
        this.financialBalance = financialBalance;
    }

    /**
     * Gets the financialBalanceTypeCode attribute.
     * 
     * @return Returns the financialBalanceTypeCode.
     */
    public String getFinancialBalanceTypeCode() {
        return financialBalanceTypeCode;
    }

    /**
     * Sets the financialBalanceTypeCode attribute value.
     * 
     * @param financialBalanceTypeCode The financialBalanceTypeCode to set.
     */
    public void setFinancialBalanceTypeCode(String financialBalanceTypeCode) {
        this.financialBalanceTypeCode = financialBalanceTypeCode;
    }

    /**
     * Gets the financialObjectCode attribute.
     * 
     * @return Returns the financialObjectCode.
     */
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Sets the financialObjectCode attribute value.
     * 
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialObjectTypeCode attribute.
     * 
     * @return Returns the financialObjectTypeCode.
     */
    public String getFinancialObjectTypeCode() {
        return financialObjectTypeCode;
    }

    /**
     * Sets the financialObjectTypeCode attribute value.
     * 
     * @param financialObjectTypeCode The financialObjectTypeCode to set.
     */
    public void setFinancialObjectTypeCode(String financialObjectTypeCode) {
        this.financialObjectTypeCode = financialObjectTypeCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     * 
     * @return Returns the financialSubObjectCode.
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute value.
     * 
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber.
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute value.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }

    @Override
    public String getBalanceTypeCode() {
        return this.getFinancialBalanceTypeCode();
    }

    @Override
    public void setBalanceTypeCode(String balanceTypeCode) {
        this.setFinancialBalanceTypeCode(balanceTypeCode);
    }

    @Override
    public Chart getChart() {
        return this.getChartOfAccounts();
    }

    @Override
    public void setChart(Chart chart) {
        this.setChartOfAccounts(chart);
    }

    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts.
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute value.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the financialObjectType attribute.
     * 
     * @return Returns the financialObjectType.
     */
    public ObjectType getFinancialObjectType() {
        return financialObjectType;
    }

    /**
     * Sets the financialObjectType attribute value.
     * 
     * @param financialObjectType The financialObjectType to set.
     */
    public void setFinancialObjectType(ObjectType financialObjectType) {
        this.financialObjectType = financialObjectType;
    }

    @Override
    public String getObjectCode() {
        return this.getFinancialObjectCode();
    }

    @Override
    public void setObjectCode(String objectCode) {
        this.setFinancialObjectCode(objectCode);
    }

    @Override
    public ObjectType getObjectType() {
        return this.getFinancialObjectType();
    }

    @Override
    public void setObjectType(ObjectType objectType) {
        this.setFinancialObjectType(objectType);
    }

    @Override
    public String getObjectTypeCode() {
        return this.getFinancialObjectTypeCode();
    }

    @Override
    public void setObjectTypeCode(String objectTypeCode) {
        this.setFinancialObjectTypeCode(objectTypeCode);
    }

    @Override
    public String getSubObjectCode() {
        return this.getFinancialSubObjectCode();
    }

    @Override
    public void setSubObjectCode(String subObjectCode) {
        this.setFinancialSubObjectCode(subObjectCode);
    }

    public String getEmplid() {
        return emplid;
    }

    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }
    
    public String getPersonName() {
        UserId empl = new PersonPayrollId(getEmplid());
        UniversalUser universalUser = null;
        
        try{
            universalUser = SpringServiceLocator.getUniversalUserService().getUniversalUser(empl);
        }catch(UserNotFoundException e){
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return universalUser.getPersonName();
    }        
        
    public String getPersonName2() {
        UniversalUser universalUser;

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborBalanceInquiryService = (LaborBalanceInquiryService) beanFactory.getBean("laborBalanceInquiryService");
        universalUser = laborBalanceInquiryService.getUniversalUserByEmplid(getEmplid());
        return universalUser.getPersonName();
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public KualiDecimal getYtdActualAmount() {
        return ytdActualAmount;
    }

    public void setYtdActualAmount(KualiDecimal ytdActualAmount) {
        this.ytdActualAmount = ytdActualAmount;
    }

    public KualiDecimal getOutstandingEncum() {
        return outstandingEncum;
    }

    public void setOutstandingEncum(KualiDecimal outstandingEncum) {
        this.outstandingEncum = outstandingEncum;
    }

    public KualiDecimal getFinancialBeginningBalanceLineAmount() {
        return financialBeginningBalanceLineAmount;
    }

    public void setFinancialBeginningBalanceLineAmount(KualiDecimal financialBeginningBalanceLineAmount) {
        this.financialBeginningBalanceLineAmount = financialBeginningBalanceLineAmount;
    }
}