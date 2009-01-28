/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.cab.batch.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.AssetPaymentDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.KualiDecimal;

public class GlLineServiceTest extends KualiTestBase {
    private GlLineService glLineService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerEntry primary;
    private List<GeneralLedgerEntry> entries;

    @Override
    @ConfigureContext(session = UserNameFixture.bomiddle, shouldCommitTransactions = false)
    protected void setUp() throws Exception {
        super.setUp();
        glLineService = SpringContext.getBean(GlLineService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        // create a list of GL entries
        GeneralLedgerEntry entry = createCABGLEntry("1023200", 1, new KualiDecimal(5200.50));

        GeneralLedgerEntry entry2 = createCABGLEntry("1031400", 2, new KualiDecimal(3300.50));

        // use a primary entry too
        this.primary = entry;
        this.entries = new ArrayList<GeneralLedgerEntry>();
        entries.add(entry);
        entries.add(entry2);
    }

    private CapitalAssetInformationDetail createNewCapitalAssetInformationDetail() {
        CapitalAssetInformationDetail assetInformationDetail = new CapitalAssetInformationDetail();
        assetInformationDetail.setDocumentNumber("1001");
        assetInformationDetail.setItemLineNumber(1);
        assetInformationDetail.setCampusCode("BL");
        assetInformationDetail.setBuildingCode("BL001");
        assetInformationDetail.setBuildingRoomNumber("001");
        assetInformationDetail.setBuildingSubRoomNumber("23");
        assetInformationDetail.setCapitalAssetTagNumber("TGX");
        assetInformationDetail.setCapitalAssetSerialNumber("SER");
        return assetInformationDetail;
    }

    private CapitalAssetInformation createNewCapitalAssetInformation() {
        CapitalAssetInformation assetInformation = new CapitalAssetInformation();
        assetInformation.setDocumentNumber("1001");
        assetInformation.setVendorHeaderGeneratedIdentifier(1000);
        assetInformation.setVendorDetailAssignedIdentifier(0);
        assetInformation.setCapitalAssetTypeCode("07009");
        assetInformation.setCapitalAssetManufacturerName("MFR");
        assetInformation.setCapitalAssetDescription("DESC");
        assetInformation.setCapitalAssetManufacturerModelNumber("MDL");
        return assetInformation;
    }

    private GeneralLedgerEntry createCABGLEntry(String accountNumber, int seqNumber, KualiDecimal amount) throws ParseException {
        Entry glEntry = createGLEntry(accountNumber, seqNumber, amount);
        businessObjectService.save(glEntry);
        GeneralLedgerEntry entry = new GeneralLedgerEntry(glEntry);
        return entry;
    }

    private Entry createGLEntry(String accountNumber, int seqNumber, KualiDecimal amount) throws ParseException {
        Entry entry = new Entry();
        entry.setUniversityFiscalYear(2008);
        entry.setChartOfAccountsCode("BL");
        entry.setAccountNumber(accountNumber);
        entry.setSubAccountNumber("---");
        entry.setFinancialObjectCode("7015");
        entry.setFinancialSubObjectCode("---");
        entry.setFinancialBalanceTypeCode("AC");
        entry.setFinancialObjectTypeCode("AS");
        entry.setUniversityFiscalPeriodCode("10");
        entry.setFinancialDocumentTypeCode("INV");
        entry.setFinancialSystemOriginationCode("01");
        entry.setDocumentNumber("1001");
        entry.setTransactionLedgerEntrySequenceNumber(seqNumber);
        entry.setTransactionLedgerEntryDescription("Test GL");
        entry.setTransactionLedgerEntryAmount(amount);
        entry.setOrganizationReferenceId(null);
        entry.setReferenceFinancialSystemOriginationCode(null);
        entry.setReferenceFinancialDocumentNumber(null);
        entry.setTransactionDebitCreditCode("D");
        entry.setOrganizationDocumentNumber(null);
        entry.setProjectCode(null);
        entry.setTransactionDate(new Date(DateUtils.parseDate("04/23/2008", new String[] { "MM/dd/yyyy" }).getTime()));
        entry.setTransactionPostingDate(new Date(DateUtils.parseDate("04/23/2008", new String[] { "MM/dd/yyyy" }).getTime()));
        entry.setTransactionDateTimeStamp(new Timestamp(DateUtils.parseDate("04/23/2008 10:59:59", new String[] { "MM/dd/yyyy HH:mm:ss" }).getTime()));
        return entry;
    }

    public void testCreateAssetGlobalDocument_noFPData() throws Exception {
        MaintenanceDocument assetGlobalDocument = (MaintenanceDocument) glLineService.createAssetGlobalDocument(entries, primary);
        assertNotNull(assetGlobalDocument);
        AssetGlobal assetGlobal = (AssetGlobal) assetGlobalDocument.getNewMaintainableObject().getBusinessObject();
        // assert here
        assertEquals("BL", assetGlobal.getOrganizationOwnerChartOfAccountsCode());
        assertEquals("1023200", assetGlobal.getOrganizationOwnerAccountNumber());
        assertEquals(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE, assetGlobal.getAcquisitionTypeCode());
        assertEquals(null, assetGlobal.getInventoryStatusCode());
        assertEquals(null, assetGlobal.getCapitalAssetTypeCode());
        assertEquals(null, assetGlobal.getManufacturerName());
        assertEquals(null, assetGlobal.getManufacturerModelNumber());
        assertEquals(null, assetGlobal.getVendorName());
        assertEquals(null, assetGlobal.getCapitalAssetDescription());
        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetSharedDetails();
        assertTrue(assetGlobalDetails.isEmpty());
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        assertEquals(2, assetPaymentDetails.size());
        if ("1023200".equals(assetPaymentDetails.get(0).getAccountNumber())) {
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(0), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(1), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
        }
        else {
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(0), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(1), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
        }
    }

    public void testCreateAssetGlobalDocument_FPData() throws Exception {
        CapitalAssetInformation assetInformation = createNewCapitalAssetInformation();
        assetInformation.getCapitalAssetInformationDetails().add(createNewCapitalAssetInformationDetail());
        businessObjectService.save(assetInformation);
        assetInformation.refreshNonUpdateableReferences();
        MaintenanceDocument assetGlobalDocument = (MaintenanceDocument) glLineService.createAssetGlobalDocument(entries, primary);
        assertNotNull(assetGlobalDocument);
        AssetGlobal assetGlobal = (AssetGlobal) assetGlobalDocument.getNewMaintainableObject().getBusinessObject();
        // assert here
        assertEquals("BL", assetGlobal.getOrganizationOwnerChartOfAccountsCode());
        assertEquals("1023200", assetGlobal.getOrganizationOwnerAccountNumber());
        assertEquals(CamsConstants.AssetGlobal.NEW_ACQUISITION_TYPE_CODE, assetGlobal.getAcquisitionTypeCode());
        assertEquals("A", assetGlobal.getInventoryStatusCode());
        assertEquals("07009", assetGlobal.getCapitalAssetTypeCode());
        assertEquals("MFR", assetGlobal.getManufacturerName());
        assertEquals("MDL", assetGlobal.getManufacturerModelNumber());
        assertEquals("DESC", assetGlobal.getCapitalAssetDescription());
        assertEquals("ABC CLEANING SERVICES", assetGlobal.getVendorName());

        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetSharedDetails();
        assertEquals(1, assetGlobalDetails.size());
        AssetGlobalDetail assetGlobalDetail = assetGlobalDetails.get(0);
        assertEquals("BL", assetGlobalDetail.getCampusCode());
        assertEquals("BL001", assetGlobalDetail.getBuildingCode());
        assertEquals("001", assetGlobalDetail.getBuildingRoomNumber());
        assertEquals("23", assetGlobalDetail.getBuildingSubRoomNumber());
        assertEquals("TGX", assetGlobalDetail.getCampusTagNumber());
        assertEquals("SER", assetGlobalDetail.getSerialNumber());
        List<AssetPaymentDetail> assetPaymentDetails = assetGlobal.getAssetPaymentDetails();
        assertEquals(2, assetPaymentDetails.size());
        if ("1023200".equals(assetPaymentDetails.get(0).getAccountNumber())) {
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(0), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(1), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
        }
        else {
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(0), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
            assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(1), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
        }
    }

    private void assertAssetPaymentDetail(Document document, AssetPaymentDetail detail, String accountNumber, KualiDecimal amount, Integer seqNo) {
        assertEquals(document.getDocumentNumber(), detail.getDocumentNumber());
        assertEquals(seqNo, detail.getSequenceNumber());
        assertEquals(Integer.valueOf(2008), detail.getPostingYear());
        assertEquals("10", detail.getPostingPeriodCode());
        assertEquals("BL", detail.getChartOfAccountsCode());
        assertEquals(accountNumber, detail.getAccountNumber());
        assertEquals("", detail.getSubAccountNumber());
        assertEquals("7015", detail.getFinancialObjectCode());
        assertEquals("", detail.getProjectCode());
        assertEquals("", detail.getOrganizationReferenceId());
        assertEquals(amount, detail.getAmount());
        assertEquals("01", detail.getExpenditureFinancialSystemOriginationCode());
        assertEquals("1001", detail.getExpenditureFinancialDocumentNumber());
        assertEquals("INV", detail.getExpenditureFinancialDocumentTypeCode());
        assertEquals("04/23/2008", new SimpleDateFormat("MM/dd/yyyy").format(detail.getExpenditureFinancialDocumentPostedDate()));
        assertEquals("", detail.getPurchaseOrderNumber());
        assertEquals(false, detail.isTransferPaymentIndicator());
    }

    public void testCreateAssetPaymentDocument_noFPData() throws Exception {
        AssetPaymentDocument document = (AssetPaymentDocument) glLineService.createAssetPaymentDocument(entries, primary);
        assertNotNull(document);
        // assert here
        List<AssetPaymentDetail> assetPaymentDetails = document.getSourceAccountingLines();
        assertEquals(2, assetPaymentDetails.size());
        if ("1023200".equals(assetPaymentDetails.get(0).getAccountNumber())) {
            assertAssetPaymentDetail(document, assetPaymentDetails.get(0), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
            assertAssetPaymentDetail(document, assetPaymentDetails.get(1), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
        }
        else {
            assertAssetPaymentDetail(document, assetPaymentDetails.get(0), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
            assertAssetPaymentDetail(document, assetPaymentDetails.get(1), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
        }
    }

    public void testCreateAssetPaymentDocument_FPData() throws Exception {
        CapitalAssetInformation assetInformation = new CapitalAssetInformation();
        assetInformation.setDocumentNumber("1001");
        assetInformation.setCapitalAssetNumber(1594L);
        businessObjectService.save(assetInformation);
        AssetPaymentDocument document = (AssetPaymentDocument) glLineService.createAssetPaymentDocument(entries, primary);
        assertNotNull(document);
        List<AssetPaymentAssetDetail> assetPaymentAssetDetails = document.getAssetPaymentAssetDetail();
        assertEquals(1, assetPaymentAssetDetails.size());
        assertEquals(Long.valueOf(1594), assetPaymentAssetDetails.get(0).getCapitalAssetNumber());
        // assert here
        List<AssetPaymentDetail> assetPaymentDetails = document.getSourceAccountingLines();
        assertEquals(2, assetPaymentDetails.size());
        if ("1023200".equals(assetPaymentDetails.get(0).getAccountNumber())) {
            assertAssetPaymentDetail(document, assetPaymentDetails.get(0), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
            assertAssetPaymentDetail(document, assetPaymentDetails.get(1), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
        }
        else {
            assertAssetPaymentDetail(document, assetPaymentDetails.get(0), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
            assertAssetPaymentDetail(document, assetPaymentDetails.get(1), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
        }
    }
}
