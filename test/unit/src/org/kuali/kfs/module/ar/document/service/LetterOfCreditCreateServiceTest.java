/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the LetterOfCreditCreateService
 */
@ConfigureContext(session = khuntley)
public class LetterOfCreditCreateServiceTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {

        super.setUp();
    }

    @SuppressWarnings("null")
    public void testCreateCashControlDocuments() {
        // To set the input parameters.
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        LetterOfCreditCreateService leterOfCreditCreateService = SpringContext.getBean(LetterOfCreditCreateService.class);
        DocumentService documentService = SpringContext.getBean(DocumentService.class);


        ModuleConfiguration systemConfiguration = SpringContext.getBean(KualiModuleService.class).getModuleServiceByNamespaceCode("KFS-CG").getModuleConfiguration();

        String destinationFolderPath = ((FinancialSystemModuleConfiguration) systemConfiguration).getBatchFileDirectories().get(0);

        String runtimeStamp = dateTimeService.toDateTimeStringForFilename(new java.util.Date());
        String errOutputFile = destinationFolderPath + File.separator + "LetterOfCreditCreateServiceTest" + "_" + runtimeStamp + ArConstants.BatchFileSystem.EXTENSION;
        // To create error file and store all the errors in it.
        File errOutPutFile = new File(errOutputFile);
        PrintStream outputFileStream = null;

        try {
            outputFileStream = new PrintStream(errOutPutFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        KualiDecimal totalAmount = new KualiDecimal(1000);

        String documentNumber = leterOfCreditCreateService.createCashControlDocuments(null, null, null, totalAmount, outputFileStream);
        // To check if both cash control document and payment application document has been created.
        CashControlDocument cashcontrolDocument = null;

        try {
            cashcontrolDocument = (CashControlDocument) documentService.getByDocumentHeaderId(documentNumber);
        }
        catch (WorkflowException ex) {

            ex.printStackTrace();
        }

        assertNotNull(cashcontrolDocument);

        if (ObjectUtils.isNotNull(cashcontrolDocument)) {

            PaymentApplicationDocument paymentApplicationDocument = null;


            paymentApplicationDocument = cashcontrolDocument.getCashControlDetail(0).getReferenceFinancialDocument();

            assertNotNull(paymentApplicationDocument);

        }

    }

    /**
     * To test if there is a cash control document existing in saved / enroute/processed state.
     */
    public void testValidatecashControlDocument() {

        LetterOfCreditCreateService leterOfCreditCreateService = SpringContext.getBean(LetterOfCreditCreateService.class);

        String locCreationType = "TEST";
        String locValue = "1";

        File errOutPutFile = new File("testValidatecashControlDocument");
        PrintStream outputFileStream = null;

        try {
            outputFileStream = new PrintStream(errOutPutFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            CashControlDocument cashControlDoc = (CashControlDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashControlDocument.class);
            cashControlDoc.getDocumentHeader().setDocumentDescription("JUNIT TEST");
            AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
            accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
            accountsReceivableDocumentHeader.setProcessingChartOfAccountCode("UA");
            accountsReceivableDocumentHeader.setProcessingOrganizationCode("VPIT");
            cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
            cashControlDoc.setLocCreationType(locCreationType);
            cashControlDoc.setProposalNumber(new Long(locValue));
            cashControlDoc.setInvoiceDocumentType(ArConstants.CGIN_DOCUMENT_TYPE);

            CashControlDetail cashControlDetail = new CashControlDetail();
            cashControlDetail.setFinancialDocumentLineAmount(new KualiDecimal("100"));
            cashControlDetail.setReferenceFinancialDocumentNumber(cashControlDoc.getDocumentNumber());
            cashControlDoc.getCashControlDetails().add(cashControlDetail);
            SpringContext.getBean(CashControlDocumentService.class).addNewCashControlDetail("JUNIT TEST", cashControlDoc, cashControlDetail);
            cashControlDoc.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
            SpringContext.getBean(DocumentService.class).saveDocument(cashControlDoc);

            // Set cashcontrol document to Cancelled and check.


            assertFalse(leterOfCreditCreateService.validatecashControlDocument(null, locCreationType, locValue, outputFileStream));


        }
        catch (WorkflowException ex) {

            ex.printStackTrace();
        }
    }
}
