/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.KualiCode;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class Chart extends PersistableBusinessObjectBase implements KualiCode {

    private static final long serialVersionUID = 4129020803214027609L;

    public static final String CACHE_NAME = KFSConstants.APPLICATION_NAMESPACE_CODE + "/" + "Chart";
    
    protected String finChartOfAccountDescription;
    protected boolean active;
    protected String finCoaManagerPrincipalId;
    protected String reportsToChartOfAccountsCode;
    protected String chartOfAccountsCode;
    protected String finAccountsPayableObjectCode;
    protected String finExternalEncumbranceObjCd;
    protected String finPreEncumbranceObjectCode;
    protected String financialCashObjectCode;
    protected String icrIncomeFinancialObjectCode;
    protected String finAccountsReceivableObjCode;
    protected String finInternalEncumbranceObjCd;
    protected String icrExpenseFinancialObjectCd;
    protected String incBdgtEliminationsFinObjCd;
    protected String expBdgtEliminationsFinObjCd;
    protected String fundBalanceObjectCode;

    protected ObjectCode incBdgtEliminationsFinObj;
    protected ObjectCode expBdgtEliminationsFinObj;
    protected ObjectCode finAccountsPayableObject;
    protected ObjectCode finExternalEncumbranceObj;
    protected ObjectCode finPreEncumbranceObject;
    protected ObjectCode financialCashObject;
    protected ObjectCode icrIncomeFinancialObject;
    protected ObjectCode finAccountsReceivableObj;
    protected ObjectCode finInternalEncumbranceObj;
    protected ObjectCode icrExpenseFinancialObject;
    protected ObjectCode fundBalanceObject;
    protected Person finCoaManager;
    protected Chart reportsToChartOfAccounts;

    private static transient ChartService chartService;
    
    /**
     * Gets the finChartOfAccountDescription attribute.
     * 
     * @return Returns the finChartOfAccountDescription
     */
    public String getFinChartOfAccountDescription() {
        return finChartOfAccountDescription;
    }

    /**
     * Sets the finChartOfAccountDescription attribute.
     * 
     * @param finChartOfAccountDescription The finChartOfAccountDescription to set.
     */
    public void setFinChartOfAccountDescription(String finChartOfAccountDescription) {
        this.finChartOfAccountDescription = finChartOfAccountDescription;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * Gets the finAccountsPayableObject attribute.
     * 
     * @return Returns the finAccountsPayableObject
     */
    public ObjectCode getFinAccountsPayableObject() {
        return finAccountsPayableObject;
    }

    /**
     * Sets the finAccountsPayableObject attribute.
     * 
     * @param finAccountsPayableObject The finAccountsPayableObject to set.
     * @deprecated
     */
    public void setFinAccountsPayableObject(ObjectCode finAccountsPayableObject) {
        this.finAccountsPayableObject = finAccountsPayableObject;
    }


    /**
     * Gets the finExternalEncumbranceObj attribute.
     * 
     * @return Returns the finExternalEncumbranceObj.
     */
    public ObjectCode getFinExternalEncumbranceObj() {
        return finExternalEncumbranceObj;
    }

    /**
     * Sets the finExternalEncumbranceObj attribute value.
     * 
     * @param finExternalEncumbranceObj The finExternalEncumbranceObj to set.
     * @deprecated
     */
    public void setFinExternalEncumbranceObj(ObjectCode finExternalEncumbranceObj) {
        this.finExternalEncumbranceObj = finExternalEncumbranceObj;
    }

    /**
     * Gets the finPreEncumbranceObject attribute.
     * 
     * @return Returns the finPreEncumbranceObject
     */
    public ObjectCode getFinPreEncumbranceObject() {
        return finPreEncumbranceObject;
    }

    /**
     * Sets the finPreEncumbranceObject attribute.
     * 
     * @param finPreEncumbranceObject The finPreEncumbranceObject to set.
     * @deprecated
     */
    public void setFinPreEncumbranceObject(ObjectCode finPreEncumbranceObject) {
        this.finPreEncumbranceObject = finPreEncumbranceObject;
    }

    /**
     * Gets the financialCashObject attribute.
     * 
     * @return Returns the financialCashObject
     */
    public ObjectCode getFinancialCashObject() {
        return financialCashObject;
    }

    /**
     * Sets the financialCashObject attribute.
     * 
     * @param financialCashObject The financialCashObject to set.
     * @deprecated
     */
    public void setFinancialCashObject(ObjectCode financialCashObject) {
        this.financialCashObject = financialCashObject;
    }

    /**
     * Gets the icrIncomeFinancialObject attribute.
     * 
     * @return Returns the icrIncomeFinancialObject
     */
    public ObjectCode getIcrIncomeFinancialObject() {
        return icrIncomeFinancialObject;
    }

    /**
     * Sets the icrIncomeFinancialObject attribute.
     * 
     * @param icrIncomeFinancialObject The icrIncomeFinancialObject to set.
     * @deprecated
     */
    public void setIcrIncomeFinancialObject(ObjectCode icrIncomeFinancialObject) {
        this.icrIncomeFinancialObject = icrIncomeFinancialObject;
    }

    /**
     * Gets the finAccountsReceivableObj attribute.
     * 
     * @return Returns the finAccountsReceivableObj
     */
    public ObjectCode getFinAccountsReceivableObj() {
        return finAccountsReceivableObj;
    }

    /**
     * Sets the finAccountsReceivableObj attribute.
     * 
     * @param finAccountsReceivableObj The finAccountsReceivableObj to set.
     * @deprecated
     */
    public void setFinAccountsReceivableObj(ObjectCode finAccountsReceivableObj) {
        this.finAccountsReceivableObj = finAccountsReceivableObj;
    }

    public Person getFinCoaManager() {
        finCoaManager = SpringContext.getBean(PersonService.class).updatePersonIfNecessary(getFinCoaManagerPrincipalId(), finCoaManager);
        return finCoaManager;
    }

    /**
     * Sets the finCoaManagerUniversal attribute.
     * 
     * @param finCoaManagerUniversal The finCoaManagerUniversal to set.
     * @deprecated
     */
    public void setFinCoaManager(Person finCoaManagerUniversal) {
        this.finCoaManager = finCoaManagerUniversal;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return Returns the reportsToChartOfAccounts
     */
    public Chart getReportsToChartOfAccounts() {
        return reportsToChartOfAccounts;
    }

    /**
     * Sets the reportsToChartOfAccounts attribute.
     * 
     * @param reportsToChartOfAccounts The reportsToChartOfAccounts to set.
     * @deprecated
     */
    public void setReportsToChartOfAccounts(Chart reportsToChartOfAccounts) {
        this.reportsToChartOfAccounts = reportsToChartOfAccounts;
    }

    /**
     * Gets the finInternalEncumbranceObj attribute.
     * 
     * @return Returns the finInternalEncumbranceObj.
     */
    public ObjectCode getFinInternalEncumbranceObj() {
        return finInternalEncumbranceObj;
    }

    /**
     * Sets the finInternalEncumbranceObj attribute value.
     * 
     * @param finInternalEncumbranceObj The finInternalEncumbranceObj to set.
     * @deprecated
     */
    public void setFinInternalEncumbranceObj(ObjectCode finInternalEncumbranceObj) {
        this.finInternalEncumbranceObj = finInternalEncumbranceObj;
    }

    /**
     * Gets the icrExpenseFinancialObject attribute.
     * 
     * @return Returns the icrExpenseFinancialObject.
     */
    public ObjectCode getIcrExpenseFinancialObject() {
        return icrExpenseFinancialObject;
    }

    /**
     * Sets the icrExpenseFinancialObject attribute value.
     * 
     * @param icrExpenseFinancialObject The icrExpenseFinancialObject to set.
     * @deprecated
     */
    public void setIcrExpenseFinancialObject(ObjectCode icrExpenseFinancialObject) {
        this.icrExpenseFinancialObject = icrExpenseFinancialObject;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * 
     * @return Returns the chartOfAccountsCode.
     */
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute value.
     * 
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Gets the expBdgtEliminationsFinObj attribute.
     * 
     * @return Returns the expBdgtEliminationsFinObj.
     */
    public ObjectCode getExpBdgtEliminationsFinObj() {
        return expBdgtEliminationsFinObj;
    }

    /**
     * Sets the expBdgtEliminationsFinObj attribute value.
     * 
     * @param expBdgtEliminationsFinObj The expBdgtEliminationsFinObj to set.
     * @deprecated
     */
    public void setExpBdgtEliminationsFinObj(ObjectCode expBdgtEliminationsFinObj) {
        this.expBdgtEliminationsFinObj = expBdgtEliminationsFinObj;
    }

    /**
     * Gets the incBdgtEliminationsFinObj attribute.
     * 
     * @return Returns the incBdgtEliminationsFinObj.
     */
    public ObjectCode getIncBdgtEliminationsFinObj() {
        return incBdgtEliminationsFinObj;
    }

    /**
     * Sets the incBdgtEliminationsFinObj attribute value.
     * 
     * @param incBdgtEliminationsFinObj The incBdgtEliminationsFinObj to set.
     * @deprecated
     */
    public void setIncBdgtEliminationsFinObj(ObjectCode incBdgtEliminationsFinObj) {
        this.incBdgtEliminationsFinObj = incBdgtEliminationsFinObj;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("chartOfAccountsCode", this.chartOfAccountsCode);

        return m;
    }

    /**
     * Gets the finAccountsPayableObjectCode attribute.
     * 
     * @return Returns the finAccountsPayableObjectCode.
     */
    public String getFinAccountsPayableObjectCode() {
        return finAccountsPayableObjectCode;
    }

    /**
     * Sets the finAccountsPayableObjectCode attribute value.
     * 
     * @param finAccountsPayableObjectCode The finAccountsPayableObjectCode to set.
     */
    public void setFinAccountsPayableObjectCode(String finAccountsPayableObjectCode) {
        this.finAccountsPayableObjectCode = finAccountsPayableObjectCode;
    }

    /**
     * Gets the finAccountsReceivableObjCode attribute.
     * 
     * @return Returns the finAccountsReceivableObjCode.
     */
    public String getFinAccountsReceivableObjCode() {
        return finAccountsReceivableObjCode;
    }

    /**
     * Sets the finAccountsReceivableObjCode attribute value.
     * 
     * @param finAccountsReceivableObjCode The finAccountsReceivableObjCode to set.
     */
    public void setFinAccountsReceivableObjCode(String finAccountsReceivableObjCode) {
        this.finAccountsReceivableObjCode = finAccountsReceivableObjCode;
    }

    /**
     * Gets the financialCashObjectCode attribute.
     * 
     * @return Returns the financialCashObjectCode.
     */
    public String getFinancialCashObjectCode() {
        return financialCashObjectCode;
    }

    /**
     * Sets the financialCashObjectCode attribute value.
     * 
     * @param financialCashObjectCode The financialCashObjectCode to set.
     */
    public void setFinancialCashObjectCode(String financialCashObjectCode) {
        this.financialCashObjectCode = financialCashObjectCode;
    }

    /**
     * Gets the finExternalEncumbranceObjCd attribute.
     * 
     * @return Returns the finExternalEncumbranceObjCd.
     */
    public String getFinExternalEncumbranceObjCd() {
        return finExternalEncumbranceObjCd;
    }

    /**
     * Sets the finExternalEncumbranceObjCd attribute value.
     * 
     * @param finExternalEncumbranceObjCd The finExternalEncumbranceObjCd to set.
     */
    public void setFinExternalEncumbranceObjCd(String finExternalEncumbranceObjCd) {
        this.finExternalEncumbranceObjCd = finExternalEncumbranceObjCd;
    }

    /**
     * Gets the finInternalEncumbranceObjCd attribute.
     * 
     * @return Returns the finInternalEncumbranceObjCd.
     */
    public String getFinInternalEncumbranceObjCd() {
        return finInternalEncumbranceObjCd;
    }

    /**
     * Sets the finInternalEncumbranceObjCd attribute value.
     * 
     * @param finInternalEncumbranceObjCd The finInternalEncumbranceObjCd to set.
     */
    public void setFinInternalEncumbranceObjCd(String finInternalEncumbranceObjCd) {
        this.finInternalEncumbranceObjCd = finInternalEncumbranceObjCd;
    }

    /**
     * Gets the finPreEncumbranceObjectCode attribute.
     * 
     * @return Returns the finPreEncumbranceObjectCode.
     */
    public String getFinPreEncumbranceObjectCode() {
        return finPreEncumbranceObjectCode;
    }

    /**
     * Sets the finPreEncumbranceObjectCode attribute value.
     * 
     * @param finPreEncumbranceObjectCode The finPreEncumbranceObjectCode to set.
     */
    public void setFinPreEncumbranceObjectCode(String finPreEncumbranceObjectCode) {
        this.finPreEncumbranceObjectCode = finPreEncumbranceObjectCode;
    }

    /**
     * Gets the icrExpenseFinancialObjectCd attribute.
     * 
     * @return Returns the icrExpenseFinancialObjectCd.
     */
    public String getIcrExpenseFinancialObjectCd() {
        return icrExpenseFinancialObjectCd;
    }

    /**
     * Sets the icrExpenseFinancialObjectCd attribute value.
     * 
     * @param icrExpenseFinancialObjectCd The icrExpenseFinancialObjectCd to set.
     */
    public void setIcrExpenseFinancialObjectCd(String icrExpenseFinancialObjectCd) {
        this.icrExpenseFinancialObjectCd = icrExpenseFinancialObjectCd;
    }

    /**
     * Gets the icrIncomeFinancialObjectCode attribute.
     * 
     * @return Returns the icrIncomeFinancialObjectCode.
     */
    public String getIcrIncomeFinancialObjectCode() {
        return icrIncomeFinancialObjectCode;
    }

    /**
     * Sets the icrIncomeFinancialObjectCode attribute value.
     * 
     * @param icrIncomeFinancialObjectCode The icrIncomeFinancialObjectCode to set.
     */
    public void setIcrIncomeFinancialObjectCode(String icrIncomeFinancialObjectCode) {
        this.icrIncomeFinancialObjectCode = icrIncomeFinancialObjectCode;
    }

    /**
     * Gets the expBdgtEliminationsFinObjCd attribute.
     * 
     * @return Returns the expBdgtEliminationsFinObjCd
     */
    public String getExpBdgtEliminationsFinObjCd() {
        return expBdgtEliminationsFinObjCd;
    }

    /**
     * Sets the expBdgtEliminationsFinObjCd attribute.
     * 
     * @param expBdgtEliminationsFinObjCd The expBdgtEliminationsFinObjCd to set.
     */
    public void setExpBdgtEliminationsFinObjCd(String expBdgtEliminationsFinObjCd) {
        this.expBdgtEliminationsFinObjCd = expBdgtEliminationsFinObjCd;
    }

    /**
     * Gets the incBdgtEliminationsFinObjCd attribute.
     * 
     * @return Returns the incBdgtEliminationsFinObjCd
     */
    public String getIncBdgtEliminationsFinObjCd() {
        return incBdgtEliminationsFinObjCd;
    }

    /**
     * Sets the incBdgtEliminationsFinObjCd attribute.
     * 
     * @param incBdgtEliminationsFinObjCd The incBdgtEliminationsFinObjCd to set.
     */
    public void setIncBdgtEliminationsFinObjCd(String incBdgtEliminationsFinObjCd) {
        this.incBdgtEliminationsFinObjCd = incBdgtEliminationsFinObjCd;
    }

    /**
     * Gets the finCoaManagerPrincipalId attribute.
     * 
     * @return Returns the finCoaManagerPrincipalId.
     */
    public String getFinCoaManagerPrincipalId() {
        if (StringUtils.isNotBlank(chartOfAccountsCode) && StringUtils.isBlank(finCoaManagerPrincipalId)) {
            Person chartManager = getChartService().getChartManager(chartOfAccountsCode);
            if (chartManager != null) {
                finCoaManager = chartManager;
                finCoaManagerPrincipalId = chartManager.getPrincipalId();
            } else {
                finCoaManagerPrincipalId = null;
                finCoaManager = null;
            }
        }
        return finCoaManagerPrincipalId;
    }

    /**
     * Sets the finCoaManagerPrincipalId attribute value.
     * 
     * @param finCoaManagerPrincipalId The finCoaManagerPrincipalId to set.
     */
    public void setFinCoaManagerPrincipalId(String finCoaManagerPrincipalId) {
        this.finCoaManagerPrincipalId = finCoaManagerPrincipalId;
    }

    /**
     * @return Returns the reportsToChartOfAccountsCode.
     */
    public String getReportsToChartOfAccountsCode() {
        return reportsToChartOfAccountsCode;
    }

    /**
     * @param reportsToChartOfAccountsCode The reportsToChartOfAccountsCode to set.
     */
    public void setReportsToChartOfAccountsCode(String reportsToChartOfAccountsCode) {
        this.reportsToChartOfAccountsCode = reportsToChartOfAccountsCode;
    }

    /**
     * Gets the fundBalanceObject attribute.
     * 
     * @return Returns the fundBalanceObject.
     */
    public ObjectCode getFundBalanceObject() {
        return fundBalanceObject;
    }

    /**
     * Sets the fundBalanceObject attribute value.
     * 
     * @param fundBalanceObject The fundBalanceObject to set.
     * @deprecated
     */
    public void setFundBalanceObject(ObjectCode fundBalanceObject) {
        this.fundBalanceObject = fundBalanceObject;
    }

    /**
     * Gets the fundBalanceObjectCode attribute.
     * 
     * @return Returns the fundBalanceObjectCode.
     */
    public String getFundBalanceObjectCode() {
        return fundBalanceObjectCode;
    }

    /**
     * Sets the fundBalanceObjectCode attribute value.
     * 
     * @param fundBalanceObjectCode The fundBalanceObjectCode to set.
     */
    public void setFundBalanceObjectCode(String fundBalanceObjectCode) {
        this.fundBalanceObjectCode = fundBalanceObjectCode;
    }

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */
    public String getCodeAndDescription() {
        if (StringUtils.isNotBlank(getChartOfAccountsCode()) && StringUtils.isNotBlank(getFinChartOfAccountDescription()))
            return getChartOfAccountsCode() + " - " + getFinChartOfAccountDescription();
        else 
            return "";
    }

    public String getCode() {
        return this.chartOfAccountsCode;
    }

    public String getName() {
        return this.finChartOfAccountDescription;
    }

    protected static ChartService getChartService() {
        if ( chartService == null ) {
            chartService = SpringContext.getBean(ChartService.class);
        }
        return chartService;
    }

    public void setCode(String chartOfAccountsCode) {
        setChartOfAccountsCode(chartOfAccountsCode);        
    }

    public void setName(String finChartOfAccountDescription) {
        setFinChartOfAccountDescription(finChartOfAccountDescription);
    }
    
    public String getChartCodeForReport() {
        return this.chartOfAccountsCode;
    }
}

