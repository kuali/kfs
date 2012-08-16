/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.fp.businessobject;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class CapitalAssetAccountsGroupDetails extends PersistableBusinessObjectBase {

    //primary key fields..
    private String documentNumber;
    private Integer capitalAssetLineNumber;
    private Integer capitalAssetAccountLineNumber;
    private Integer sequenceNumber;

    // accounting line info
    private String financialDocumentLineTypeCode;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String subAccountNumber;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceId;
    private KualiDecimal amount;

    private CapitalAssetInformation capitalAssetInformation;

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */

    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Gets the capitalAssetAccountLineNumber attribute.
     *
     * @return Returns the capitalAssetAccountLineNumber
     */

    public Integer getCapitalAssetAccountLineNumber() {
        return capitalAssetAccountLineNumber;
    }

    /**
     * Gets the financialDocumentLineTypeCode attribute.
     *
     * @return Returns the financialDocumentLineTypeCode
     */

    public String getFinancialDocumentLineTypeCode() {
        return financialDocumentLineTypeCode;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     *
     * @return Returns the chartOfAccountsCode
     */

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber
     */

    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode
     */

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    /**
     * Gets the capitalAssetInformation attribute.
     *
     * @return Returns the capitalAssetInformation.
     */
    public CapitalAssetInformation getCapitalAssetInformation() {
        return capitalAssetInformation;
    }

    /**
     * Sets the capitalAssetInformation attribute value.
     *
     * @param capitalAssetInformation The capitalAssetInformation to set.
     */
    public void setCapitalAssetInformation(CapitalAssetInformation capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }


    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Sets the capitalAssetAccountLineNumber attribute.
     *
     * @param capitalAssetAccountLineNumber The capitalAssetAccountLineNumber to set.
     */
    public void setCapitalAssetAccountLineNumber(Integer capitalAssetAccountLineNumber) {
        this.capitalAssetAccountLineNumber = capitalAssetAccountLineNumber;
    }

    /**
     * Sets the financialDocumentLineTypeCode attribute.
     *
     * @param financialDocumentLineTypeCode The financialDocumentLineTypeCode to set.
     */
    public void setFinancialDocumentLineTypeCode(String financialDocumentLineTypeCode) {
        this.financialDocumentLineTypeCode = financialDocumentLineTypeCode;
    }

    /**
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    /**
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Sets the financialObjectCode attribute.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Constructs a CapitalAssetInformation.java.
     */
    public CapitalAssetAccountsGroupDetails() {
        super();
        setAmount(KualiDecimal.ZERO);
    }

    /**
     * Gets the sequenceNumber attribute.
     *
     * @return Returns the sequenceNumber
     */

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * Sets the sequenceNumber attribute.
     *
     * @param sequenceNumber The sequenceNumber to set.
     */
    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put(KFSPropertyConstants.CAPITAL_ASSET_LINE_NUMBER, this.getCapitalAssetLineNumber());
        m.put(KFSPropertyConstants.CAPITAL_ASSET_ACCOUNT_LINE_NUMBER, this.getCapitalAssetAccountLineNumber());
        m.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());

        return m;
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
        simpleValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        simpleValues.put(KFSPropertyConstants.AMOUNT, this.getAmount());
        simpleValues.put(KFSPropertyConstants.SEQUENCE_NUMBER, this.getSequenceNumber());

        return simpleValues;
    }

    /**
     * Gets the capitalAssetLineNumber attribute.
     * @return Returns the capitalAssetLineNumber.
     */
    public Integer getCapitalAssetLineNumber() {
        return capitalAssetLineNumber;
    }

    /**
     * Sets the capitalAssetLineNumber attribute value.
     * @param capitalAssetLineNumber The capitalAssetLineNumber to set.
     */
    public void setCapitalAssetLineNumber(Integer capitalAssetLineNumber) {
        this.capitalAssetLineNumber = capitalAssetLineNumber;
    }

    /**
     * @return Returns the amount.
     */
    public KualiDecimal getAmount() {
        if (ObjectUtils.isNull(amount)) {
            return KualiDecimal.ZERO;
        }
        else {
            return amount;
        }
    }

    /**
     * @param amount The amount to set.
     */
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
        
}
