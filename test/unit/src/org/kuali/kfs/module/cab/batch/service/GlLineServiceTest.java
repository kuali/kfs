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

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.kfs.module.cab.document.service.GlLineService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetGlobalDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.kns.util.KualiDecimal;

public class GlLineServiceTest extends KualiTestBase {
    private GlLineService glLineService;
    private BusinessObjectService businessObjectService;
    private GeneralLedgerEntry primary;
    private List<GeneralLedgerEntry> entries;

    @Override
    @ConfigureContext(session = UserNameFixture.khuntley, shouldCommitTransactions = false)
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
        assertEquals(CamsConstants.ACQUISITION_TYPE_CODE_N, assetGlobal.getAcquisitionTypeCode());
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
        assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(0), "1023200", new KualiDecimal(5200.50), Integer.valueOf(1));
        assertAssetPaymentDetail(assetGlobalDocument, assetPaymentDetails.get(1), "1031400", new KualiDecimal(3300.50), Integer.valueOf(2));
    }

    private void assertAssetPaymentDetail(MaintenanceDocument assetGlobalDocument, AssetPaymentDetail detail, String accountNumber, KualiDecimal amount, Integer seqNo) {
        assertEquals(assetGlobalDocument.getDocumentNumber(), detail.getDocumentNumber());
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
}
