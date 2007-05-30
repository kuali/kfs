/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.module.purap.document;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.CreditMemoItem;
import org.kuali.module.purap.bo.CreditMemoStatusHistory;


/**
 * Credit Memo Document
 */
public class CreditMemoDocument extends AccountsPayableDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoDocument.class);

    private Integer paymentRequestIdentifier;
    private String creditMemoNumber;
    private Date creditMemoDate;
    private KualiDecimal creditMemoAmount;
    private Timestamp creditMemoPaidTimestamp;
    private String itemMiscellaneousCreditDescription;
    
    private PaymentRequestDocument paymentRequest;

    /**
	 * Default constructor.
	 */
	public CreditMemoDocument() {
        super();
    }
    
    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#isBoNotesSupport()
     */
    @Override
    public boolean isBoNotesSupport() {
        return true;
    }

    public void refreshAllReferences() {
        super.refreshAllReferences();
        this.refreshReferenceObject("paymentRequest");
    }
    
    /**
     * Perform logic needed to initiate CM Document
     */
    public void initiateDocument() {
        LOG.debug("initiateDocument() started");
        this.setStatusCode( PurapConstants.CreditMemoStatuses.INITIATE );
  
        //TODO: Change this one:
        this.setAccountsPayableProcessorIdentifier("TBD");
        UniversalUser currentUser = (UniversalUser)GlobalVariables.getUserSession().getUniversalUser();
        this.setAccountsPayableProcessorIdentifier(currentUser.getPersonUniversalIdentifier());
        // paymentRequest.setProcessedCampusCode(u.getCampusCd());
        //paymentRequest.setAccountsPayableProcessorId(u.getId());
        //this.setStatusCode( PurapConstants.PaymentRequestStatuses.IN_PROCESS )
       // this.setInitialized(true);
   //     this.refreshAllReferences();
    }

    /**
     * @see org.kuali.core.document.DocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        LOG.debug("handleRouteStatusChange() started");
        super.handleRouteStatusChange();

    }

    @Override
    public void handleRouteLevelChange() {
        LOG.debug("handleRouteLevelChange() started");
        super.handleRouteLevelChange();
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocument#addToStatusHistories(java.lang.String, java.lang.String)
     */
    public void addToStatusHistories( String oldStatus, String newStatus, Note statusHistoryNote ) {
        CreditMemoStatusHistory cmsh = new CreditMemoStatusHistory( oldStatus, newStatus );
        this.addStatusHistoryNote( cmsh, statusHistoryNote );
        this.getStatusHistories().add( cmsh );
    }

    /**
     * 
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getItemClass()
     */
    @Override
    public Class getItemClass() {
        return CreditMemoItem.class;
    }
    
    /**
     * @see org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase#getSourceAccountingLineClass()
     */
//    @Override
//    public Class getSourceAccountingLineClass() {
//        return CreditMemoAccount.class;
//    }


    /**
     * Gets the paymentRequestIdentifier attribute.
     * 
     * @return Returns the paymentRequestIdentifier
     * 
     */
    public Integer getPaymentRequestIdentifier() { 
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute.
     * 
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     * 
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }


    /**
     * Gets the creditMemoNumber attribute.
     * 
     * @return Returns the creditMemoNumber
     * 
     */
    public String getCreditMemoNumber() { 
        return creditMemoNumber;
    }

    /**
     * Sets the creditMemoNumber attribute.
     * 
     * @param creditMemoNumber The creditMemoNumber to set.
     * 
     */
    public void setCreditMemoNumber(String creditMemoNumber) {
        this.creditMemoNumber = creditMemoNumber;
    }


    /**
     * Gets the creditMemoDate attribute.
     * 
     * @return Returns the creditMemoDate
     * 
     */
    public Date getCreditMemoDate() { 
        return creditMemoDate;
    }

    /**
     * Sets the creditMemoDate attribute.
     * 
     * @param creditMemoDate The creditMemoDate to set.
     * 
     */
    public void setCreditMemoDate(Date creditMemoDate) {
        this.creditMemoDate = creditMemoDate;
    }

    /**
     * Gets the creditMemoAmount attribute.
     * 
     * @return Returns the creditMemoAmount
     * 
     */
    public KualiDecimal getCreditMemoAmount() { 
        return creditMemoAmount;
    }

    /**
     * Sets the creditMemoAmount attribute.
     * 
     * @param creditMemoAmount The creditMemoAmount to set.
     * 
     */
    public void setCreditMemoAmount(KualiDecimal creditMemoAmount) {
        this.creditMemoAmount = creditMemoAmount;
    }

    /**
     * Gets the itemMiscellaneousCreditDescription attribute.
     * 
     * @return Returns the itemMiscellaneousCreditDescription
     * 
     */
    public String getItemMiscellaneousCreditDescription() { 
        return itemMiscellaneousCreditDescription;
    }

    /**
     * Sets the itemMiscellaneousCreditDescription attribute.
     * 
     * @param itemMiscellaneousCreditDescription The itemMiscellaneousCreditDescription to set.
     * 
     */
    public void setItemMiscellaneousCreditDescription(String itemMiscellaneousCreditDescription) {
        this.itemMiscellaneousCreditDescription = itemMiscellaneousCreditDescription;
    }


    /**
     * Gets the creditMemoPaidTimestamp attribute. 
     * @return Returns the creditMemoPaidTimestamp.
     */
    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    /**
     * Sets the creditMemoPaidTimestamp attribute value.
     * @param creditMemoPaidTimestamp The creditMemoPaidTimestamp to set.
     */
    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }


    /**
     * Gets the paymentRequest attribute.
     * 
     * @return Returns the paymentRequest
     * 
     */
    public PaymentRequestDocument getPaymentRequest() { 
        return paymentRequest;
    }


}
