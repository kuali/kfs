/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.PurapConstants.CAMSValidationStrings;
import org.kuali.module.purap.bo.CapitalAssetTransactionType;
import org.kuali.module.purap.bo.PurchasingItemCapitalAsset;
import org.kuali.module.purap.bo.RecurringPaymentType;
import org.kuali.module.purap.fixtures.PurapTestConstants.PurCams;

public enum PurchasingCapitalAssetFixture {
    
    TWO_CAPITAL(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.CAPITAL),
    TWO_EXPENSE(CAMSValidationStrings.EXPENSE,CAMSValidationStrings.EXPENSE),
    CAPITAL_EXPENSE(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.EXPENSE),
    
    POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.CAPITAL_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.EXPENSE_OBJECT_CODE),
    ZERO_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.ZERO_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NEGATIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.NEGATIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NULL_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(null,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.POSITIVE_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    
    RECURRING_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE(PurCams.RECURRING_PAYMENT_TYPE, PurCams.NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE),
    NO_PAYMENT_TYPE_NONRECURRING_TRAN_TYPE(null, PurCams.NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE),
    RECURRING_PAYMENT_TYPE_NO_TRAN_TYPE(PurCams.RECURRING_PAYMENT_TYPE, null),
    RECURRING_PAYMENT_TYPE_RECURRING_TRAN_TYPE(PurCams.RECURRING_PAYMENT_TYPE, PurCams.RECURRING_TRAN_TYPE),
    NO_PAYMENT_TYPE_RECURRING_TRAN_TYPE(null, PurCams.RECURRING_TRAN_TYPE),
    
    ASSET_NUMBER_REQUIRING_TRAN_TYPE_ONE_ASSET(PurCams.NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE,PurCams.ASSET_NUMBER_1),
    ASSET_NUMBER_REQUIRING_TRAN_TYPE_NO_ASSETS(PurCams.NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE),
    ASSET_NUMBER_REQUIRING_TRAN_TYPE_TWO_ASSETS(PurCams.NONRECURRING_ASSET_NUMBER_REQUIRING_TRAN_TYPE,PurCams.ASSET_NUMBER_1,PurCams.ASSET_NUMBER_2),
    NONASSET_NUMBER_REQUIRING_TRAN_TYPE_NO_ASSETS(PurCams.NONRECURRING_NON_ASSET_NUMBER_REQUIRING_TRAN_TYPE),
    NONASSET_NUMBER_REQUIRING_TRAN_TYPE_ONE_ASSET(PurCams.NONRECURRING_NON_ASSET_NUMBER_REQUIRING_TRAN_TYPE,PurCams.ASSET_NUMBER_1),
    ;
    
    HashSet capitalOrExpenseSet = new HashSet();
    ObjectCode objectCode;
    KualiDecimal quantity;
    KualiDecimal extendedPrice;
    CapitalAssetTransactionType capitalAssetTransactionType = null;
    RecurringPaymentType recurringPaymentType = null;
    List<PurchasingItemCapitalAsset> assets = null;
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateAccountingLinesNotCapitalAndExpense.
     * 
     * @param capOrExp1
     * @param capOrExp2
     * @see PurchasingDocumentRuleBase.validateAccountingLinesNotCapitalAndExpense
     */
    private PurchasingCapitalAssetFixture(String capOrExp1, String capOrExp2) {
        this.capitalOrExpenseSet.add(capOrExp1);
        this.capitalOrExpenseSet.add(capOrExp2);
        this.objectCode = PurCams.EXPENSE_OBJECT_CODE; //Used in these tests only for display of errors.
    }
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateLevelCapitalAssetIndication.
     * 
     * @param quantity
     * @param extendedPrice
     * @param objectCode
     * @see PurchasingDocumentRuleBase.validateLevelCapitalAssetIndication
     */
    private PurchasingCapitalAssetFixture(KualiDecimal quantity, KualiDecimal extendedPrice, ObjectCode objectCode) {
        this.quantity = quantity;
        this.extendedPrice = extendedPrice;
        this.objectCode = objectCode;
    }
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateCapitalAssetTransactionTypeVersusRecurrence
     * 
     * @param recurringPaymentType
     * @param capitalAssetTransactionTypeCode
     * @see PurchasingDocumentRuleBase.validateCapitalAssetTransactionTypeVersusRecurrence
     */
    private PurchasingCapitalAssetFixture(RecurringPaymentType recurringPaymentType, CapitalAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
        this.recurringPaymentType = recurringPaymentType;
    }
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateCapitalAssetNumberRequirements.
     * Note the variable capitalAssetNumbers argument.
     * 
     * @param capitalAssetTransactionType       A CapitalAssetTransactionType
     * @param capitalAssetNumbers               A variable argument of Longs.
     * @see PurchasingDocumentRuleBase.validateCapitalAssetNumberRequirements
     */
    private PurchasingCapitalAssetFixture(CapitalAssetTransactionType capitalAssetTransactionType, Long... capitalAssetNumbers) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
        if ( capitalAssetNumbers != null ) {
            this.assets = new ArrayList();
            for( Long capitalAssetNumber : capitalAssetNumbers ) {
                this.assets.add(new PurchasingItemCapitalAsset(capitalAssetNumber));
            }
        }
    }

    public HashSet populateForCapitalAndExpenseCheck() {
        return capitalOrExpenseSet;
    }

    public HashSet getCapitalOrExpenseSet() {
        return capitalOrExpenseSet;
    }

    public void setCapitalOrExpenseSet(HashSet capitalOrExpenseSet) {
        this.capitalOrExpenseSet = capitalOrExpenseSet;
    }

    public KualiDecimal getExtendedPrice() {
        return extendedPrice;
    }

    public void setExtendedPrice(KualiDecimal extendedPrice) {
        this.extendedPrice = extendedPrice;
    }

    public ObjectCode getObjectCode() {
        return objectCode;
    }

    public void setObjectCode(ObjectCode objectCode) {
        this.objectCode = objectCode;
    }

    public KualiDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(KualiDecimal quantity) {
        this.quantity = quantity;
    }

    public List<PurchasingItemCapitalAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<PurchasingItemCapitalAsset> assets) {
        this.assets = assets;
    }

    public CapitalAssetTransactionType getCapitalAssetTransactionType() {
        return capitalAssetTransactionType;
    }

    public void setCapitalAssetTransactionType(CapitalAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
    }

    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }
    
    
}
