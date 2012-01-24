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
package org.kuali.kfs.module.cab;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.ObjectSubType;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.cab.businessobject.AssetTransactionType;
import org.kuali.kfs.module.purap.fixture.PurchasingCapitalAssetFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class PurApItemValidationTest extends MaintenanceRuleTestBase {

    private CapitalAssetBuilderModuleService cabModuleService;
    
    protected void setUp() throws Exception {
        super.setUp();
        KNSGlobalVariables.setMessageList(new MessageList());
        if( null == cabModuleService ) {
            cabModuleService = SpringContext.getBean(CapitalAssetBuilderModuleService.class);
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    // Tests of validateAccountingLinesNotCapitalAndExpense
    
//    /**
//     * Tests that, if two object codes of Capital Asset level have been processed, the
//     * rule will be passed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_TwoCapital() {
//       HashSet<String> set = PurchasingCapitalAssetFixture.TWO_CAPITAL.populateForCapitalAndExpenseCheck();
//       ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_CAPITAL.getObjectCode();
//       assertTrue(cabModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));
//    }
//    
//    /**
//     * Tests that, if two object codes of a level that is not Capital Asset have been processed,
//     * the rule will be passed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_TwoExpense() {
//        HashSet<String> set = PurchasingCapitalAssetFixture.TWO_EXPENSE.populateForCapitalAndExpenseCheck();
//        ObjectCode objectCode = PurchasingCapitalAssetFixture.TWO_EXPENSE.getObjectCode();
//        assertTrue(cabModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));       
//    }
//    
//    /**
//     * Tests that, if an object code with a level of Capital Asset has been processed together 
//     * with an object code not of a level of Capital Asset, then the rule will be failed.
//     */
//    public void testValidateAccountingLinesNotCapitalAndExpense_CapitalExpense() {
//        HashSet<String> set = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.populateForCapitalAndExpenseCheck();
//        ObjectCode objectCode = PurchasingCapitalAssetFixture.CAPITAL_EXPENSE.getObjectCode();
//        assertFalse(cabModuleService.validateAccountingLinesNotCapitalAndExpense(set, "1", objectCode));
//    }
//    
    // Tests of validateLevelCapitalAssetIndication
    
    @SuppressWarnings("deprecation")
    private ObjectCode getObjectCodeWithLevel(PurchasingCapitalAssetFixture fixture, String levelCode) {
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectLevel objLevel = new ObjectLevel();
        objLevel.setFinancialObjectLevelCode(levelCode);
        objectCode.setFinancialObjectLevel(objLevel);
        objectCode.setFinancialObjectLevelCode(levelCode);
        return objectCode;
    }
    
//    /**
//     * Tests that the rule will be failed if given an extended price above the
//     * threshold for capital assets, and an object code whose level should be among those listed in 
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter.
//     */  
//    public void testValidateLevelCapitalAssetIndication_HappyPath() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
//        assertFalse(cabModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//    
//    /**
//     * Tests that the rule will be passed if given an extended price above the
//     * threshold for capital assets, but an object code whose level should not be among those listed in 
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate a definite capital asset.
//     */
//    public void testValidateLevelCapitalAssetIndication_CapCodeLevel() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "CAP");
//        assertTrue(cabModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//    
//    /**
//     * Tests that the rule will be passed if given an extended price above the
//     * threshold for capital assets, but an object code whose level should not be among those listed in 
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, but should indicate an expense.
//     */
//    public void testValidateLevelCapitalAssetIndication_ExpenseCodeLevel() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "DEBT");      
//        assertTrue(cabModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }    
//
//    
//    /**
//     * Tests that the rule will be passed if given a an extended price below the
//     * threshold for capital assets, but an object code whose level should be among those listed in 
//     * the POSSIBLE_CAPITAL_ASSET_OBJECT_LEVELS parameter, due to the lower price.
//     */
//    public void testValidateLevelCapitalAssetIndication_NonCapitalPrice() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE;
//        KualiDecimal itemQuantity = fixture.getQuantity();
//        BigDecimal itemUnitPrice = fixture.getItemUnitPrice();
//        ObjectCode objectCode = getObjectCodeWithLevel(fixture, "S&E");
//        assertTrue(cabModuleService.validateLevelCapitalAssetIndication(itemUnitPrice, objectCode, "1"));
//    }
//        
    // Tests of validateObjectCodeVersusTransactionType
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_Passing() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        //TODO: uncomment this when data in MDS jira fixed
//        assertTrue(cabModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedCombination() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("AM");
        financialObjectSubType.setFinancialObjectSubTypeName("Arts and Museums");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("FABR"); // "Fabrication"
        //TODO: uncomment this when data in MDS jira fixed
//        assertFalse(cabModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }
    
    @SuppressWarnings("deprecation")
    public void testValidateObjectCodeVersusTransactionType_NotIncludedSubtype() {
        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE;
        ObjectCode objectCode = fixture.getObjectCode();
        ObjectSubType financialObjectSubType = new ObjectSubType();
        objectCode.setFinancialObjectSubTypeCode("BI");
        financialObjectSubType.setFinancialObjectSubTypeName("Bond Issuance");
        objectCode.setFinancialObjectSubType(financialObjectSubType);
        AssetTransactionType tranType = new AssetTransactionType();
        tranType.setCapitalAssetTransactionTypeCode("NEW");
        //TODO: uncomment this when data in MDS jira fixed
//        assertFalse(cabModuleService.validateObjectCodeVersusTransactionType(objectCode, tranType, false, "1", true));
    }
    
    // Tests of validateCapitalAssetTransactionTypeVersusRecurrence
    
//    /**
//     * Tests that, if the rule is given a recurring payment type and a non-recurring transaction type,
//     * the rule will fail.
//     */
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(cabModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//    
//    /**
//     * Tests that, if the rule is given no payment type, and a non-recurring transaction type, the rule will pass.
//     */
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NonRecurringTranTypeAndNoRecurringPaymentType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertTrue(cabModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//    
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_NoTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_NO_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(cabModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//    
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.RECURRING_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertTrue(cabModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }
//    
//    public void testValidateCapitalAssetTransactionTypeVersusRecurrence_RecurringTranTypeAndNoRecurringPaymentType() {
//        PurchasingCapitalAssetFixture fixture = PurchasingCapitalAssetFixture.NO_PAYMENT_TYPE_RECURRING_TRAN_TYPE;
//        CapitalAssetBuilderAssetTransactionType tranType = fixture.getCapitalAssetBuilderAssetTransactionType();
//        RecurringPaymentType recurringPaymentType = fixture.getRecurringPaymentType();
//        assertFalse(cabModuleService.validateCapitalAssetTransactionTypeVersusRecurrence(tranType, recurringPaymentType, "1"));
//    }   
//    
}

