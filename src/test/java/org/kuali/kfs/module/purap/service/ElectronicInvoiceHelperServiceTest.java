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
package org.kuali.kfs.module.purap.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceInputFileType;
import org.kuali.kfs.module.purap.batch.ElectronicInvoiceStep;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoice;
import org.kuali.kfs.module.purap.businessobject.ElectronicInvoiceLoad;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.ElectronicInvoiceHelperServiceFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext
public class ElectronicInvoiceHelperServiceTest extends KualiTestBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceHelperServiceTest.class);
    private ElectronicInvoiceInputFileType electronicInvoiceInputFileType;
    private UnitTestSqlDao unitTestSqlDao;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        electronicInvoiceInputFileType = SpringContext.getBean(ElectronicInvoiceInputFileType.class);
        TestUtils.setSystemParameter(ElectronicInvoiceStep.class, PurapParameterConstants.ElectronicInvoiceParameters.FILE_MOVE_AFTER_LOAD_IND,"yes");
        FileUtils.cleanDirectory(new File(electronicInvoiceInputFileType.getDirectoryPath()));
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * TODO: Remove once other tests are fixed
     */
    public void testNothing() {

    }

    /**
     * TODO: Fix this test. It should not have a hard-coded URL for finding the schema.
     */
    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public void PATCHFIX_testRejectDocumentCreationInvalidData()
    throws Exception{

        String rejectFile = "reject.xml";
        PurchaseOrderDocument poDocument = createPODoc(null);
        String vendorDUNS = poDocument.getVendorDetail().getVendorDunsNumber();
        String poNumber = poDocument.getPurapDocumentIdentifier().toString();

        String xmlChunk = ElectronicInvoiceHelperServiceFixture.getCXMLForRejectDocCreation(vendorDUNS,poNumber);
        writeXMLFile(xmlChunk, rejectFile);

        //If we use the schemaLocation with DEV url and not running Tomcat locally, this test is going to fail, because
        //it won't be able to find the electronicInvoice.xsd unless Tomcat is running locally.
        //Therefore, for unit test purpose, let's set the schemaLocation to CNV url.
        String schemaLocation = electronicInvoiceInputFileType.getSchemaLocation();
        int beginIndex = schemaLocation.indexOf("static");
        String newSchemaLocation = "https://test.kuali.org/kfs-reg/" + schemaLocation.substring(beginIndex);
        electronicInvoiceInputFileType.setSchemaLocation(newSchemaLocation);

        ElectronicInvoiceLoad load = SpringContext.getBean(ElectronicInvoiceHelperService.class).loadElectronicInvoices();

        assertTrue(load.containsRejects());

        ElectronicInvoiceRejectDocument rejectDoc = (ElectronicInvoiceRejectDocument)load.getRejectDocuments().get(0);
        assertNotNull(rejectDoc);
        assertEquals(rejectDoc.getInvoiceFileName(),rejectFile);
        assertEquals(5, rejectDoc.getInvoiceRejectReasons().size());

        File rejectedFileInRejectDir = new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator + rejectFile);
        assertTrue(rejectedFileInRejectDir.exists());

    }

    /**
     * TODO: Fix this test. It should not have a hard-coded URL for finding the schema.
     */
    @ConfigureContext(session = kfs, shouldCommitTransactions=true)
    public void PATCHFIX_testRejectDocumentCreationCorruptXML()
    throws Exception{

        String corruptFile = "corrupt.xml";
        PurchaseOrderDocument poDocument = createPODoc(null);
        String vendorDUNS = poDocument.getVendorDetail().getVendorDunsNumber();
        String poNumber = poDocument.getPurapDocumentIdentifier().toString();

        String xmlChunk = ElectronicInvoiceHelperServiceFixture.getCorruptedCXML(vendorDUNS,poNumber);
        writeXMLFile(xmlChunk, corruptFile);

        ElectronicInvoiceLoad load = SpringContext.getBean(ElectronicInvoiceHelperService.class).loadElectronicInvoices();

        assertTrue(load.containsRejects());

        ElectronicInvoiceRejectDocument rejectDoc = (ElectronicInvoiceRejectDocument)load.getRejectDocuments().get(0);

        assertNotNull(rejectDoc);
        assertEquals(rejectDoc.getInvoiceFileName(),corruptFile);
        assertEquals(1, rejectDoc.getInvoiceRejectReasons().size());
        assertEquals(PurapConstants.ElectronicInvoice.FILE_FORMAT_INVALID,rejectDoc.getInvoiceRejectReasons().get(0).getInvoiceRejectReasonTypeCode());

        File  corruptedFileInRejectDir = new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "reject" + File.separator + corruptFile);
        assertTrue(corruptedFileInRejectDir.exists());

    }

    /**
     * TODO: Fix this test. It should not have a hard-coded URL for finding the schema.
     */
    @ConfigureContext(session = kfs, shouldCommitTransactions=false)
    public void PATCHFIX_testPaymentRequestDocumentCreation()
    throws Exception{

        String acceptFile = "accept.xml";

        changeCurrentUser(khuntley);
        RequisitionDocument reqDoc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        AccountingDocumentTestUtils.testRouteDocument(reqDoc, SpringContext.getBean(DocumentService.class));
        Integer reqId = reqDoc.getPurapDocumentIdentifier();

        changeCurrentUser(kfs);
        PurchaseOrderDocument poDocument = createPODoc(reqId);

        String vendorDUNS = RandomUtils.nextInt() + "";
        if (StringUtils.isEmpty(poDocument.getVendorDetail().getVendorDunsNumber())){
            VendorDetail vd = SpringContext.getBean(VendorService.class).getByVendorNumber(poDocument.getVendorNumber());
            vd.setVendorDunsNumber(vendorDUNS);
            SpringContext.getBean(VendorService.class).saveVendorHeader(vd);
        }else{
            vendorDUNS = poDocument.getVendorDetail().getVendorDunsNumber();
        }

        String poNumber = poDocument.getPurapDocumentIdentifier().toString();
        createItemMappingsRecords(poDocument.getVendorHeaderGeneratedIdentifier()+"", poDocument.getVendorDetailAssignedIdentifier()+"");
        updateUnitPriceVariance();

        String xmlChunk = ElectronicInvoiceHelperServiceFixture.getCXMLForPaymentDocCreation(vendorDUNS,poNumber);
        writeXMLFile(xmlChunk, acceptFile);

        ElectronicInvoiceLoad load = SpringContext.getBean(ElectronicInvoiceHelperService.class).loadElectronicInvoices();

        assertFalse(load.containsRejects());
        File acceptedFileInAcceptDir = new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + "accept" + File.separator + acceptFile);
        assertTrue(acceptedFileInAcceptDir.exists());

    }

    private PurchaseOrderDocument createPODoc(Integer reqId)
    throws Exception {
        PurchaseOrderDocument poDocument = PurchaseOrderDocumentFixture.EINVOICE_PO.createPurchaseOrderDocument();
        if (reqId != null){
            poDocument.setRequisitionIdentifier(reqId);
        }
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        poDocument.prepareForSave();
        assertFalse(DocumentStatus.ENROUTE.equals(poDocument.getDocumentHeader().getWorkflowDocument().getStatus()));
        AccountingDocumentTestUtils.routeDocument(poDocument, "saving copy source document", null, documentService);
        WorkflowTestUtils.waitForDocumentApproval(poDocument.getDocumentNumber());
        assertTrue("Document should now be final.", poDocument.getDocumentHeader().getWorkflowDocument().isFinal());

        return poDocument;
    }

    private void createItemMappingsRecords(String vendorHeaderGeneratedId,String vendorDetailAssignedId){
        unitTestSqlDao.sqlCommand("truncate table AP_ELCTRNC_INV_MAP_T");
        createItemMappingsRecord("1",vendorHeaderGeneratedId,vendorDetailAssignedId,"ITEM",ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_ITEM);
        createItemMappingsRecord("2",vendorHeaderGeneratedId,vendorDetailAssignedId,"TAX",ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_TAX);
        createItemMappingsRecord("3",vendorHeaderGeneratedId,vendorDetailAssignedId,"SPHD",ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SPECIAL_HANDLING);
        createItemMappingsRecord("4",vendorHeaderGeneratedId,vendorDetailAssignedId,"SHIP",ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_SHIPPING);
        createItemMappingsRecord("5",vendorHeaderGeneratedId,vendorDetailAssignedId,"DISC",ElectronicInvoice.INVOICE_AMOUNT_TYPE_CODE_DISCOUNT);
    }

    private void createItemMappingsRecord(String invId,
                                          String vendorHeaderGeneratedIdentifier,
                                          String vendorDetailAssignedIdentifier,
                                          String invoiceItemTypeCode,
                                          String itemTypeCode){

        String objId = java.util.UUID.randomUUID().toString();

        String sqlPart1 = "INSERT INTO AP_ELCTRNC_INV_MAP_T (AP_ELCTRNC_INV_MAP_ID,OBJ_ID,VER_NBR,VNDR_HDR_GNRTD_ID,VNDR_DTL_ASND_ID,INV_ITM_TYP_CD,ITM_TYP_CD) VALUES (";
        String sqlPart2 = invId + ",'" + objId + "',1," + vendorHeaderGeneratedIdentifier + "," + vendorDetailAssignedIdentifier + ",'" + invoiceItemTypeCode + "','" + itemTypeCode + "')";
        unitTestSqlDao.sqlCommand(sqlPart1 + sqlPart2);

    }

    private void updateUnitPriceVariance(){
        String query = "update PUR_PO_CST_SRC_T set ITM_UNIT_PRC_UPR_VAR_PCT=10,ITM_UNIT_PRC_LWR_VAR_PCT=10 where PO_CST_SRC_CD='EST'";
        unitTestSqlDao.sqlCommand(query);
    }

    private void writeXMLFile(String xmlChunk,String fileName)
    throws Exception {
        FileWriter fileWriter = new FileWriter(new File(electronicInvoiceInputFileType.getDirectoryPath() + File.separator + fileName));
        fileWriter.write(xmlChunk);
        fileWriter.flush();
        fileWriter.close();
    }

}
