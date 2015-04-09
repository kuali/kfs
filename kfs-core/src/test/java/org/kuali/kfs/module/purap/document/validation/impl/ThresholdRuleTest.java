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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.ThresholdFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class ThresholdRuleTest extends PurapRuleTestBase {

    private ThresholdRule thresholdRule;

    public ThresholdRuleTest() {
        super();
    }

    @Override
    protected void setUp()
    throws Exception {
        super.setUp();
        thresholdRule = new ThresholdRule();
    }

    public final void testThreshold_InvalidDescription(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE);
        doc.getDocumentHeader().setDocumentDescription(null);
        assertFalse(thresholdRule.processSaveDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.documentHeader.documentDescription"));
    }

    public final void testThreshold_ChartOfAccount(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccount_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertTrue(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.chartOfAccountsCode"));
    }

    public final void testThreshold_ChartOfAccountAndAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ACCOUNTTYPE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndSubAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_SUBACCOUNTTYPE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndSubAccountType_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_SUBACCOUNTTYPE_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertTrue(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.subFundGroupCode"));

    }

    public final void testThreshold_ChartOfAccountAndAccountTypeAndSubAccountType(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ACCOUNTTYPE_AND_SUBACCOUNTTYPE);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertFalse(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(2, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.accountTypeCode"));
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.subFundGroupCode"));
    }

    public final void testThreshold_ChartOfAccountAndCommodityCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_COMMODITYCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndCommodityCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_COMMODITYCODE_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertTrue(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.purchasingCommodityCode"));
    }

    public final void testThreshold_ChartOfAccountAndObjectCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_OBJECTCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndObjectCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_OBJECTCODE_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertTrue(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.financialObjectCode"));
    }

    public final void testThreshold_ChartOfAccountAndOrgCode(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ORGCODE);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndOrgCode_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_ORGCODE_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertTrue(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.organizationCode"));
    }

    public final void testThreshold_ChartOfAccountAndVendorNumber(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_VENDORNUMBER);
        assertTrue(thresholdRule.processSaveDocument(doc));
    }

    public final void testThreshold_ChartOfAccountAndVendorNumber_Invalid(){
        MaintenanceDocumentBase doc = getMaintenanceDocument(ThresholdFixture.CHARTCODE_AND_VENDORNUMBER_INVALID);
        assertTrue(thresholdRule.processSaveDocument(doc));
        assertFalse(thresholdRule.processRouteDocument(doc));
        assertTrue(GlobalVariables.getMessageMap().hasErrors());
        assertEquals(1, GlobalVariables.getMessageMap().getErrorCount());
        assertTrue(GlobalVariables.getMessageMap().getErrorMessages().containsKey("document.newMaintainableObject.vendorNumber"));
    }

    private MaintenanceDocumentBase getMaintenanceDocument(ThresholdFixture thresholdFixture){
        MaintenanceDocumentBase doc = null;
        try {
            doc = (MaintenanceDocumentBase) SpringContext.getBean(DocumentService.class).getNewDocument(PurapConstants.RECEIVING_THRESHOLD_DOCUMENT_TYPE);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        doc.getDocumentHeader().setDocumentDescription("JUnit test document");
        Maintainable maintainableDoc = doc.getNewMaintainableObject();
        maintainableDoc.setBusinessObject(thresholdFixture.getThresholdBO());
        return doc;
    }

}

