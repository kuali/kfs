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
package org.kuali.module.chart.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class Chart extends BusinessObjectBase {

    private static final long serialVersionUID = 4129020803214027609L;

    private String finChartOfAccountDescription;
    private boolean finChartOfAccountActiveIndicator;
    private String finCoaManagerUniversalId;
    private String reportsToChartOfAccountsCode;
    private String chartOfAccountsCode;
    private String finAccountsPayableObjectCode;
    private String finExternalEncumbranceObjCd;
    private String finPreEncumbranceObjectCode;
    private String financialCashObjectCode;
    private String icrIncomeFinancialObjectCode;
    private String finAccountsReceivableObjCode;
    private String finInternalEncumbranceObjCd;
    private String icrExpenseFinancialObjectCd;
    private String incBdgtEliminationsFinObjCd;
    private String expBdgtEliminationsFinObjCd;


    private ObjectCode incBdgtEliminationsFinObj;
    private ObjectCode expBdgtEliminationsFinObj;
    private ObjectCode finAccountsPayableObject;
    private ObjectCode finExternalEncumbranceObj;
    private ObjectCode finPreEncumbranceObject;
    private ObjectCode financialCashObject;
    private ObjectCode icrIncomeFinancialObject;
    private ObjectCode finAccountsReceivableObj;
    private ObjectCode finInternalEncumbranceObj;
    private ObjectCode icrExpenseFinancialObject;
    private UniversalUser finCoaManagerUniversal;
    private Chart reportsToChartOfAccounts;


    /**
     * Default no-arg constructor.
     */
    public Chart() {
    }

    /**
     * Gets the finChartOfAccountDescription attribute.
     * 
     * @return - Returns the finChartOfAccountDescription
     * 
     */
    public String getFinChartOfAccountDescription() {
        return finChartOfAccountDescription;
    }

    /**
     * Sets the finChartOfAccountDescription attribute.
     * 
     * @param finChartOfAccountDescription The finChartOfAccountDescription to set.
     * 
     */
    public void setFinChartOfAccountDescription(String finChartOfAccountDescription) {
        this.finChartOfAccountDescription = finChartOfAccountDescription;
    }

    /**
     * Gets the finChartOfAccountActiveIndicator attribute.
     * 
     * @return - Returns the finChartOfAccountActiveIndicator
     * 
     */
    public boolean isFinChartOfAccountActiveIndicator() {
        return finChartOfAccountActiveIndicator;
    }

    /**
     * Sets the finChartOfAccountActiveIndicator attribute.
     * 
     * @param finChartOfAccountActiveIndicator The finChartOfAccountActiveIndicator to set.
     * 
     */
    public void setFinChartOfAccountActiveIndicator(boolean finChartOfAccountActiveIndicator) {
        this.finChartOfAccountActiveIndicator = finChartOfAccountActiveIndicator;
    }


    /**
     * Gets the finAccountsPayableObject attribute.
     * 
     * @return - Returns the finAccountsPayableObject
     * 
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
     * @return - Returns the finPreEncumbranceObject
     * 
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
     * @return - Returns the financialCashObject
     * 
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
     * @return - Returns the icrIncomeFinancialObject
     * 
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
     * @return - Returns the finAccountsReceivableObj
     * 
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

    /**
     * Gets the finCoaManagerUniversal attribute.
     * 
     * @return - Returns the finCoaManagerUniversal
     * 
     */
    public UniversalUser getFinCoaManagerUniversal() {
        return finCoaManagerUniversal;
    }

    /**
     * Sets the finCoaManagerUniversal attribute.
     * 
     * @param finCoaManagerUniversal The finCoaManagerUniversal to set.
     * @deprecated
     */
    public void setFinCoaManagerUniversal(UniversalUser finCoaManagerUniversal) {
        this.finCoaManagerUniversal = finCoaManagerUniversal;
    }

    /**
     * Gets the reportsToChartOfAccounts attribute.
     * 
     * @return - Returns the reportsToChartOfAccounts
     * 
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
     * 
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
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
     * @return - Returns the expBdgtEliminationsFinObjCd
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
     * @return - Returns the incBdgtEliminationsFinObjCd
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
     * @return Returns the finCoaManagerUniversalId.
     */
    public String getFinCoaManagerUniversalId() {
        return finCoaManagerUniversalId;
    }

    /**
     * @param finCoaManagerUniversalId The finCoaManagerUniversalId to set.
     */
    public void setFinCoaManagerUniversalId(String finCoaManagerUniversalId) {
        this.finCoaManagerUniversalId = finCoaManagerUniversalId;
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
}