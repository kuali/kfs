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
package org.kuali.kfs.module.purap.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

@ConfigureContext(session = KHUNTLEY)
public class PurchasingServiceTestDontRunThisYet extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    public void testSetupCAMSItems() {
        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_APO_VALID.createRequisitionDocument();
        requisition.setCapitalAssetSystemTypeCode("IND");
        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
        List<PurchasingCapitalAssetItem> afterFirstCall = requisition.getPurchasingCapitalAssetItems();
    
        RequisitionItem item2 = (RequisitionItem)ObjectUtils.deepCopy(requisition.getItem(0));
        item2.setItemIdentifier(null);
        requisition.addItem(item2);
        
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
        List<PurchasingCapitalAssetItem> afterSecondCall = requisition.getPurchasingCapitalAssetItems();
        assertTrue(afterSecondCall.size() == 2);
        
        for (PurchasingCapitalAssetItem camsItem : afterSecondCall) {
            assertTrue(camsItem.getPurchasingCapitalAssetSystem() != null);
        }
        
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public void testDeleteCAMSItems() {
        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.setCapitalAssetSystemTypeCode("IND");
        requisition.getDocumentHeader().setDocumentDescription("ct unit testDeleteCAMSItems()");
        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
        item1.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        item1.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
        item1.getSourceAccountingLine(0).setSubAccountNumber(null);    
        
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);    

        RequisitionItem item2 = (RequisitionItem)RequisitionItemFixture.REQ_ITEM_NO_APO.createRequisitionItem();
        item2.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
        item2.getSourceAccountingLine(0).setAccountNumber("0212001");
        item2.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        item2.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
        item2.getSourceAccountingLine(0).setSubAccountNumber(null);  
        
        requisition.addItem(item2);

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        SpringContext.getBean(PurchasingService.class).saveDocumentWithoutValidation(requisition);
        
        //now do the deletion
        SpringContext.getBean(PurchasingService.class).deleteCapitalAssetItems(requisition, requisition.getItem(0).getItemIdentifier());
        
        List<PurchasingCapitalAssetItem> afterDeletion = requisition.getPurchasingCapitalAssetItems();
     
        SpringContext.getBean(PurchasingService.class).saveDocumentWithoutValidation(requisition);
        assertTrue(afterDeletion.size() == 1);
        
        for (PurchasingCapitalAssetItem camsItem : afterDeletion) {
            assertTrue(camsItem.getPurchasingCapitalAssetSystem() != null);
        }
    }
    
    public void testSetupCAMSSystem() {
        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.getDocumentHeader().setDocumentDescription("ct unit testDeleteCAMSItems()");
        requisition.setCapitalAssetSystemTypeCode("ONE");
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(requisition);
        
        assertTrue(requisition.getPurchasingCapitalAssetSystems().size() == 1);
    }
    
    public void testCABModuleServiceIndividualValidation() {
        Map fieldValues = new HashMap();
        fieldValues.put("capitalAssetSystemIdentifier", 3);  
        List<CapitalAssetSystem> capitalAssetSystems = (List<CapitalAssetSystem>)SpringContext.getBean(BusinessObjectService.class).findMatching(RequisitionCapitalAssetSystem.class, fieldValues);
        RequisitionCapitalAssetSystem system = (RequisitionCapitalAssetSystem)capitalAssetSystems.get(0);
        Integer requisitionId = system.getPurapDocumentIdentifier();
        RequisitionDocument document = SpringContext.getBean(RequisitionService.class).getRequisitionById(requisitionId);
        List<PurchasingCapitalAssetItem> capitalAssetItems = document.getPurchasingCapitalAssetItems();
        boolean result = SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateIndividualCapitalAssetSystemFromPurchasing("NEW", capitalAssetSystems, capitalAssetItems, "EA", "REQUISITION");
        assertFalse(result);
    }
}
