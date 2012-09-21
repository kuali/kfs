/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.batch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.annotations.Param;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.POCostSources;
import org.kuali.kfs.module.purap.PurapConstants.POTransmissionMethods;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionSources;
import org.kuali.kfs.module.purap.PurapConstants.RequisitionStatuses;
import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderView;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetLocation;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.RequisitionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.kuali.kfs.sys.batch.Job;
import org.kuali.kfs.sys.batch.TestingStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.api.parameter.EvaluationOperator;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.api.parameter.Parameter.Builder;
import org.kuali.rice.coreservice.api.parameter.ParameterType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurapMassRequisitionStep extends AbstractStep implements TestingStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurapMassRequisitionStep.class);

    private DocumentService documentService;
    private RequisitionService requisitionService;
    private PurapService purapService;
    private BusinessObjectService boService;
    private PersistenceStructureService psService;

    private final int NUM_DOCS_TO_CREATE = 25; // number of each document type to create
    private final int ROUTE_TO_FINAL_SECONDS_LIMIT = 240; // number of seconds to wait for routing of documents to Final.
    private final String RUN_INDICATOR_PARAMETER_NAMESPACE_STEP = "PurapMassRequisitionStep";
    private final String RUN_INDICATOR_PARAMETER_VALUE = "N";
    private final String RUN_INDICATOR_PARAMETER_ALLOWED = "A";
    private final String RUN_INDICATOR_PARAMETER_DESCRIPTION = "Tells the job framework whether to run this job or not; because the PurapMassRequisitionJob needs to only be run once after database initialization.";
    private final String RUN_INDICATOR_PARAMETER_TYPE = "CONFG";

    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
        LOG.debug("Starting execution of PurapMassRequisitionStep");

        Parameter runIndicatorParameter = SpringContext.getBean(ParameterService.class).getParameter(PurapMassRequisitionStep.class,Job.STEP_RUN_PARM_NM);
        
        if (runIndicatorParameter == null || "Y".equals(runIndicatorParameter.getValue())) {
            // save runParameter as "N" so that the job won't run until DB has been cleared
            setInitiatedRunParameter();

            for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {

                try {
                    LOG.info("Setting user session for routing of quantity document.");
                    GlobalVariables.setUserSession(new UserSession("khuntley"));
                    // create document
                    RequisitionDocument reqDoc = populateQuantityDocument();

                    LOG.info("Blanket approving quantity requisition document.");
                    // route it
                    documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
                }
                catch (WorkflowException e) {
                    e.printStackTrace();
                }
                Thread.sleep(5000);
            }

            for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {

                try {
                    LOG.info("Setting user session for routing of non-quantity document.");
                    GlobalVariables.setUserSession(new UserSession("khuntley"));
                    // create document
                    RequisitionDocument reqDoc = populateNonQuantityDocument();
                    LOG.info("Blanket approving non-quantity requisition document.");
                    // route it
                    documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
                }
                catch (WorkflowException e) {
                    e.printStackTrace();
                }
                Thread.sleep(5000);
            }


            // TODO leaving CAMS docs commented out until problem is fixed
            // for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {
            // RequisitionDocument reqDoc = null;
            // try {
            // LOG.info("Setting user session for routing of requisition with capital asset data and addresses.");
            // GlobalVariables.setUserSession(new UserSession("khuntley"));
            // // create document
            // reqDoc = populateCapitalAsset_Individual_WithAddresses_Document();
            // Thread.sleep(10000);
            // LOG.info("Blanket approving requisition with capital asset data and addresses.");
            // // route it
            // documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
            // }
            // catch (WorkflowException e) {
            // e.printStackTrace();
            // }
            // catch (ValidationException ve) {
            // continue;
            // }
            // Thread.sleep(5000);
            //
            // // Use Contract Manager Assignment to create PO.
            // LOG.info("Setting user session for contract manager assignment of requisition with capital asset data and addresses.");
            // GlobalVariables.setUserSession(new UserSession("parke"));
            // LOG.info("Routing Contract Manager Assignment document for requisition with capital asset data and addresses.");
            // ContractManagerAssignmentDocument acmDoc = createAndRouteContractManagerAssignmentDocument(reqDoc);
            //
            // Thread.sleep(20000);
            //
            // LOG.info("Routing Purchase Order document for requisition with capital asset data and addresses.");
            // createAndRoutePurchaseOrderDocument(reqDoc, acmDoc);
            //
            // Thread.sleep(5000);
            // }
            //
            // for (int i = 0; i < NUM_DOCS_TO_CREATE; i++) {
            // RequisitionDocument reqDoc = null;
            // try {
            // LOG.info("Setting user session for routing of BAS requisition.");
            // GlobalVariables.setUserSession(new UserSession("khuntley"));
            // // create document
            // reqDoc = populateCapitalAsset_Individual_Unfilled_Document();
            // Thread.sleep(10000);
            // LOG.info("Blanket approving BAS requisition.");
            // // route it
            // documentService.blanketApproveDocument(reqDoc, "auto-routing: Test Requisition Job.", null);
            // }
            // catch (WorkflowException e) {
            // e.printStackTrace();
            // }
            // catch (ValidationException ve) {
            // continue;
            // }
            // Thread.sleep(5000);
            //
            // // Use Contract Manager Assignment to create PO.
            // LOG.info("Setting user session for contract manager assignment BAS requisition.");
            // GlobalVariables.setUserSession(new UserSession("parke"));
            // LOG.info("Routing Contract Manager Assignment document for BAS requisition.");
            // ContractManagerAssignmentDocument acmDoc = createAndRouteContractManagerAssignmentDocument(reqDoc);
            //
            // Thread.sleep(20000);
            //
            // LOG.info("Routing Purchase Order document for BAS requisition");
            // createAndRoutePurchaseOrderDocument(reqDoc, acmDoc);
            //
            // Thread.sleep(5000);
            // }
        }

        Thread.sleep(60000);
        return true;
    }

    private RequisitionDocument populateQuantityDocument() {
        LOG.debug("Creating a new requisition.");
        RequisitionDocument reqDoc = null;
        try {
            reqDoc = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);
            LOG.info("Populating a new requisition.");
            // RequisitionDocument reqDoc = new RequisitionDocument();
            // set doc attributes
            reqDoc.getDocumentHeader().setExplanation("batch created quantity document");
            DocumentHeader documentHeader = reqDoc.getDocumentHeader();
            documentHeader.setDocumentDescription("batch created quantity document");
            reqDoc.setDocumentFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("KO");
            reqDoc.setOrganizationCode("SBSC");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@localhost.localhost");
            reqDoc.setRequestorPersonPhoneNumber("812-555-5555");
            reqDoc.setDeliveryBuildingCode("ADMN");
            reqDoc.setDeliveryBuildingName("Administration");
            reqDoc.setDeliveryBuildingRoomNumber("100");
            reqDoc.setDeliveryBuildingLine1Address("98 smart street");
            reqDoc.setDeliveryCityName("brainy");
            reqDoc.setDeliveryStateCode("CA");
            reqDoc.setDeliveryPostalCode("46202");
            reqDoc.setDeliveryCountryCode("US");
            reqDoc.setDeliveryToName("front desk");
            reqDoc.setBillingName("THE UNIVERSITY");
            reqDoc.setBillingLine1Address("ACCOUNTS PAYABLE");
            reqDoc.setBillingCityName("BUTTER NUT");
            reqDoc.setBillingStateCode("SC");
            reqDoc.setBillingPostalCode("47402");
            reqDoc.setBillingCountryCode("US");
            reqDoc.setBillingPhoneNumber("111-111-1111");
            reqDoc.setPurchaseOrderAutomaticIndicator(false);            
            reqDoc.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1002);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("MK CORPORATION ACTIVE");
            reqDoc.setVendorLine1Address("3894 SOUTH ST");
            reqDoc.setVendorLine2Address("P.O. BOX 3455");
            reqDoc.setVendorCityName("SPRINGFIELD");
            reqDoc.setVendorStateCode("IL");
            reqDoc.setVendorPostalCode("33555");
            reqDoc.setVendorCountryCode("US");
            reqDoc.setUseTaxIndicator(false);

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
            reqDoc.fixItemReferences();
        }
        catch (WorkflowException e1) {
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
            documentHeader.setDocumentDescription("batch created non-quantity document");
            reqDoc.setDocumentFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("KO");
            reqDoc.setOrganizationCode("SBSC");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setDeliveryCountryCode("US");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@localhost.localhost");
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
            reqDoc.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1016);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("PHYSIK INSTRUMENT L.P.");
            reqDoc.setVendorLine1Address("16 AUBURN ST");
            reqDoc.setVendorCityName("AUBURN");
            reqDoc.setVendorStateCode("MA");
            reqDoc.setVendorPostalCode("01501");
            reqDoc.setVendorCountryCode("US");
            reqDoc.setUseTaxIndicator(false);

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
            reqDoc.fixItemReferences();
        }
        catch (WorkflowException e1) {
            e1.printStackTrace();
        }
        return reqDoc;
    }

    public RequisitionDocument populateCapitalAsset_Individual_WithAddresses_Document() {
        RequisitionDocument reqDoc = null;
        try {
            reqDoc = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);

            // set doc attributes
            reqDoc.getDocumentHeader().setExplanation("batch created quantity document for cams");
            DocumentHeader documentHeader = reqDoc.getDocumentHeader();
            documentHeader.setDocumentDescription("batch created quantity document for cams");
            reqDoc.setDocumentFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("UA");
            reqDoc.setOrganizationCode("VPIT");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@localhost.localhost");
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
            reqDoc.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1002);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("MK CORPORATION ACTIVE");
            reqDoc.setVendorLine1Address("3984 SOUTH ST");
            reqDoc.setVendorCityName("SPRINGFIELD");
            reqDoc.setVendorStateCode("IL");
            reqDoc.setVendorPostalCode("33555");
            reqDoc.setVendorCountryCode("US");
            reqDoc.setUseTaxIndicator(false);

            // set item attributes
            RequisitionItem item1 = new RequisitionItem();
            item1.setItemLineNumber(new Integer(1));
            item1.setItemUnitOfMeasureCode("EA");
            item1.setItemCatalogNumber("P10M980");
            item1.setItemDescription("Gas Chromatograph");
            item1.setItemUnitPrice(new BigDecimal(5000));
            item1.setItemTypeCode("ITEM");
            item1.setItemQuantity(new KualiDecimal(2.00));
            item1.setExtendedPrice(new KualiDecimal(10000));
            item1.setItemAssignedToTradeInIndicator(false);
            item1.refreshReferenceObject("itemType");

            // set accounting line attributes
            RequisitionAccount account1 = new RequisitionAccount();
            account1.setPostingYear(2004);
            account1.setChartOfAccountsCode("BL");
            account1.setAccountNumber("1023200");
            account1.setFinancialObjectCode("7000");
            account1.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            account1.setAmount(new KualiDecimal("10000"));
            account1.setAccountLinePercent(new BigDecimal("100"));

            item1.getSourceAccountingLines().add(account1);
            reqDoc.getItems().add(item1);
            reqDoc.fixItemReferences();

            reqDoc.setCapitalAssetSystemStateCode("NEW");
            reqDoc.setCapitalAssetSystemTypeCode("IND");

            // Save here because auto-generated IDs will be needed later.
            purapService.saveDocumentNoValidation(reqDoc);
            List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems = new ArrayList();
            RequisitionCapitalAssetItem capitalAssetItem = (RequisitionCapitalAssetItem) requisitionService.createCamsItem(reqDoc, item1);
            capitalAssetItem.setCapitalAssetTransactionTypeCode("NEW");

            RequisitionCapitalAssetSystem system = (RequisitionCapitalAssetSystem) capitalAssetItem.getPurchasingCapitalAssetSystem();
            system.setCapitalAssetNoteText("CA Notes go here");
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            system.setCapitalAssetManufacturerName("MK CORPORATION ACTIVE");
            system.setCapitalAssetTypeCode("07034");
            system.setCapitalAssetModelDescription("XXYYZZ");

            List<CapitalAssetLocation> locations = new ArrayList();

            RequisitionCapitalAssetLocation loc1 = new RequisitionCapitalAssetLocation();
            loc1.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            loc1.setItemQuantity(new KualiDecimal("1.00"));
            loc1.setCampusCode("BL");
            loc1.setBuildingCode("BL001");
            loc1.setCapitalAssetLine1Address("211 S Indiana Ave");
            loc1.setBuildingRoomNumber("001");
            loc1.setCapitalAssetCityName("Bloomington");
            loc1.setCapitalAssetStateCode("IN");
            loc1.setCapitalAssetPostalCode("47405-7001");
            loc1.setCapitalAssetCountryCode("US");
            locations.add(loc1);

            RequisitionCapitalAssetLocation loc2 = new RequisitionCapitalAssetLocation();
            loc2.setCapitalAssetSystemIdentifier(system.getCapitalAssetSystemIdentifier());
            loc2.setItemQuantity(new KualiDecimal("1.00"));
            loc2.setCampusCode("BL");
            loc2.setBuildingCode("BL001");
            loc2.setCapitalAssetLine1Address("211 S Indiana Ave");
            loc2.setBuildingRoomNumber("001A");
            loc2.setCapitalAssetCityName("Bloomington");
            loc2.setCapitalAssetStateCode("IN");
            loc2.setCapitalAssetPostalCode("47405-7001");
            loc2.setCapitalAssetCountryCode("US");
            locations.add(loc2);

            system.setCapitalAssetLocations(locations);

            purchasingCapitalAssetItems.add(capitalAssetItem);
            reqDoc.setPurchasingCapitalAssetItems(purchasingCapitalAssetItems);
        }
        catch (WorkflowException e1) {
            e1.printStackTrace();
        }
        return reqDoc;
    }

    private RequisitionDocument populateCapitalAsset_Individual_Unfilled_Document() {
        RequisitionDocument reqDoc = null;
        try {
            reqDoc = (RequisitionDocument) documentService.getNewDocument(RequisitionDocument.class);

            // set doc attributes
            reqDoc.getDocumentHeader().setExplanation("batch created quantity document BAS");
            DocumentHeader documentHeader = reqDoc.getDocumentHeader();
            documentHeader.setDocumentDescription("batch created quantity document BAS");
            reqDoc.setDocumentFundingSourceCode("INST");
            reqDoc.setRequisitionSourceCode(RequisitionSources.STANDARD_ORDER);
            reqDoc.setPurchaseOrderTransmissionMethodCode(POTransmissionMethods.NOPRINT);
            reqDoc.setPurchaseOrderCostSourceCode(POCostSources.ESTIMATE);
            reqDoc.setChartOfAccountsCode("UA");
            reqDoc.setOrganizationCode("VPIT");
            reqDoc.setDeliveryCampusCode("KO");
            reqDoc.setRequestorPersonName("WATSON,TERRENCE G");
            reqDoc.setRequestorPersonEmailAddress("tw@localhost.localhost");
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
            reqDoc.setApplicationDocumentStatus(RequisitionStatuses.APPDOC_IN_PROCESS);
            reqDoc.setVendorHeaderGeneratedIdentifier(1002);
            reqDoc.setVendorDetailAssignedIdentifier(0);
            reqDoc.setVendorName("MK CORPORATION ACTIVE");
            reqDoc.setVendorLine1Address("3984 SOUTH ST");
            reqDoc.setVendorCityName("SPRINGFIELD");
            reqDoc.setVendorStateCode("IL");
            reqDoc.setVendorPostalCode("33555");
            reqDoc.setVendorCountryCode("US");
            reqDoc.setUseTaxIndicator(false);

            // set item attributes
            RequisitionItem item1 = new RequisitionItem();
            item1.setItemLineNumber(new Integer(1));
            item1.setItemUnitOfMeasureCode("EA");
            item1.setItemCatalogNumber("");
            item1.setItemDescription("Gas Chromatograph");
            item1.setItemUnitPrice(new BigDecimal(6000));
            item1.setItemTypeCode("ITEM");
            item1.setItemQuantity(new KualiDecimal(1.00));
            item1.setExtendedPrice(new KualiDecimal(6000));
            item1.setItemAssignedToTradeInIndicator(false);

            // set accounting line attributes
            RequisitionAccount account1 = new RequisitionAccount();
            account1.setPostingYear(2004);
            account1.setChartOfAccountsCode("BL");
            account1.setAccountNumber("1023200");
            account1.setFinancialObjectCode("7000");
            account1.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            account1.setAmount(new KualiDecimal("10000"));
            account1.setAccountLinePercent(new BigDecimal("100"));

            item1.getSourceAccountingLines().add(account1);
            reqDoc.getItems().add(item1);
            reqDoc.fixItemReferences();

            reqDoc.setCapitalAssetSystemStateCode("NEW");
            reqDoc.setCapitalAssetSystemTypeCode("IND");

            // Save here because auto-generated IDs will be needed later.
            purapService.saveDocumentNoValidation(reqDoc);
            List<PurchasingCapitalAssetItem> purchasingCapitalAssetItems = new ArrayList();
            RequisitionCapitalAssetItem capitalAssetItem = (RequisitionCapitalAssetItem) requisitionService.createCamsItem(reqDoc, item1);
            capitalAssetItem.setCapitalAssetTransactionTypeCode("NEW");

            RequisitionCapitalAssetSystem system = (RequisitionCapitalAssetSystem) capitalAssetItem.getPurchasingCapitalAssetSystem();
            system.setCapitalAssetNoteText("");
            system.setCapitalAssetNotReceivedCurrentFiscalYearIndicator(false);
            system.setCapitalAssetManufacturerName("");
            system.setCapitalAssetTypeCode("");
            system.setCapitalAssetModelDescription("");

            purchasingCapitalAssetItems.add(capitalAssetItem);
            reqDoc.setPurchasingCapitalAssetItems(purchasingCapitalAssetItems);
        }
        catch (WorkflowException e1) {
            e1.printStackTrace();
        }
        return reqDoc;
    }

    /**
     * Sets a Run parameter that tells the step that it has already run and it does not need to run again.
     */
    private void setInitiatedRunParameter() {
        // First, see if we can find an existing Parameter object with this key.
        Parameter runIndicatorParameter = SpringContext.getBean(ParameterService.class).getParameter(PurapMassRequisitionStep.class,Job.STEP_RUN_PARM_NM);
        if (runIndicatorParameter == null) {
            Parameter.Builder newParameter = Builder.create(KFSConstants.APPLICATION_NAMESPACE_CODE, PurapConstants.PURAP_NAMESPACE, RUN_INDICATOR_PARAMETER_NAMESPACE_STEP, Job.STEP_RUN_PARM_NM, ParameterType.Builder.create("CONFG"));
            newParameter.setDescription(RUN_INDICATOR_PARAMETER_DESCRIPTION);
            newParameter.setEvaluationOperator(EvaluationOperator.ALLOW);
            newParameter.setValue(RUN_INDICATOR_PARAMETER_VALUE);
            SpringContext.getBean(ParameterService.class).createParameter( newParameter.build() );
        } else {
            Parameter.Builder newParameter = Builder.create(runIndicatorParameter);
            newParameter.setValue(RUN_INDICATOR_PARAMETER_VALUE);
            SpringContext.getBean(ParameterService.class).updateParameter( newParameter.build() );
        }
    }

    /**
     * Used by setInitiatedRunParameter to build a Map of search keys to obtain the exact needed Run parameter from the database.
     * 
     * @return A Map<String,Object> where the Objects are the attributes of the Run parameter
     */
    private Map<String, Object> buildRunParameterSearchKeyMap() {
        Map<String, Object> pkMapForParameter = new HashMap<String, Object>();

        // Set up a list of all the field names and values of the fields in the Parameter object.
        Map<String, Object> fieldNamesValuesForParameter = new HashMap<String, Object>();
        fieldNamesValuesForParameter.put("parameterNamespaceCode", PurapConstants.PURAP_NAMESPACE);
        fieldNamesValuesForParameter.put("parameterDetailTypeCode", RUN_INDICATOR_PARAMETER_NAMESPACE_STEP);
        fieldNamesValuesForParameter.put("parameterName", Job.STEP_RUN_PARM_NM);
        fieldNamesValuesForParameter.put("parameterConstraintCode", RUN_INDICATOR_PARAMETER_ALLOWED);
        fieldNamesValuesForParameter.put("parameterTypeCode", RUN_INDICATOR_PARAMETER_TYPE);

        // get the primary keys and assign them to values
        List<String> parameterPKFields = psService.getPrimaryKeys(Parameter.class);
        for (String pkFieldName : parameterPKFields) {
            pkMapForParameter.put(pkFieldName, fieldNamesValuesForParameter.get(pkFieldName));
        }
        return pkMapForParameter;
    }

    private ContractManagerAssignmentDocument createAndRouteContractManagerAssignmentDocument(RequisitionDocument reqDoc) {
        ContractManagerAssignmentDocument acmDoc = null;
        try {
            acmDoc = (ContractManagerAssignmentDocument) documentService.getNewDocument(ContractManagerAssignmentDocument.class);
            List<ContractManagerAssignmentDetail> contractManagerAssignmentDetails = new ArrayList<ContractManagerAssignmentDetail>();
            ContractManagerAssignmentDetail detail = new ContractManagerAssignmentDetail(acmDoc, reqDoc);
            detail.setContractManagerCode(new Integer("10"));
            detail.refreshReferenceObject("contractManager");
            contractManagerAssignmentDetails.add(detail);
            acmDoc.setContractManagerAssignmentDetailss(contractManagerAssignmentDetails);
            acmDoc.getDocumentHeader().setDocumentDescription("batch-created");
            documentService.routeDocument(acmDoc, "Routing batch-created Contract Manager Assignment Document", null);
            ChangeWaiter waiter = new ChangeWaiter(documentService, acmDoc.getDocumentNumber(), "F");
            try {
                waiter.waitUntilChange(waiter, ROUTE_TO_FINAL_SECONDS_LIMIT, 5);
            }
            catch (Exception e) {
                throw new RuntimeException("ContractManagerAssignmentDocument timed out in routing to final.");
            }
        }
        catch (WorkflowException we) {
            we.printStackTrace();
        }
        catch (ValidationException ve) {
            ve.printStackTrace();
        }
        return acmDoc;
    }

    private void createAndRoutePurchaseOrderDocument(RequisitionDocument reqDoc, ContractManagerAssignmentDocument acmDoc) {

        List<PurchaseOrderView> poViews = reqDoc.getRelatedViews().getRelatedPurchaseOrderViews();
        if ((poViews != null) && (poViews.size() >= 1)) {
            // There should be only one related PO at this point, so get that one and route it.
            PurchaseOrderView poView = poViews.get(0);
            String relatedPOWorkflowDocumentId = poView.getDocumentNumber();
            PurchaseOrderDocument poDoc = null;
            try {
                poDoc = (PurchaseOrderDocument) documentService.getByDocumentHeaderId(relatedPOWorkflowDocumentId);
                documentService.blanketApproveDocument(poDoc, "auto-routing: Test Requisition Job", null);
                ChangeWaiter waiter = new ChangeWaiter(documentService, poDoc.getDocumentNumber(), "F");
                try {
                    waiter.waitUntilChange(waiter, ROUTE_TO_FINAL_SECONDS_LIMIT, 5);
                }
                catch (Exception e) {
                    throw new RuntimeException("ContractManagerAssignmentDocument timed out in routing to final.");
                }
            }
            catch (WorkflowException e) {
                e.printStackTrace();
            }
            catch (ValidationException ve) {
                ve.printStackTrace();
            }
        }
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public RequisitionService getRequisitionService() {
        return requisitionService;
    }

    public void setRequisitionService(RequisitionService requisitionService) {
        this.requisitionService = requisitionService;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public BusinessObjectService getBoService() {
        return boService;
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    public PersistenceStructureService getPsService() {
        return psService;
    }

    public void setPsService(PersistenceStructureService psService) {
        this.psService = psService;
    }

    /**
     * Performs the same function as DocumentWorkflowStatusMonitor, which is a test utility, and cannot be used outside that
     * package. Allows us to wait until a change of Workflow status has occurred.
     */
    private class ChangeWaiter {

        final DocumentService documentService;
        final private String docHeaderId;
        final private String[] desiredWorkflowStates;

        public ChangeWaiter(DocumentService documentService, String docHeaderId, String desiredWorkflowStatus) {
            this.documentService = documentService;
            this.docHeaderId = docHeaderId;
            this.desiredWorkflowStates = new String[] { desiredWorkflowStatus };
        }

        public boolean valueChanged() throws Exception {
            Document d = documentService.getByDocumentHeaderId(docHeaderId.toString());

            String currentStatus = d.getDocumentHeader().getWorkflowDocument().getStatus().getCode();

            for (int i = 0; i < desiredWorkflowStates.length; i++) {
                if (StringUtils.equals(desiredWorkflowStates[i], currentStatus)) {
                    return true;
                }
            }
            return false;
        }

        public boolean waitUntilChange(ChangeWaiter monitor, int maxWaitSeconds, int pauseSeconds) throws Exception {
            long maxWaitMs = maxWaitSeconds * 1000;
            long pauseMs = pauseSeconds * 1000;

            boolean valueChanged = false;
            boolean interrupted = false;
            long startTimeMs = System.currentTimeMillis();
            long endTimeMs = startTimeMs + maxWaitMs;

            Thread.sleep(pauseMs / 10); // the first time through, sleep a fraction of the specified time
            valueChanged = monitor.valueChanged();
            LOG.debug("starting wait loop");
            while (!interrupted && !valueChanged && (System.currentTimeMillis() < endTimeMs)) {
                try {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("sleeping for " + pauseMs + " ms");
                    }
                    Thread.sleep(pauseMs);
                }
                catch (InterruptedException e) {
                    interrupted = true;
                }
                LOG.debug("checking wait loop sentinel");
                valueChanged = monitor.valueChanged();
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("finished wait loop (" + valueChanged + ")");
            }
            
            return valueChanged;
        }
    }

}
