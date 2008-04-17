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
package org.kuali.module.purap.document;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapRuleConstants;
import org.kuali.module.purap.fixtures.RequisitionDocumentFixture;
import org.kuali.module.purap.fixtures.RequisitionDocumentWithCommodityCodeFixture;
import org.kuali.module.purap.service.RequisitionService;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.RelatesTo;
import org.kuali.test.suite.RelatesTo.JiraIssue;

/**
 * This class is used to create and test populated Requisition Documents
 * that are not eligible to become APO. 
 * One of the ineligibility criterias is if the APO Limit is null.
 * However, currently there is no known way to simulate this criteria
 * yet, so we'll skip this particular criteria for now. 
 * All the other criterias for APO ineligibility are already added in this class.
 */
@ConfigureContext(session = KHUNTLEY)
public class NegativeAPOTest extends KualiTestBase {

    private RequisitionDocument requisitionDocument = null;

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        requisitionDocument = null;
        super.tearDown();
    }

     //Requisition total > APO Limit.
     public void testInvalidAPOTotalGreaterThanAPOLimitNoRouting() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_TOTAL_GREATER_THAN_APO_LIMIT);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }
     }
     
     //Requisition total <= 0.
     public void testInvalidAPOTotalNotGreaterThanZero() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_TOTAL_NOT_GREATER_THAN_ZERO.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));      
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_TOTAL_NOT_GREATER_THAN_ZERO);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }
     }
     
     //Requisition contains restricted item.
     public void testInvalidAPORequisitionContainsRestrictedItem() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_CONTAINS_RESTRICTED_ITEM.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));       
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_RESTRICTED_ITEM);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }         
     }
     
     //Vendor was not selected from the vendor database.
     public void testInvalidAPOVendorNotSelected() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));    
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_VENDOR_NOT_SELECTED_FROM_VENDOR_DATABASE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }             
     }
     
     //Error retrieving vendor from database.
     public void testInvalidAPOVendorNotInDB() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_ERROR_RETRIEVING_VENDOR_FROM_DATABASE.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));                 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_ERROR_RETRIEVING_VENDOR_FROM_DATABASE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //Selected Vendor has restricted indicator equals to true.
     public void testInvalidAPOVendorIsRestricted() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_WITH_RESTRICTED_VENDOR.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));   
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_SELECTED_VENDOR_IS_RESTRICTED);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //Payment type is marked as recurring.
     public void testInvalidAPOPaymentTypeRecurring() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_PAYMENT_TYPE_RECURRING.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));      
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_PAYMENT_TYPE_IS_RECURRING);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     //PO Total Limit is not null and not zero.
     public void testInvalidAPOPOTotalLimitNonzero() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_PO_TOTAL_LIMIT_NON_ZERO.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument)); 
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_PO_TOTAL_LIMIT_IS_NOT_EMPTY);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
     }
     
     @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=false)
     public void testInvalidAPOHasAlternateVendorName() throws Exception {
         RequisitionDocument requisitionDocument = RequisitionDocumentFixture.REQ_APO_INVALID_ALTERNATE_VENDOR_NAMES.createRequisitionDocument();
         final String docId = requisitionDocument.getDocumentNumber();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));        
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_ALTERNATE_VENDOR_NAMES);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }           
     }
     
     public void testInvalidAPOHasInactiveCommodityCode() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_APO_INACTIVE_COMMODITY_CODE;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_CONTAINS_INACTIVE_COMMODITY_CODE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "N");
     }
     
     public void testInvalidAPOHasCommodityCodeWithRestrictedMaterial() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         RequisitionDocumentWithCommodityCodeFixture fixture = RequisitionDocumentWithCommodityCodeFixture.REQ_APO_COMMODITY_CODE_WITH_RESTRICTED_MATERIAL;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_COMMODITY_CODE_WITH_RESTRICTED_MATERIAL);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "N");
     }
     
     public void testInvalidAPOMissingCommodityCode() throws Exception {
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "Y");
         //This fixture is intentionally used because it is an otherwise valid fixture for APO, except
         //that it does not contain commodity code, which should fail the APO criteria.
         RequisitionDocumentFixture fixture = RequisitionDocumentFixture.REQ_APO_VALID;
         RequisitionDocument requisitionDocument = fixture.createRequisitionDocument();
         requisitionDocument.refreshReferenceObject("vendorDetail");
         assertFalse(SpringContext.getBean(RequisitionService.class).isAutomaticPurchaseOrderAllowed(requisitionDocument));
         if (requisitionDocument.getBoNotes() != null && requisitionDocument.getBoNotes().size() > 0) {
             String reason = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.NON_APO_REQUISITION_MISSING_COMMODITY_CODE);
             assertTrue(requisitionDocument.getBoNote(0).getNoteText().indexOf(reason) >=0);
         }  
         TestUtils.setSystemParameter(PurchaseOrderDocument.class, PurapRuleConstants.ITEMS_REQUIRE_COMMODITY_CODE_IND, "N");
     }
     
}
