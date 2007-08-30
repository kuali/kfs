/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.purap.document;

import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.module.purap.fixtures.RequisitionItemAccountsFixture.WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.document.AccountingDocumentTestUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.PurchasingItem;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.module.purap.fixtures.RequisitionItemAccountsFixture;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.UserNameFixture;

/**
 * This class is used to test InternalBillingDocument.
 * 
 * 
 */
@ConfigureContext(session = KHUNTLEY)
public class RequisitionDocumentTest extends KualiTestBase {
    public static final Class<RequisitionDocument> DOCUMENT_CLASS = RequisitionDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), RequisitionDocument.class);
    }

    private List<RequisitionItemAccountsFixture> getItemParametersFromFixtures() {
        List<RequisitionItemAccountsFixture> list = new ArrayList<RequisitionItemAccountsFixture>();
        list.add(WITH_DESC_WITH_UOM_WITH_PRICE_WITH_ACCOUNT);
        return list;
    }

    private int getExpectedPrePeCount() {
        return 0;
    }

    public final void testAddItem() throws Exception {
        List<PurchasingItem> items = new ArrayList<PurchasingItem>();
        for (RequisitionItem item : generateItems()) {
            items.add(item);
        }
        int expectedItemTotal = items.size();
        PurchasingDocumentTestUtils.testAddItem((PurchasingDocument)DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), items, expectedItemTotal);
    }


    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }


    // test util methods
    private List<RequisitionItem> generateItems() throws Exception {
        List<RequisitionItem> items = new ArrayList<RequisitionItem>();
        // set items to document
        for (RequisitionItemAccountsFixture itemFixture : getItemParametersFromFixtures()) {
            items.add(itemFixture.populateItem());
        }

        return items;
    }

    private RequisitionDocument buildDocument() throws Exception {
        RequisitionDocument document = (RequisitionDocument) getDocumentParameterFixture();

        for (RequisitionItemAccountsFixture itemFixture : getItemParametersFromFixtures()) {
            document.addItem(itemFixture.populateItem());
        }
        document.setRequisitionSourceCode(PurapConstants.RequisitionSources.STANDARD_ORDER);
        document.setStatusCode(PurapConstants.RequisitionStatuses.IN_PROCESS);
        document.setPurchaseOrderCostSourceCode(PurapConstants.POCostSources.ESTIMATE);
        document.setChartOfAccountsCode("BL");
        document.setFundingSourceCode("INST");
        document.setOrganizationAutomaticPurchaseOrderLimit(new KualiDecimal("10000"));
        
        document.setDeliveryCampusCode("BL");
        document.setBillingName("Joe Tester");
        document.setBillingLine1Address("test");
        document.setBillingCityName("test");
        document.setBillingStateCode("IN");
        document.setBillingPostalCode("10101");
        document.setBillingCountryCode("US");
        document.setBillingPhoneNumber("111-111-1111");
        
        document.setDeliveryBuildingName("Administration");
        document.setDeliveryBuildingLine1Address("test");
        document.setDeliveryCityName("test");
        document.setDeliveryStateCode("IU");
        document.setDeliveryPostalCode("10101");
        document.setDeliveryToName("Joe Tester");
        document.setDeliveryBuildingRoomNumber("1");
        
        document.setRequestorPersonName("HUNTLEY,KEISHA Y");
        document.setRequestorPersonPhoneNumber("111-111-1111");
        document.setRequestorPersonEmailAddress("test@test.com");

        document.setPurchaseOrderTransmissionMethodCode(PurapConstants.POTransmissionMethods.PRINT);
        document.setOrganizationCode("ACQU");

        return document;
    }
    

    private void deleteItem(RequisitionDocument document, int index) {
        List items = document.getItems();
        items.remove(index);
        document.setItems(items);
    }

    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }

}
