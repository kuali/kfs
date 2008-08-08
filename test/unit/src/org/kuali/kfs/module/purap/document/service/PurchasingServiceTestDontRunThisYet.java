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

import java.util.List;

import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionCapitalAssetSystem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.PurchasingCapitalAssetSystemFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

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
        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        
        SpringContext.getBean(PurchasingService.class).setupCAMSItems(requisition);
        List<PurchasingCapitalAssetItem> afterFirstCall = requisition.getPurchasingCapitalAssetItems();
    
        RequisitionItem item2 = (RequisitionItem)ObjectUtils.deepCopy(requisition.getItem(0));
        item2.setItemIdentifier(null);
        requisition.addItem(item2);
        
        SpringContext.getBean(PurchasingService.class).setupCAMSItems(requisition);
        List<PurchasingCapitalAssetItem> afterSecondCall = requisition.getPurchasingCapitalAssetItems();
        assertTrue(afterSecondCall.size() == 2);
        
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = true)
    public void testDeleteCAMSItems() {
        RequisitionDocument requisition = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        requisition.getDocumentHeader().setDocumentDescription("ct unit testDeleteCAMSItems()");
        RequisitionItem item1 = (RequisitionItem)requisition.getItem(0);
        item1.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
        item1.getSourceAccountingLine(0).setAccountNumber("0212001");
        item1.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        item1.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
        item1.getSourceAccountingLine(0).setSubAccountNumber(null);    
        
        SpringContext.getBean(PurchasingService.class).setupCAMSItems(requisition);
    
        RequisitionCapitalAssetSystem sys1 = (RequisitionCapitalAssetSystem)PurchasingCapitalAssetSystemFixture.ASSET_SYSTEM_BASIC_1.createPurchasingCapitalAssetSystem(RequisitionCapitalAssetSystem.class);
        requisition.getPurchasingCapitalAssetSystems().add(sys1);

        requisition.getPurchasingCapitalAssetItems().get(0).setPurchasingCapitalAssetSystem(sys1);
        
        RequisitionItem item2 = (RequisitionItem)RequisitionItemFixture.REQ_ITEM_NO_APO.createRequisitionItem();
        item2.getSourceAccountingLine(0).setChartOfAccountsCode("BL");
        item2.getSourceAccountingLine(0).setAccountNumber("0212001");
        item2.getSourceAccountingLine(0).setFinancialObjectCode("7099");
        item2.getSourceAccountingLine(0).setFinancialSubObjectCode(null);
        item2.getSourceAccountingLine(0).setSubAccountNumber(null);  
        
        requisition.addItem(item2);

        SpringContext.getBean(PurchasingService.class).setupCAMSItems(requisition);
        
        RequisitionCapitalAssetSystem sys2 = (RequisitionCapitalAssetSystem)PurchasingCapitalAssetSystemFixture.ASSET_SYSTEM_BASIC_2.createPurchasingCapitalAssetSystem(RequisitionCapitalAssetSystem.class);
        requisition.getPurchasingCapitalAssetSystems().add(sys2);
        
        requisition.getPurchasingCapitalAssetItems().get(1).setPurchasingCapitalAssetSystem(sys2);
        
        SpringContext.getBean(RequisitionService.class).saveDocumentWithoutValidation(requisition);
        
        //now do the deletion
        SpringContext.getBean(PurchasingService.class).deleteCAMSItems(requisition, requisition.getItem(0).getItemIdentifier());
        
        List<PurchasingCapitalAssetItem> afterDeletion = requisition.getPurchasingCapitalAssetItems();
     
        SpringContext.getBean(RequisitionService.class).saveDocumentWithoutValidation(requisition);
        assertTrue(afterDeletion.size() == 1);
    }
}
