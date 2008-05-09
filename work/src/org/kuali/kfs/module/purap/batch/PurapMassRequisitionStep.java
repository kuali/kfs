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
package org.kuali.module.purap.batch;

import java.math.BigDecimal;
import java.util.Date;

import org.kuali.core.UserSession;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.batch.AbstractStep;
import org.kuali.module.purap.PurapConstants.POCostSources;
import org.kuali.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.module.purap.PurapConstants.RequisitionSources;
import org.kuali.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.document.RequisitionDocument;

import edu.iu.uis.eden.exception.WorkflowException;

public class PurapMassRequisitionStep extends AbstractStep {
    private DocumentService documentService;
    private final int NUM_DOCS_TO_CREATE = 25; // number of each document type to create

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {
            // create document
            RequisitionDocument reqDoc = populateQuantityDocument();
            
            // route it
            try {
                GlobalVariables.setUserSession(new UserSession("KHUNTLEY"));
                documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }
            catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            Thread.sleep(5000);
        }

        for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {
            // create document
            RequisitionDocument reqDoc = populateNonQuantityDocument();
            // route it
            try {
                GlobalVariables.setUserSession(new UserSession("KHUNTLEY"));
                documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }
            catch (UserNotFoundException e) {
                e.printStackTrace();
            }
            Thread.sleep(5000);
        }

        Thread.sleep(60000);
        return true;
    }

    private RequisitionDocument populateQuantityDocument() {
        RequisitionDocument reqDoc = null;
        try {
            reqDoc = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);

            // RequisitionDocument reqDoc = new RequisitionDocument();
            // set doc attributes
            reqDoc.getDocumentHeader().setExplanation("batch created quantity document");
            DocumentHeader documentHeader = reqDoc.getDocumentHeader();
            documentHeader.setFinancialDocumentDescription("batch created quantity document");
            reqDoc.setFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("KO");
            reqDoc.setOrganizationCode("SBSC");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@kuali.org");
            reqDoc.setRequestorPersonPhoneNumber("812-555-5555");
            reqDoc.setDeliveryBuildingCode("ADMN");
            reqDoc.setDeliveryBuildingName("Administration");
            reqDoc.setDeliveryBuildingRoomNumber("100");
            reqDoc.setDeliveryBuildingLine1Address("98 smart street");
            reqDoc.setDeliveryCityName("brainy");
            reqDoc.setDeliveryStateCode("CA");
            reqDoc.setDeliveryPostalCode("46202");
            reqDoc.setDeliveryToName("front desk");
            reqDoc.setBillingName("THE UNIVERSITY");
            reqDoc.setBillingLine1Address("ACCOUNTS PAYABLE");
            reqDoc.setBillingCityName("BUTTER NUT");
            reqDoc.setBillingStateCode("SC");
            reqDoc.setBillingPostalCode("47402");
            reqDoc.setBillingCountryCode("US");
            reqDoc.setBillingPhoneNumber("111-111-1111");
            reqDoc.setPurchaseOrderAutomaticIndicator(false);
            reqDoc.setStatusCode(RequisitionStatuses.IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1002);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("MK CORPORATION ACTIVE");
            reqDoc.setVendorLine1Address("3894 SOUTH ST");
            reqDoc.setVendorLine2Address("P.O. BOX 3455");
            reqDoc.setVendorCityName("SPRINGFIELD");
            reqDoc.setVendorStateCode("IL");
            reqDoc.setVendorPostalCode("33555");
            reqDoc.setVendorCountryCode("US");

            // set item attributes
            RequisitionItem item1 = new RequisitionItem();
            item1.setItemLineNumber(new Integer(1));
            item1.setItemUnitOfMeasureCode("PCS");
            item1.setItemCatalogNumber("P10M980");
            item1.setItemDescription("Copy Paper - 8 1/2 x 11, White, 92, 20lb");
            item1.setItemUnitPrice(new BigDecimal(30.20));
            item1.setItemTypeCode("ITEM");
            item1.setItemQuantity(new KualiDecimal(20));
            item1.setExtendedPrice(new KualiDecimal(604));
            item1.setItemAssignedToTradeInIndicator(false);
            // set accounting line attributes

            RequisitionAccount account1 = new RequisitionAccount();
            account1.setPostingYear(2004);
            account1.setChartOfAccountsCode("BL");
            account1.setAccountNumber("1023200");
            account1.setFinancialObjectCode("4100");
            account1.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            account1.setAmount(new KualiDecimal("100"));
            account1.setAccountLinePercent(new BigDecimal("100"));

            item1.getSourceAccountingLines().add(account1);
            reqDoc.getItems().add(item1);
        }
        catch (WorkflowException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return reqDoc;
    }
    
    private RequisitionDocument populateNonQuantityDocument() {
        RequisitionDocument reqDoc = null;
        try {
            reqDoc = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);

            // RequisitionDocument reqDoc = new RequisitionDocument();
            // set doc attributes
            reqDoc.getDocumentHeader().setExplanation("batch created non-quantity document");
            DocumentHeader documentHeader = reqDoc.getDocumentHeader();
            documentHeader.setFinancialDocumentDescription("batch created non-quantity document");
            reqDoc.setFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("KO");
            reqDoc.setOrganizationCode("SBSC");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@kuali.org");
            reqDoc.setRequestorPersonPhoneNumber("812-555-5555");
            reqDoc.setDeliveryBuildingCode("ADMN");
            reqDoc.setDeliveryBuildingName("Administration");
            reqDoc.setDeliveryBuildingRoomNumber("100");
            reqDoc.setDeliveryBuildingLine1Address("98 smart street");
            reqDoc.setDeliveryCityName("brainy");
            reqDoc.setDeliveryStateCode("CA");
            reqDoc.setDeliveryPostalCode("46202");
            reqDoc.setDeliveryToName("front desk");
            reqDoc.setBillingName("THE UNIVERSITY");
            reqDoc.setBillingLine1Address("ACCOUNTS PAYABLE");
            reqDoc.setBillingCityName("BUTTER NUT");
            reqDoc.setBillingStateCode("SC");
            reqDoc.setBillingPostalCode("47402");
            reqDoc.setBillingCountryCode("US");
            reqDoc.setBillingPhoneNumber("111-111-1111");
            reqDoc.setPurchaseOrderAutomaticIndicator(false);
            reqDoc.setStatusCode(RequisitionStatuses.IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1016);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("PHYSIK INSTRUMENT L.P.");
            reqDoc.setVendorLine1Address("16 AUBURN ST");
            reqDoc.setVendorCityName("AUBURN");
            reqDoc.setVendorStateCode("MA");
            reqDoc.setVendorPostalCode("01501");
            reqDoc.setVendorCountryCode("US");

            // set item attributes
            RequisitionItem item1 = new RequisitionItem();
            item1.setItemLineNumber(new Integer(1));
            item1.setItemUnitOfMeasureCode("");
            item1.setItemCatalogNumber("");
            item1.setItemDescription("consulting");
            item1.setItemUnitPrice(new BigDecimal(5000));
            item1.setItemTypeCode("SRVC");
            item1.setItemQuantity(null);
            item1.setExtendedPrice(new KualiDecimal(5000));
            item1.setItemAssignedToTradeInIndicator(false);
            // set accounting line attributes

            RequisitionAccount account1 = new RequisitionAccount();
            account1.setPostingYear(2004);
            account1.setChartOfAccountsCode("BL");
            account1.setAccountNumber("1023200");
            account1.setFinancialObjectCode("4078");
            account1.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            account1.setAmount(new KualiDecimal("100"));
            account1.setAccountLinePercent(new BigDecimal("100"));

            item1.getSourceAccountingLines().add(account1);
            reqDoc.getItems().add(item1);
        }
        catch (WorkflowException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return reqDoc;
    }

    /**
     * Sets the documentService attribute value. For use by Spring.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

}
