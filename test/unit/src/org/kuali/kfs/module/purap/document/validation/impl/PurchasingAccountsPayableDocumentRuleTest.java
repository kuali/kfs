/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.parke;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.validation.PurapRuleTestBase;
import org.kuali.kfs.module.purap.fixture.PurApAccountingLineFixture;
import org.kuali.kfs.module.purap.fixture.PurApItemFixture;
import org.kuali.kfs.module.purap.fixture.PurapAccountingServiceFixture;
import org.kuali.kfs.module.purap.fixture.PurchaseOrderAccountingLineFixture;
import org.kuali.kfs.module.purap.fixture.RequisitionDocumentFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEventBase;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;

@ConfigureContext(session = parke, shouldCommitTransactions=true)
public class PurchasingAccountsPayableDocumentRuleTest extends
		PurapRuleTestBase {
	
    private Map<String, GenericValidation> validations;
    
    ParameterService parameterService;

    protected void setUp() throws Exception {
        super.setUp();
        validations = SpringContext.getBeansOfType(GenericValidation.class);
        if (null == parameterService) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
    }

    protected void tearDown() throws Exception {
        validations = null;
        super.tearDown();
    }

    // Tests of checkNegativeAccountsValidation
    
    public void testCheckNegativeAccountsValidation_PositiveAmount() {
        RequisitionDocument doc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
        
        PurchasingAccountsPayableCheckNegativeAccountsValidation validation = (PurchasingAccountsPayableCheckNegativeAccountsValidation)validations.get("PurchasingAccountsPayable-checkNegativeAccountsValidation-test");                
        assertTrue( validation.validate(new AttributedDocumentEventBase("","", doc)) );        
    }
    
    public void testCheckNegativeAccountsValidation_NegativeAmount() {
        RequisitionDocument doc = RequisitionDocumentFixture.REQ_WITH_NEGATIVE_AMOUNT.createRequisitionDocument();

        PurchasingAccountsPayableCheckNegativeAccountsValidation validation = (PurchasingAccountsPayableCheckNegativeAccountsValidation)validations.get("PurchasingAccountsPayable-checkNegativeAccountsValidation-test");                
        assertFalse( validation.validate(new AttributedDocumentEventBase("","", doc)) );        
    }   
    
    // Tests of validateBelowTheLineValues
    private void validateBelowTheLineValues_TestHelper(String documentType, PurApItem item, String parameterString) {
        try {
            if (!parameterService.parameterExists(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), 
                    parameterString)) {
                fail("Parameter does not exist:  "+parameterString+" for "+documentType);
            }
            else if (/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(Class.forName(PurapConstants.PURAP_DETAIL_TYPE_CODE_MAP.get(documentType)), 
                    parameterString, item.getItemTypeCode()).evaluationSucceeds()) {
                
                RequisitionDocument doc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
                PurchasingAccountsPayableBelowTheLineValuesValidation validation = (PurchasingAccountsPayableBelowTheLineValuesValidation)validations.get("PurchasingAccountsPayable-belowTheLineValuesValidation");
                item.setPurapDocument(doc);
                validation.setItemForValidation(item);
                assertTrue( validation.validate(new AttributedDocumentEventBase("","", doc)));        
            }
            else {
                RequisitionDocument doc = RequisitionDocumentFixture.REQ_ONLY_REQUIRED_FIELDS.createRequisitionDocument();
                PurchasingAccountsPayableBelowTheLineValuesValidation validation = (PurchasingAccountsPayableBelowTheLineValuesValidation)validations.get("PurchasingAccountsPayable-belowTheLineValuesValidation");
                item.setPurapDocument(doc);
                validation.setItemForValidation(item);
                assertFalse( validation.validate(new AttributedDocumentEventBase("","",doc)) );        
            }
        }
        catch (ClassNotFoundException cnfe) {
            fail(cnfe.toString());
        }        
    }
    
    public void testValidateBelowTheLineValues_Req_Freight_Positive() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.VALID_FREIGHT_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_POSITIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_Req_Freight_Negative() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.NEGATIVE_FREIGHT_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_NEGATIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_Req_Freight_Zero() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.ZERO_FREIGHT_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_ZERO;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_Req_ShippingHandling_Positive() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_POSITIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_Req_ShippingHandling_Negative() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_NEGATIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_Req_ShippingHandling_Zero() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(RequisitionDocument.class);
        PurApItem item = PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM.createPurApItem(RequisitionItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_ZERO;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_Freight_Positive() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.VALID_FREIGHT_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_POSITIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_Freight_Negative() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.NEGATIVE_FREIGHT_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_NEGATIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_Freight_Zero() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.ZERO_FREIGHT_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_ZERO;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_ShippingHandling_Positive() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.VALID_SHIPPING_AND_HANDLING_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_POSITIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_ShippingHandling_Negative() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.NEGATIVE_SHIPPING_AND_HANDLING_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_NEGATIVE;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }
    
    public void testValidateBelowTheLineValues_PREQ_ShippingHandling_Zero() {
        String documentType = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(PaymentRequestDocument.class);
        PurApItem item = PurApItemFixture.ZERO_SHIPPING_AND_HANDLING_ITEM.createPurApItem(PaymentRequestItem.class);
        String parameterString = PurapConstants.ITEM_ALLOWS_ZERO;
        validateBelowTheLineValues_TestHelper(documentType, item, parameterString);
    }    
    
    // Tests of verifyHasAccounts
    
    public void testVerifyHasAccounts_Positive() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();
        PurApAccountingLineFixture purApAcctLineFixture = PurApAccountingLineFixture.BASIC_ACCOUNT_1;
        PurApAccountingLine purApAcctLine1 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine1);
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setSourceAccountingLines(purAccounts);
        
        PurchasingAccountsPayableHasAccountsValidation validation = (PurchasingAccountsPayableHasAccountsValidation)validations.get("PurchasingAccountsPayable-hasAccountsValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );                
    }
    
    public void testVerifyHasAccounts_Negative() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setSourceAccountingLines(purAccounts);
        
        PurchasingAccountsPayableHasAccountsValidation validation = (PurchasingAccountsPayableHasAccountsValidation)validations.get("PurchasingAccountsPayable-hasAccountsValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );                
    }   
    
    // Tests of verifyAccountPercent
    
    public void testVerifyAccountPercent_FiftyFifty() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_TWO_ACCOUNTS;
        PurchasingAccountsPayableDocument doc =  fixture.generateRequisitionDocument_OneItem();
        List<PurApAccountingLine> purAccounts = doc.getItems().get(0).getSourceAccountingLines();
                
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setSourceAccountingLines(purAccounts);
        
        PurchasingAccountsPayableAccountPercentValidation validation = (PurchasingAccountsPayableAccountPercentValidation)validations.get("PurchasingAccountsPayable-accountPercentValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );               
    }
    
    /*public void testVerifyAccountPercent_ThreeWay() {
        PurapAccountingServiceFixture fixture = PurapAccountingServiceFixture.REQ_PRORATION_THIRDS;
        PurchasingAccountsPayableDocument doc =  fixture.generateRequisitionDocument_OneItem();
        List<PurApAccountingLine> purAccounts = doc.getItems().get(0).getSourceAccountingLines();
        assertTrue(rules.verifyAccountPercent(doc, purAccounts, "1"));
    }*/
    
    // Tests of verifyUniqueAccountingStrings
    
    public void testVerifyUniqueAccountingStrings_DifferentStrings() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();
        PurApAccountingLineFixture purApAcctLineFixture = PurApAccountingLineFixture.ACCOUNT_50_PERCENT;
        PurApAccountingLine purApAcctLine1 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine1);
        PurApAccountingLine purApAcctLine2 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE2);      
        purAccounts.add(purApAcctLine2);     
                
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setSourceAccountingLines(purAccounts);
        
        PurchasingAccountsPayableUniqueAccountingStringsValidation validation = (PurchasingAccountsPayableUniqueAccountingStringsValidation)validations.get("PurchasingAccountsPayable-uniqueAccountingStringsValidation");
        validation.setItemForValidation(item);
        assertTrue( validation.validate(null) );                               
    }
    
    public void testVerifyUniqueAccountingStrings_SameStrings() {
        List<PurApAccountingLine> purAccounts = new ArrayList<PurApAccountingLine>();
        PurApAccountingLineFixture purApAcctLineFixture = PurApAccountingLineFixture.ACCOUNT_50_PERCENT;
        PurApAccountingLine purApAcctLine1 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine1);
        PurApAccountingLine purApAcctLine2 = purApAcctLineFixture.createPurApAccountingLine(PaymentRequestAccount.class, 
                AccountingLineFixture.PURAP_LINE1);      
        purAccounts.add(purApAcctLine2);       

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setSourceAccountingLines(purAccounts);
        
        PurchasingAccountsPayableUniqueAccountingStringsValidation validation = (PurchasingAccountsPayableUniqueAccountingStringsValidation)validations.get("PurchasingAccountsPayable-uniqueAccountingStringsValidation");
        validation.setItemForValidation(item);
        assertFalse( validation.validate(null) );
    }
    
    // Tests of verifyAccountingStringsBetween0And100Percent
    
    public void testVerifyAccountingStringsBetween0And100Percent_Positive() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BASIC_ACCOUNT_1,
                AccountingLineFixture.PURAP_LINE1);
       
        PurchasingAccountsPayableAccountPercentBetween0And100Validation validation = (PurchasingAccountsPayableAccountPercentBetween0And100Validation)validations.get("PurchasingAccountsPayable-accountPercentBetween0And100Validation-test");
        validation.setAccountingLine(accountingLine);
        validation.setErrorPropertyName(PurapPropertyConstants.ACCOUNT_LINE_PERCENT);
        validation.setItemIdentifier("1");
        assertTrue( validation.validate(null) );       
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentTooHigh() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_TOO_HIGH,
                AccountingLineFixture.PURAP_LINE1);

        PurchasingAccountsPayableAccountPercentBetween0And100Validation validation = (PurchasingAccountsPayableAccountPercentBetween0And100Validation)validations.get("PurchasingAccountsPayable-accountPercentBetween0And100Validation-test");
        validation.setAccountingLine(accountingLine);
        validation.setErrorPropertyName(PurapPropertyConstants.ACCOUNT_LINE_PERCENT);
        validation.setItemIdentifier("1");
        assertFalse( validation.validate(null) );       
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentZero() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_ZERO,
                AccountingLineFixture.PURAP_LINE1);
        
        PurchasingAccountsPayableAccountPercentBetween0And100Validation validation = (PurchasingAccountsPayableAccountPercentBetween0And100Validation)validations.get("PurchasingAccountsPayable-accountPercentBetween0And100Validation-test");
        validation.setAccountingLine(accountingLine);
        validation.setErrorPropertyName(PurapPropertyConstants.ACCOUNT_LINE_PERCENT);
        validation.setItemIdentifier("1");
        assertFalse( validation.validate(null) );       
    }
    
    public void testVerifyAccountingStringsBetween0And100Percent_PercentNegative() {
        PurApAccountingLine accountingLine = PurchaseOrderAccountingLineFixture.BASIC_PO_ACCOUNT_1.createPurApAccountingLine(
                PurchaseOrderDocument.class,
                PurApAccountingLineFixture.BAD_ACCOUNT_PERCENT_NEGATIVE,
                AccountingLineFixture.PURAP_LINE1);
        
        PurchasingAccountsPayableAccountPercentBetween0And100Validation validation = (PurchasingAccountsPayableAccountPercentBetween0And100Validation)validations.get("PurchasingAccountsPayable-accountPercentBetween0And100Validation-test");
        validation.setAccountingLine(accountingLine);
        validation.setErrorPropertyName(PurapPropertyConstants.ACCOUNT_LINE_PERCENT);
        validation.setItemIdentifier("1");
        assertFalse( validation.validate(null) );       
    }
}
