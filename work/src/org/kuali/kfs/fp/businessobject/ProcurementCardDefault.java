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
package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a procurement card holder, or the individual whose name is on the card.
 */

public class ProcurementCardDefault extends PersistableBusinessObjectBase implements MutableInactivatable {
    private String creditCardNumber;

    private String cardHolderName;
    private String cardHolderAlternateName;
    private String cardHolderLine1Address;
    private String cardHolderLine2Address;
    private String cardHolderCityName;
    private String cardHolderStateCode;
    private String cardHolderZipCode;
    private String cardHolderWorkPhoneNumber;
    private KualiDecimal cardLimit;
    private KualiDecimal cardCycleAmountLimit;
    private KualiDecimal cardCycleVolumeLimit;
    private String cardStatusCode;
    private String cardNoteText;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String projectCode;

    private boolean active;

    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private ObjectCode objectCode;
    private SubObjectCode subObjectCode;
    private ProjectCode project;

    /**
     * Default constructor.
     */
    public ProcurementCardDefault() {

    }

     /**
     * Gets the creditCardNumber attribute.
     *
     * @return Returns the creditCardNumber
     */
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    /**
     * Sets the creditCardNumber attribute.
     *
     * @param creditCardNumber The creditCardNumber to set.
     */
    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Gets the cardHolderName attribute.
     *
     * @return Returns the cardHolderName
     */
    public String getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Sets the cardHolderName attribute.
     *
     * @param cardHolderName The cardHolderName to set.
     */
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * Gets the cardHolderAlternateName attribute.
     *
     * @return Returns the cardHolderAlternateName
     */
    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    /**
     * Sets the cardHolderAlternateName attribute.
     *
     * @param cardHolderAlternateName The cardHolderAlternateName to set.
     */
    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }

    /**
     * Gets the cardHolderLine1Address attribute.
     *
     * @return Returns the cardHolderLine1Address
     */
    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    /**
     * Sets the cardHolderLine1Address attribute.
     *
     * @param cardHolderLine1Address The cardHolderLine1Address to set.
     */
    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }

    /**
     * Gets the cardHolderLine2Address attribute.
     *
     * @return Returns the cardHolderLine2Address
     */
    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    /**
     * Sets the cardHolderLine2Address attribute.
     *
     * @param cardHolderLine2Address The cardHolderLine2Address to set.
     */
    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }

    /**
     * Gets the cardHolderCityName attribute.
     *
     * @return Returns the cardHolderCityName
     */
    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    /**
     * Sets the cardHolderCityName attribute.
     *
     * @param cardHolderCityName The cardHolderCityName to set.
     */
    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }

    /**
     * Gets the cardHolderStateCode attribute.
     *
     * @return Returns the cardHolderStateCode
     */
    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    /**
     * Sets the cardHolderStateCode attribute.
     *
     * @param cardHolderStateCode The cardHolderStateCode to set.
     */
    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }

    /**
     * Gets the cardHolderZipCode attribute.
     *
     * @return Returns the cardHolderZipCode
     */
    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    /**
     * Sets the cardHolderZipCode attribute.
     *
     * @param cardHolderZipCode The cardHolderZipCode to set.
     */
    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }

    /**
     * Gets the cardHolderWorkPhoneNumber attribute.
     *
     * @return Returns the cardHolderWorkPhoneNumber
     */
    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    /**
     * Sets the cardHolderWorkPhoneNumber attribute.
     *
     * @param cardHolderWorkPhoneNumber The cardHolderWorkPhoneNumber to set.
     */
    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }

    /**
     * Gets the cardLimit attribute.
     *
     * @return Returns the cardLimit
     */
    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    /**
     * Sets the cardLimit attribute.
     *
     * @param cardLimit The cardLimit to set.
     */
    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }

    /**
     * Gets the cardCycleAmountLimit attribute.
     *
     * @return Returns the cardCycleAmountLimit
     */
    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    /**
     * Sets the cardCycleAmountLimit attribute.
     *
     * @param cardCycleAmountLimit The cardCycleAmountLimit to set.
     */
    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }

    /**
     * Gets the cardCycleVolumeLimit attribute.
     *
     * @return Returns the cardCycleVolumeLimit
     */
    public KualiDecimal getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    /**
     * Sets the cardCycleVolumeLimit attribute.
     *
     * @param cardCycleVolumeLimit The cardCycleVolumeLimit to set.
     */
    public void setCardCycleVolumeLimit(KualiDecimal cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }

    /**
     * Gets the cardStatusCode attribute.
     *
     * @return Returns the cardStatusCode
     */
    public String getCardStatusCode() {
        return cardStatusCode;
    }

    /**
     * Sets the cardStatusCode attribute.
     *
     * @param cardStatusCode The cardStatusCode to set.
     */
    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }

    /**
     * Gets the cardNoteText attribute.
     *
     * @return Returns the cardNoteText
     */
    public String getCardNoteText() {
        return cardNoteText;
    }

    /**
     * Sets the cardNoteText attribute.
     *
     * @param cardNoteText The cardNoteText to set.
     */
    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
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
     * Sets the chartOfAccountsCode attribute.
     *
     * @param chartOfAccountsCode The chartOfAccountsCode to set.
     */
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
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
     * Sets the accountNumber attribute.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber
     */
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
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
     * Sets the financialObjectCode attribute.
     *
     * @param financialObjectCode The financialObjectCode to set.
     */
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    /**
     * Gets the financialSubObjectCode attribute.
     *
     * @return Returns the financialSubObjectCode
     */
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    /**
     * Sets the financialSubObjectCode attribute.
     *
     * @param financialSubObjectCode The financialSubObjectCode to set.
     */
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    /**
     * @return the project code associated with this card holder
     */
    public String getProjectCode() {
        return projectCode;
    }

    /**
     * Sets the project code associated with this card holder
     * @param projectCode the project code associated with this card holder
     */
    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#isActive()
     * @return true if this procurement card holder detail is active, false otherwise
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.kns.bo.Inactivateable#setActive(boolean)
     * Sets whether this procurement card holder detail is active or not
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the account attribute.
     *
     * @return Returns the account
     */
    public Account getAccount() {
        return account;
    }

    /**
     * Sets the account attribute.
     *
     * @param account The account to set.
     * @deprecated
     */
    @Deprecated
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the chartOfAccounts attribute.
     *
     * @return Returns the chartOfAccounts
     */
    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     *
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    @Deprecated
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute.
     *
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    @Deprecated
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    /**
     * Gets the objectCode attribute.
     *
     * @return Returns the objectCode
     */
     public ObjectCode getObjectCode() {
        return objectCode;
    }

     /**
      * Sets the objectCode attribute.
      *
      * @param objectCode The objectCode to set.
      * @deprecated
      */
    @Deprecated
    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    /**
     * Gets the subObjectCode attribute.
     *
     * @return Returns the subObjectCode
     */
    public SubObjectCode getSubObjectCode() {
        return subObjectCode;
    }

    /**
     * Sets the subObjectCode attribute.
     *
     * @param subObjectCode The subObjectCode to set.
     * @deprecated
     */
    @Deprecated
    public void setSubObjectCode(SubObjectCode subObjectCode) {
        this.subObjectCode = subObjectCode;
    }

    /**
     * @return the ProjectCode associated with this procurement card holder detail
     */
    public ProjectCode getProject() {
        return project;
    }

    /**
     * Sets the ProjectCode associated with this procurement card holder detail.  Deprecated because, basically, this method is meant for OJB.
     * @param project the project code for OJB to set.
     * @deprecated
     */
    @Deprecated
    public void setProject(ProjectCode project) {
        this.project = project;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.CREDIT_CARD_NUMBER, this.creditCardNumber);
        return m;
    }


}
