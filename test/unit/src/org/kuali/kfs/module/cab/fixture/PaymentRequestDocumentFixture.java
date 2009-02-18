/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.fixture;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.KualiDecimal;

public enum PaymentRequestDocumentFixture {

    REC1 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(311);
            obj.setDocumentNumber("31");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(21);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ1.newRecord());
            return obj;
        };
    },
    REC2 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(321);
            obj.setDocumentNumber("32");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(21);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ2.newRecord());
            return obj;
        };
    },
    REC3 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(331);
            obj.setDocumentNumber("33");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(22);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ3.newRecord());
            return obj;
        };
    },
    REC4 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(341);
            obj.setDocumentNumber("34");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(22);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ4.newRecord());
            return obj;
        };
    },
    REC5 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(351);
            obj.setDocumentNumber("35");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(23);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1001");
            obj.setVendorInvoiceAmount(new KualiDecimal(19000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ5.newRecord());
            return obj;
        };
    },
    REC6 {
        private DateTimeService dateTimeService;
        @Override
        public PaymentRequestDocument newRecord() {
            PaymentRequestDocument obj = new PaymentRequestDocument();
            obj.setPurapDocumentIdentifier(361);
            obj.setDocumentNumber("36");
            obj.setStatusCode("DPTA");
            obj.setPurchaseOrderIdentifier(23);
            obj.setPostingYear(2009);
            obj.setInvoiceDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setInvoiceNumber("1003");
            obj.setVendorInvoiceAmount(new KualiDecimal(14000));
            obj.setVendorPaymentTermsCode("00N30");
            obj.setVendorShippingPaymentTermsCode("AL");
            obj.setPaymentRequestPayDate(new java.sql.Date(dateTimeService.getCurrentDate().getTime()));
            obj.setPaymentRequestCostSourceCode("EST");
            obj.setPaymentRequestedCancelIndicator(false);
            obj.setPaymentAttachmentIndicator(false);
            obj.setImmediatePaymentIndicator(false);
            obj.setHoldIndicator(false);
            obj.setPaymentRequestElectronicInvoiceIndicator(false);
            obj.setVendorHeaderGeneratedIdentifier(2013);
            obj.setVendorDetailAssignedIdentifier(0);
            obj.setVendorName("BESCO WATER TREATMENT INC");
            obj.setVendorLine1Address("PO BOX 1309");
            obj.setVendorCityName("BATTLE CREEK");
            obj.setVendorStateCode("MI");
            obj.setVendorPostalCode("49016-1309");
            obj.setVendorCountryCode("US");
            obj.setAccountsPayableProcessorIdentifier("2133704704");
            obj.setLastActionPerformedByPersonId("2133704704");
            obj.setProcessingCampusCode("IN");
            obj.setAccountsPayableApprovalTimestamp(new java.sql.Timestamp(dateTimeService.getCurrentDate().getTime()));
            obj.setOriginalVendorHeaderGeneratedIdentifier(2013);
            obj.setOriginalVendorDetailAssignedIdentifier(0);
            obj.setContinuationAccountIndicator(false);
            obj.setAccountsPayablePurchasingDocumentLinkIdentifier(21);
            obj.setClosePurchaseOrderIndicator(false);
            obj.setReopenPurchaseOrderIndicator(false);
            obj.setReceivingDocumentRequiredIndicator(false);
            obj.setPaymentRequestPositiveApprovalIndicator(false);
            obj.setUseTaxIndicator(true);
            obj.setDocumentHeader(FinancialSystemDocumentHeaderFixture.PREQ6.newRecord());
            return obj;
        };
    };
    public abstract PaymentRequestDocument newRecord();

    public static void setUpData() {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(getAll());

    }

    private static List<PersistableBusinessObjectBase> getAll() {
        List<PersistableBusinessObjectBase> recs = new ArrayList<PersistableBusinessObjectBase>();
        recs.add(REC1.newRecord());
        recs.add(REC2.newRecord());
        recs.add(REC3.newRecord());
        recs.add(REC4.newRecord());
        recs.add(REC5.newRecord());
        recs.add(REC6.newRecord());
        return recs;
    }
}
