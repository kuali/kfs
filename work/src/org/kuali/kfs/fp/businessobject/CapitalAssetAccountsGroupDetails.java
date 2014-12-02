/*

 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class CapitalAssetAccountsGroupDetails extends PersistableBusinessObjectBase {

    //primary key fields..
    protected String documentNumber;
    protected Integer capitalAssetLineNumber;
    protected Integer capitalAssetAccountLineNumber;
    protected Integer sequenceNumber;

    // accounting line info
    protected String financialDocumentLineTypeCode;
    protected String chartOfAccountsCode;
    protected String accountNumber;
    protected String financialObjectCode;
    protected String subAccountNumber;
    protected String financialSubObjectCode;
    protected String projectCode;
    protected String organizationReferenceId;
    protected KualiDecimal amount;

    protected Chart chart;
    protected Account account;

    protected CapitalAssetInformation capitalAssetInformation;

    public CapitalAssetAccountsGroupDetails() {
                super();
                setAmount(KualiDecimal.ZERO);
    }

    public CapitalAssetAccountsGroupDetails(CapitalAssetAccountsGroupDetails other){
        this();
                this.documentNumber = other.documentNumber;
                this.capitalAssetLineNumber = other.capitalAssetLineNumber;
                this.capitalAssetAccountLineNumber = other.capitalAssetAccountLineNumber;
                this.sequenceNumber = other.sequenceNumber;
                this.financialDocumentLineTypeCode= other.financialDocumentLineTypeCode;
                this.chartOfAccountsCode= other.chartOfAccountsCode;
                this.accountNumber = other.accountNumber;
                this.financialObjectCode = other.financialObjectCode;
                this.subAccountNumber = other.subAccountNumber;
                this.financialSubObjectCode = other.financialSubObjectCode;
                this.projectCode = other.projectCode;
                this.organizationReferenceId = other.organizationReferenceId;
                this.amount = other.amount;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }



    public Integer getCapitalAssetAccountLineNumber() {
        return capitalAssetAccountLineNumber;
    }



    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }



    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }



    public String getAccountNumber() {
        return accountNumber;
    }



    public String getFinancialObjectCode() {
        return financialObjectCode;
    }


    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }


    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }



    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public void setCapitalAssetAccountLineNumber(Integer capitalAssetAccountLineNumber) {
        this.capitalAssetAccountLineNumber = capitalAssetAccountLineNumber;
    }


    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }


    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }


    public Integer getSequenceNumber() {
        return sequenceNumber;
    }


    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     *
     * @return Map a map with the primitive field names as the key and the primitive values as the map value.
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();

        simpleValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        simpleValues.put(KFSPropertyConstants.CAPITAL_ASSET_ACCOUNT_LINE_NUMBER, this.getCapitalAssetAccountLineNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_DOCUMENT_LINE_TYPE_CODE, this.getFinancialDocumentLineTypeCode());
        simpleValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        simpleValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        simpleValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.getSubAccountNumber());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        simpleValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, this.getFinancialSubObjectCode());
        simpleValues.put(KFSPropertyConstants.PROJECT_CODE, this.getProjectCode());
        simpleValues.put(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, this.getOrganizationReferenceId());
        simpleValues.put(KFSPropertyConstants.AMOUNT, this.getAmount());
        simpleValues.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());

        return simpleValues;
    }


    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }


    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
    }


    public KualiDecimal getAmount() {
        if (ObjectUtils.isNull(amount)) {
            return KualiDecimal.ZERO;
        }
        else {
            return amount;
        }
    }


    public void setAmount(KualiDecimal amount) {
        if (ObjectUtils.isNull(amount)) {
            this.amount = KualiDecimal.ZERO;
        }
        else {
            this.amount = amount;
        }

        this.amount = amount;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() {
        return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
        this.organizationReferenceId = organizationReferenceId;
    }

    /**
     * @return the chart
     */
    public Chart getChart() {
        return chart;
    }

    /**
     * @param chart the chart to set
     */
    public void setChart(Chart chart) {
        this.chart = chart;
    }

    /**
     * @return the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * @param account the account to set
     */
    public void setAccount(Account account) {
        this.account = account;
    }
}

