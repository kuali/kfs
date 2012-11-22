/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.tem.TemConstants.DisbursementVoucherPaymentMethods;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_TRVL_ADV_T")
public class TravelAdvance extends PersistableBusinessObjectBase {

    private Integer id;
    private String documentNumber;
    private Integer financialDocumentLineNumber;

    private KualiDecimal travelAdvanceRequested;
    private KualiDecimal amountDue;

    private String arCustomerId;
    private String arInvoiceDocNumber;
    private Date dueDate;
    private String paymentMethod = DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE;
    private Date taxRamificationNotificationDate;

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String advancePaymentReasonCode;

    private Boolean travelAdvancePolicy = Boolean.FALSE;

    private String additionalJustification;

    private Account acct;
    private SubAccount subAcct;
    private ObjectCode objCode;
    private SubObjectCode subObjCode;
    private AdvancePaymentReason advancePaymentReason;


    /**
     * This method returns the generated Id of this TravelAdvance
     *
     * @return generated id of this Travel Advance
     */
    @Id
    @GeneratedValue(generator = "TEM_TRVL_ADV_ID_SEQ")
    @SequenceGenerator(name = "TEM_TRVL_ADV_ID_SEQ", sequenceName = "TEM_TRVL_ADV_ID_SEQ", allocationSize = 5)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * This method sets the generated Id of this Travel Advance
     *
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Column(name = "FDOC_NBR")
    public String getDocumentNumber() {
        return documentNumber;
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
     * Gets the financialDocumentLineNumber attribute.
     *
     * @return Returns the financialDocumentLineNumber
     */
    @Column(name = "FDOC_LINE_NBR")
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }

    /**
     * Sets the financialDocumentLineNumber attribute.
     *
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }

    /**
     * This method returns the travel advance request
     *
     * @return travel advance requested amount
     */
    @Column(name = "TVL_ADV_REQ", precision = 19, scale = 2)
    public KualiDecimal getTravelAdvanceRequested() {
        return travelAdvanceRequested;
    }

    /**
     * This method sets the amount of advance being requested
     *
     * @param travelAdvanceRequested
     */
    public void setTravelAdvanceRequested(KualiDecimal travelAdvanceRequested) {
        this.travelAdvanceRequested = travelAdvanceRequested;
    }


    /**
     * Gets the amountDue attribute.
     * @return Returns the amountDue.
     */
    public KualiDecimal getAmountDue() {
        if (arInvoiceDocNumber != null){
            amountDue = getTravelDocumentService().getAmountDueFromInvoice(arInvoiceDocNumber,this.getTravelAdvanceRequested());
        }
        else{
            amountDue = this.getTravelAdvanceRequested();
        }
        return amountDue;
    }

    public void setAmountDue(KualiDecimal amountDue) {
        this.amountDue = amountDue;
    }

    /**
     * This method returns the Accounts Receivable Customer ID associated with this traveler
     *
     * @return customer ID
     */
    @Column(name = "AR_CUST_ID")
    public String getArCustomerId() {
        return arCustomerId;
    }

    /**
     * This method sets the Accounts Receivable Customer ID associated with this traveler
     *
     * @param arCustomerId
     */
    public void setArCustomerId(String arCustomerId) {
        this.arCustomerId = arCustomerId;
    }


    /**
     * Gets the arInvoiceDocNumber attribute.
     *
     * @return Returns the arInvoiceDocNumber.
     */
    @Column(name = "AR_INV_DOC_NBR")
    public String getArInvoiceDocNumber() {
        return arInvoiceDocNumber;
    }

    /**
     * Sets the arInvoiceDocNumber attribute value.
     *
     * @param arInvoiceDocNumber The arInvoiceDocNumber to set.
     */
    public void setArInvoiceDocNumber(String arInvoiceDocNumber) {
        this.arInvoiceDocNumber = arInvoiceDocNumber;
    }

    /**
     * Gets the dueDate attribute.
     *
     * @return Returns the dueDate.
     */
    @Column(name = "DUE_DT")
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the dueDate attribute value.
     *
     * @param dueDate The dueDate to set.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the paymentMethod attribute.
     *
     * @return Returns the paymentMethod.
     */
    @Column(name = "PYMT_MTHD")
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Sets the paymentMethod attribute value.
     *
     * @param paymentMethod The paymentMethod to set.
     */
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    /**
     * Gets the accountNumber attribute.
     *
     * @return Returns the accountNumber.
     */
    @Column(name = "ACCOUNT_NBR")
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute value.
     *
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the subAccountNumber attribute.
     *
     * @return Returns the subAccountNumber.
     */
    @Column(name = "SUB_ACCT_NBR")
    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    /**
     * Sets the subAccountNumber attribute value.
     *
     * @param subAccountNumber The subAccountNumber to set.
     */
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * Gets the financialObjectCode attribute.
     *
     * @return Returns the financialObjectCode.
     */
    @Column(name = "FIN_OBJECT_CD")
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
     * Gets the financialSubObjectCode attribute.
     *
     * @return Returns the financialSubObjectCode.
     */
    @Column(name = "FIN_SUB_OBJ_CD")
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
     * Gets the acct attribute.
     *
     * @return Returns the acct.
     */
    @Transient
    public Account getAcct() {
        return acct;
    }

    /**
     * Sets the acct attribute value.
     *
     * @param acct The acct to set.
     */
    public void setAcct(Account acct) {
        this.acct = acct;
    }

    /**
     * Gets the subAcct attribute.
     *
     * @return Returns the subAcct.
     */
    @Transient
    public SubAccount getSubAcct() {
        return subAcct;
    }

    /**
     * Sets the subAcct attribute value.
     *
     * @param subAcct The subAcct to set.
     */
    public void setSubAcct(SubAccount subAcct) {
        this.subAcct = subAcct;
    }

    /**
     * Gets the objCode attribute.
     *
     * @return Returns the objCode.
     */
    @Transient
    public ObjectCode getObjCode() {
        return objCode;
    }

    /**
     * Sets the objCode attribute value.
     *
     * @param objCode The objCode to set.
     */
    public void setObjCode(ObjectCode objCode) {
        this.objCode = objCode;
    }

    /**
     * Gets the subObjCode attribute.
     *
     * @return Returns the subObjCode.
     */
    @Transient
    public SubObjectCode getSubObjCode() {
        return subObjCode;
    }

    /**
     * Sets the subObjCode attribute value.
     *
     * @param subObjCode The subObjCode to set.
     */
    public void setSubObjCode(SubObjectCode subObjCode) {
        this.subObjCode = subObjCode;
    }

    /**
     * Gets the advancePaymentReasonCode attribute.
     * @return Returns the advancePaymentReasonCode.
     */
    public String getAdvancePaymentReasonCode() {
        return advancePaymentReasonCode;
    }

    /**
     * Sets the advancePaymentReasonCode attribute value.
     * @param advancePaymentReasonCode The advancePaymentReasonCode to set.
     */
    public void setAdvancePaymentReasonCode(String advancePaymentReasonCode) {
        this.advancePaymentReasonCode = advancePaymentReasonCode;
    }

    /**
     * Gets the advancePaymentReason attribute.
     * @return Returns the advancePaymentReason.
     */
    public AdvancePaymentReason getAdvancePaymentReason() {
        return advancePaymentReason;
    }

    /**
     * Sets the advancePaymentReason attribute value.
     * @param advancePaymentReason The advancePaymentReason to set.
     */
    public void setAdvancePaymentReason(AdvancePaymentReason advancePaymentReason) {
        this.advancePaymentReason = advancePaymentReason;
    }

    /**
     * Gets the travelAdvancePolicy attribute.
     * @return Returns the travelAdvancePolicy.
     */
    public boolean getTravelAdvancePolicy() {
        return travelAdvancePolicy;
    }

    /**
     * Sets the travelAdvancePolicy attribute value.
     * @param travelAdvancePolicy The travelAdvancePolicy to set.
     */
    public void setTravelAdvancePolicy(boolean travelAdvancePolicy) {
        this.travelAdvancePolicy = travelAdvancePolicy;
    }

    /**
     * Gets the additionalJustification attribute.
     * @return Returns the additionalJustification.
     */
    public String getAdditionalJustification() {
        return additionalJustification;
    }

    /**
     * Sets the additionalJustification attribute value.
     * @param additionalJustification The additionalJustification to set.
     */
    public void setAdditionalJustification(String additionalJustification) {
        this.additionalJustification = additionalJustification;
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the taxRamificationNotificationDate attribute.
     * @return Returns the taxRamificationNotificationDate.
     */
    @Column(name = "TAX_RAM_NTF_DT")
    public Date getTaxRamificationNotificationDate() {
        return taxRamificationNotificationDate;
    }

    /**
     * Sets the taxRamificationNotificationDate attribute value.
     * @param taxRamificationNotificationDate The taxRamificationNotificationDate to set.
     */
    public void setTaxRamificationNotificationDate(Date taxRamificationNotificationDate) {
        this.taxRamificationNotificationDate = taxRamificationNotificationDate;
    }

    /**
     * Gets the chartOfAccountsCode attribute.
     * @return Returns the chartOfAccountsCode.
     */
    @Column(name = "FIN_COA_CD")
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

}
