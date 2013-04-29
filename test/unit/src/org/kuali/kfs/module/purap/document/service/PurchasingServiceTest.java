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

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentWithCapitalAssetItemsFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionItemFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.ObjectUtils;

@ConfigureContext(session = khuntley)
public class PurchasingServiceTest extends KualiTestBase {


    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {

        super.tearDown();
    }

    public void testSetupCapitalAssetItems() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_IND_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();

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

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public void DISABLED_502_testDeleteCapitalAssetItems() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_IND_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        requisition.getDocumentHeader().setDocumentDescription("From PurchasingServiceTest)");

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        PurchasingItem item2 = RequisitionItemFixture.REQ_ITEM_VALID_CAPITAL_ASSET.createRequisitionItemForCapitalAsset();

        requisition.addItem(item2);

        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);

        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);

        //now do the deletion
        SpringContext.getBean(PurchasingService.class).deleteCapitalAssetItems(requisition, requisition.getItem(0).getItemIdentifier());

        List<PurchasingCapitalAssetItem> afterDeletion = requisition.getPurchasingCapitalAssetItems();

        try {
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(requisition);
        } catch ( ValidationException ex ) {
            fail( "Validation error when saving document without validation: " + dumpMessageMapErrors() );
        }
        assertEquals(1, afterDeletion.size());

        for (PurchasingCapitalAssetItem camsItem : afterDeletion) {
            assertEquals("PurchasingCapitalAssetSystem on " + camsItem + " should not have been null", null,camsItem.getPurchasingCapitalAssetSystem());
        }
    }

    public void testSetupCapitalAssetSystem() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_ONE_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(requisition);
        assertTrue(requisition.getPurchasingCapitalAssetSystems().size() == 1);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = false)
    public final void testDefaultUseTaxIndicatorValue(){

        RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_TAX.createRequisitionDocument();
        requisitionDocument.refreshReferenceObject("vendorDetail");

        requisitionDocument.getVendorDetail().setDefaultAddressStateCode(requisitionDocument.getVendorStateCode());
        boolean defaultUseTaxIndicatorValue = SpringContext.getBean(PurchasingService.class).getDefaultUseTaxIndicatorValue(requisitionDocument);
        assertFalse(defaultUseTaxIndicatorValue);
    }

}
