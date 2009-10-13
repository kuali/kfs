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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class PurchasingServiceTestDontRunThisYet extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

//    public void testSetupCAMSItems() {
//        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
//        requisition.setCapitalAssetSystemTypeCode("IND");
//        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
//        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
//        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
//        
//        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
//        List<PurchasingCapitalAssetItem> afterFirstCall = requisition.getPurchasingCapitalAssetItems();
//    
//        RequisitionItem item2 = (RequisitionItem)ObjectUtils.deepCopy(requisition.getItem(0));
//        item2.setItemIdentifier(null);
//        requisition.addItem(item2);
//        
//        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
//        List<PurchasingCapitalAssetItem> afterSecondCall = requisition.getPurchasingCapitalAssetItems();
//        assertTrue(afterSecondCall.size() == 2);
//        
//        for (PurchasingCapitalAssetItem camsItem : afterSecondCall) {
//            assertTrue(camsItem.getPurchasingCapitalAssetSystem() != null);
//        }
//        
//    }
//
//    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
//    public void testDeleteCAMSItems() {
//        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        requisition.setCapitalAssetSystemTypeCode("IND");
//        requisition.getDocumentHeader().setDocumentDescription("ct unit testDeleteCAMSItems()");
//        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
//        item1.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
//        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
//        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
//        item1.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
//        item1.getSourceAccountingLine(0).setSubAccountNumber(null);    
//        
//        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);    
//
//        RequisitionItem item2 = (RequisitionItem)RequisitionItemFixture.REQ_ITEM_NO_APO.createRequisitionItem();
//        item2.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
//        item2.getSourceAccountingLine(0).setAccountNumber("0212001");
//        item2.getSourceAccountingLine(0).setFinancialObjectCode("7099");
//        item2.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
//        item2.getSourceAccountingLine(0).setSubAccountNumber(null);  
//        
//        requisition.addItem(item2);
//
//        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
//
//        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);
//        
//        //now do the deletion
//        SpringContext.getBean(PurchasingService.class).deleteCapitalAssetItems(requisition, requisition.getItem(0).getItemIdentifier());
//        
//        List<PurchasingCapitalAssetItem> afterDeletion = requisition.getPurchasingCapitalAssetItems();
//     
//        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);
//        assertTrue(afterDeletion.size() == 1);
//        
//        for (PurchasingCapitalAssetItem camsItem : afterDeletion) {
//            assertTrue(camsItem.getPurchasingCapitalAssetSystem() != null);
//        }
//    }
//    
//    public void testSetupCAMSSystem() {
//        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
//        requisition.getDocumentHeader().setDocumentDescription("ct unit testDeleteCAMSItems()");
//        requisition.setCapitalAssetSystemTypeCode("ONE");
//        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(requisition);
//        
//        assertTrue(requisition.getPurchasingCapitalAssetSystems().size() == 1);
//    }
//    
//    public void testCABModuleServiceIndividualNewRequisitionValidation() {
//        Integer requisitionId = new Integer(1012);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        //The capitalAssetSystems is supposed to be null in the INDIVIDUAL system type.
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateIndividualCapitalAssetSystemFromPurchasing("NEW", capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//
//    public void testCABModuleServiceIndividualModRequisitionValidation() {
//        Integer requisitionId = new Integer(1013);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        //The capitalAssetSystems is supposed to be null in the INDIVIDUAL system type.
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateIndividualCapitalAssetSystemFromPurchasing("MOD", capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//    
//    public void testCABModuleServiceOneSystemNewRequisitionValidation() {
//        Integer requisitionId = new Integer(1004);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        List<CapitalAssetSystem> capitalAssetSystems = document.getPurchasingCapitalAssetSystems();
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateOneSystemCapitalAssetSystemFromPurchasing("NEW", capitalAssetSystems, capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//
//    public void testCABModuleServiceOneSystemModRequisitionValidation() {
//        Integer requisitionId = new Integer(1014);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        List<CapitalAssetSystem> capitalAssetSystems = document.getPurchasingCapitalAssetSystems();
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateOneSystemCapitalAssetSystemFromPurchasing("MOD", capitalAssetSystems, capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//    
//    public void testCABModuleServiceMultipleSystemNewRequisitionValidation() {
//        Integer requisitionId = new Integer(1010);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        List<CapitalAssetSystem> capitalAssetSystems = document.getPurchasingCapitalAssetSystems();
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateMultipleSystemsCapitalAssetSystemFromPurchasing("NEW", capitalAssetSystems, capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//    
//    public void testCABModuleServiceMultipleSystemModRequisitionValidation() {
//        Integer requisitionId = new Integer(1015);
//        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
//        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
//        List<CapitalAssetSystem> capitalAssetSystems = document.getPurchasingCapitalAssetSystems();
//        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateMultipleSystemsCapitalAssetSystemFromPurchasing("MOD", capitalAssetSystems, capitalAssetItems, "EA", "REQUISITION");
//        assertFalse(result);
//    }
//    
//    public void testGetValueFromAvailabilityMatrix() {
//        String ttOneNew = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getValueFromAvailabilityMatrix("capitalAssetTransactionType", "ONE", "NEW");
//        assertEquals(ttOneNew, "EACH");
//        String ttIndMod = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getValueFromAvailabilityMatrix("capitalAssetTransactionType", "IND", "MOD");
//        assertEquals(ttIndMod, "EACH");
//        String assetNumberOneNew = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getValueFromAvailabilityMatrix("itemCapitalAssets.capitalAssetNumber", "ONE", "NEW");
//        assertEquals(assetNumberOneNew, "NONE");
//        String blaOneNew = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getValueFromAvailabilityMatrix("assetNumber", "ONE", "NEW");
//        assertEquals(blaOneNew, null);
//    }

}
