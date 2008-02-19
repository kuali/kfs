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
package org.kuali.module.ar.rules;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.AddCustomerInvoiceDetailRule;

public class CustomerInvoiceDocumentRule extends AccountingDocumentRuleBase implements AddCustomerInvoiceDetailRule<AccountingDocument> {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentRule.class);

    private CustomerInvoiceDocument customerInvoiceDocument = null;

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean success = true;
        customerInvoiceDocument = (CustomerInvoiceDocument) approveEvent.getDocument();
        success &= defaultExistenceChecks(customerInvoiceDocument);
        success &= validateCustomerInvoiceDetails(customerInvoiceDocument);
        return success;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document doc) {
        boolean success = true;
        customerInvoiceDocument = (CustomerInvoiceDocument) doc;
        success &= defaultExistenceChecks(customerInvoiceDocument);
        success &= validateCustomerInvoiceDetails(customerInvoiceDocument);
        return success;
    }

    /**
     * @see org.kuali.core.rules.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document doc) {
        boolean success = true;
        customerInvoiceDocument = (CustomerInvoiceDocument) doc;
        success &= defaultExistenceChecks(customerInvoiceDocument);
        success &= validateCustomerInvoiceDetails(customerInvoiceDocument);
        return success;
    }


    /**
     * This method returns true if default existence checks for general customer invoice detail information is true
     * @param doc
     * @return
     */
    private boolean defaultExistenceChecks(CustomerInvoiceDocument doc) {
        boolean success = true;

        success &= isValidCustomerNumber(doc);
        success &= isValidBillingChartAndOrganization(doc);
        success &= isValidInvoiceDueDate(doc);
        return success;
    }

    protected boolean isValidCustomerNumber(CustomerInvoiceDocument doc) {
        boolean success = true;
        // TODO: probably have to do some kind of customer lookup here, if it isn't a reference in OJB
        return success;
    }

    /**
     * This method returns true if invoice due date is less than or equal to 90 days from today's date because invoice due date
     * cannot be more than 90 days from invoice creation date
     * 
     * @param doc
     * @return true if invoice due date is less than or equal to 90 days from today's date
     */
    protected boolean isValidInvoiceDueDate(CustomerInvoiceDocument doc) {
        boolean success = true;

        // if invoice due date is provided
        if (doc.getInvoiceDueDate() != null) {
            Timestamp invoiceDueDateTime = new Timestamp(doc.getInvoiceDueDate().getTime());
            Timestamp todayTime = new Timestamp(new Date().getTime());
            success = DateUtils.getDifferenceInDays(todayTime, invoiceDueDateTime) <= ArConstants.VALID_NUMBER_OF_DAYS_INVOICE_DUE_DATE_PAST_INVOICE_DATE;
        }

        return success;

    }

    protected boolean isValidBillingChartAndOrganization(CustomerInvoiceDocument customerInvoiceDocument) {
        boolean success = true;

        // billbychartofaccountcode is not empty and billedbyorgcode is not empty
        customerInvoiceDocument.refreshReferenceObject("billByChartOfAccount");
        if (customerInvoiceDocument.getBillByChartOfAccount() != null) {
            customerInvoiceDocument.refreshReferenceObject("billedByOrganization");
            if (customerInvoiceDocument.getBilledByOrganization() == null) {
                // fail with bad org
                success &= false;
            }
        }
        else {
            // fail as both chart and org have to be valid
            success &= false;
        }

        return success;
    }

    /**
     * This method returns true if all customer invoice details are valid
     * 
     * @param customerInvoiceDocument
     * @return
     */
    protected boolean validateCustomerInvoiceDetails(CustomerInvoiceDocument customerInvoiceDocument) {
        boolean success = true; 
        
        for( CustomerInvoiceDetail customerInvoiceDetail : customerInvoiceDocument.getCustomerInvoiceDetails() ){
            processAddCustomerInvoiceDetailBusinessRules( customerInvoiceDocument, customerInvoiceDetail );
        }
        
        return success;
    }

    /**
     * This method returns true if customer invoice detail (excluding accounting information) has no errors.
     * 
     * @return
     */
    protected boolean validateCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        boolean success = true;
        
        if (customerInvoiceDetail.getInvoiceItemQuantity() == null) {
            // rule failure
            
            success &= false;
        }
        else if (customerInvoiceDetail.getInvoiceItemQuantity().compareTo(new BigDecimal(1)) < 0) {
            // also fail
            success &= false;
        }

        if (customerInvoiceDetail.getInvoiceItemUnitPrice() == null) {
            // rule failure
            success &= false;
        }
        else if (customerInvoiceDetail.getInvoiceItemUnitPrice().compareTo(new KualiDecimal(1)) < 0) {
            // rule failure
            success &= false;
        }

        success &= isValidUnitOfMeasure(customerInvoiceDetail);
        return success;
    }


    /**
     * This method validates that the unit of measure for a customer invoice detail line is a valid UOM.
     * @param detail
     * @return
     */
    protected boolean isValidUnitOfMeasure(CustomerInvoiceDetail detail) {
        boolean success = true;
        // this most likely will be a parameter service call
        if (!StringUtils.isEmpty(detail.getInvoiceItemUnitOfMeasureCode())) {
            // validate through parameter service
        }
        return success;
    }

    /**
     * @see org.kuali.module.ar.rule.AddCustomerInvoiceDetailRule#processAddCustomerInvoiceBusinessRules(org.kuali.kfs.document.AccountingDocument, org.kuali.module.ar.bo.CustomerInvoiceDetail)
     */
    public boolean processAddCustomerInvoiceDetailBusinessRules(AccountingDocument financialDocument, CustomerInvoiceDetail customerInvoiceDetail) {
        
        //use existing accounting line rule validation to validate accounting information (in addition to doing data dictionary related validation).
        boolean isValid = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(ArConstants.NEW_CUSTOMER_INVOICE_DETAIL_ERROR_PATH_PREFIX, financialDocument, customerInvoiceDetail));
        
        //validate the rest of the customer invoice detail
        isValid &= validateCustomerInvoiceDetail(customerInvoiceDetail);

        return isValid;
    }
}
