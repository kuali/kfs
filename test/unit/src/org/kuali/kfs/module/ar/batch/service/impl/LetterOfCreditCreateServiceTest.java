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
package org.kuali.kfs.module.ar.batch.service.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.io.PrintWriter;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.LetterOfCreditCreateService;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ModuleConfiguration;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
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
    public void testCreateCashControlDocuments() throws Exception {
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
        PrintWriter outputFileStream = new PrintWriter(errOutPutFile);

        KualiDecimal totalAmount = new KualiDecimal(1000);

        String documentNumber = leterOfCreditCreateService.createCashControlDocuments(null, null, null, totalAmount, outputFileStream);
        // To check if both cash control document and payment application document has been created.
        CashControlDocument cashcontrolDocument = null;
        cashcontrolDocument = (CashControlDocument) documentService.getByDocumentHeaderId(documentNumber);

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
    public void testValidatecashControlDocument() throws Exception {

        LetterOfCreditCreateService leterOfCreditCreateService = SpringContext.getBean(LetterOfCreditCreateService.class);

        String locCreationType = "TEST";
        String locValue = "1";

        File errOutPutFile = new File("testValidatecashControlDocument");
        PrintWriter outputFileStream = new PrintWriter(errOutPutFile);

        CashControlDocument cashControlDoc = (CashControlDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashControlDocument.class);
        cashControlDoc.getDocumentHeader().setDocumentDescription("JUNIT TEST");
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = new AccountsReceivableDocumentHeader();
        accountsReceivableDocumentHeader.setDocumentNumber(cashControlDoc.getDocumentNumber());
        accountsReceivableDocumentHeader.setProcessingChartOfAccountCode("UA");
        accountsReceivableDocumentHeader.setProcessingOrganizationCode("VPIT");
        cashControlDoc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
        cashControlDoc.setLetterOfCreditCreationType(locCreationType);
        cashControlDoc.setProposalNumber(new Long(locValue));
        cashControlDoc.setInvoiceDocumentType(ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);

        CashControlDetail cashControlDetail = new CashControlDetail();
        cashControlDetail.setFinancialDocumentLineAmount(new KualiDecimal("100"));
        cashControlDetail.setReferenceFinancialDocumentNumber(cashControlDoc.getDocumentNumber());
        cashControlDoc.getCashControlDetails().add(cashControlDetail);
        SpringContext.getBean(CashControlDocumentService.class).addNewCashControlDetail("JUNIT TEST", cashControlDoc, cashControlDetail);
        cashControlDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
        SpringContext.getBean(DocumentService.class).saveDocument(cashControlDoc);
        assertFalse(leterOfCreditCreateService.validateCashControlDocument(null, locCreationType, locValue, outputFileStream));
    }
}
