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
package org.kuali.kfs.module.cab.document.validation.impl;

import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.PurapRuleConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.document.validation.impl.*;
import org.kuali.kfs.module.purap.fixture.*;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.document.validation.impl.CompositeValidation;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.util.GlobalVariables;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class contains tests of the rule validation methods present in PurchasingDocumentRuleBase that relate to capital assets.
 */
@ConfigureContext(session = UserNameFixture.parke)
public class PurchasingDocumentCapitalAssetRuleTest extends PurapRuleTestBase {

    private Map<String, Validation> validations;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        KNSGlobalVariables.setMessageList(new MessageList());
        validations = SpringContext.getBeansOfType(Validation.class);
    }

    @Override
    protected void tearDown() throws Exception {
        validations = null;
        super.tearDown();
    }

    /**
     * Tests that, if a commodity code is not entered on the item, but the system parameter
     * requires the item to have commodity code, it will give validation error about
     * the commodity code is required.
     *
     * @throws Exception
     */
    public void testMissingCommodityCodeWhenRequired() throws Exception {
        TestUtils.setSystemParameter(RequisitionDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        RequisitionDocumentFixture reqFixture = RequisitionDocumentFixture.REQ_NO_APO_VALID;

        CompositeValidation validation = (CompositeValidation)validations.get("Requisition-newProcessItemValidation");
        RequisitionDocument req = reqFixture.createRequisitionDocument();
        AttributedDocumentEventBase event = new AttributedDocumentEventBase("","", req);

        for(RequisitionItem item : (List<RequisitionItem>)req.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));

        String fieldName = KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.ITEM + "[0]." + PurapPropertyConstants.ITEM_COMMODITY_CODE;
        assertTrue(GlobalVariables.getMessageMap().fieldHasMessage(fieldName, KFSKeyConstants.ERROR_REQUIRED));
        GlobalVariables.getMessageMap().clearErrorMessages();
        TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
        PurchaseOrderDocumentFixture poFixture = PurchaseOrderDocumentFixture.PO_ONLY_REQUIRED_FIELDS;

        validation = (CompositeValidation)validations.get("PurchaseOrder-newProcessItemValidation");
        PurchaseOrderDocument po = poFixture.createPurchaseOrderDocument();
        event = new AttributedDocumentEventBase("","", po);

        for(PurchaseOrderItem item : (List<PurchaseOrderItem>)po.getItems()) {
            event.setIterationSubject(item);
            validation.validate(event);
        }
        assertTrue(GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_REQUIRED));
        assertTrue(GlobalVariables.getMessageMap().fieldHasMessage(fieldName, KFSKeyConstants.ERROR_REQUIRED));

    }

    public void testValidateOneSystemCapitalAssetSystemChartsRequiringParameters() {
        RequisitionDocument requisition = RequisitionDocumentWithCapitalAssetItemsFixture.REQ_VALID_ONE_NEW_CAPITAL_ASSET_ITEM.createRequisitionDocument();
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetItems(requisition);
        SpringContext.getBean(PurchasingService.class).setupCapitalAssetSystem(requisition);

        PurchasingCapitalAssetValidation validation = (PurchasingCapitalAssetValidation)validations.get("Purchasing-capitalAssetValidation-test");
        assertTrue( validation.validate(new AttributedDocumentEventBase("", "", requisition)) );

        //BA is one of the chart code that requires some fields (e.g. comments) to be filled in.
        PurchasingDocument purchasingDocument = requisition;
        purchasingDocument.getItems().get(0).getSourceAccountingLines().get(0).setChartOfAccountsCode("BA");
        assertFalse( "Chart BA should have required comments", validation.validate(new AttributedDocumentEventBase("", "", requisition)) );
    }
}

