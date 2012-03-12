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
package org.kuali.kfs.module.purap.fixture;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderAssetTransactionType;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants.ValidationStrings;
import org.kuali.kfs.module.purap.businessobject.RecurringPaymentType;
import org.kuali.kfs.module.purap.fixture.PurapTestConstants.PurCams;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurchasingCapitalAssetFixture {
    
    TWO_CAPITAL(ValidationStrings.CAPITAL,ValidationStrings.CAPITAL),
    TWO_EXPENSE(ValidationStrings.EXPENSE,ValidationStrings.EXPENSE),
    CAPITAL_EXPENSE(ValidationStrings.CAPITAL,ValidationStrings.EXPENSE),
    
    POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.CAPITAL_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.EXPENSE_OBJECT_CODE),
    ZERO_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.ZERO_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NEGATIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.NEGATIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NULL_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(null,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_POSITIVE_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    
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
    
    HashSet<String> capitalOrExpenseSet = new HashSet<String>();
    ObjectCode objectCode;
    KualiDecimal quantity;
    BigDecimal itemUnitPrice;
    CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType = null;
    RecurringPaymentType recurringPaymentType = null;
    List<ItemCapitalAsset> assets = null;
    
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
    private PurchasingCapitalAssetFixture(KualiDecimal quantity, BigDecimal itemUnitPrice, ObjectCode objectCode) {
        this.quantity = quantity;
        this.itemUnitPrice = itemUnitPrice;
        this.objectCode = objectCode;
    }
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateCapitalAssetTransactionTypeVersusRecurrence
     * 
     * @param recurringPaymentType
     * @param capitalAssetTransactionTypeCode
     * @see PurchasingDocumentRuleBase.validateCapitalAssetTransactionTypeVersusRecurrence
     */
    private PurchasingCapitalAssetFixture(RecurringPaymentType recurringPaymentType, CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = (CapitalAssetBuilderAssetTransactionType)capitalAssetTransactionType;
        this.recurringPaymentType = recurringPaymentType;
    }
    
    /**
     * Constructor used for tests of PurchasingDocumentRuleBase.validateCapitalAssetNumberRequirements.
     * Note the variable capitalAssetNumbers argument.
     * 
     * @param capitalAssetTransactionType       A CapitalAssetBuilderAssetTransactionType
     * @param capitalAssetNumbers               A variable argument of Longs.
     * @see PurchasingDocumentRuleBase.validateCapitalAssetNumberRequirements
     */
    private PurchasingCapitalAssetFixture(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType, Long... capitalAssetNumbers) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
        if ( capitalAssetNumbers != null ) {
            this.assets = new ArrayList<ItemCapitalAsset>();
            for( Long capitalAssetNumber : capitalAssetNumbers ) {
                //TODO: Fix this
                //this.assets.add(new PurchasingItemCapitalAssetBase(capitalAssetNumber));
            }
        }
    }

    public HashSet<String> populateForCapitalAndExpenseCheck() {
        return capitalOrExpenseSet;
    }

    public HashSet<String> getCapitalOrExpenseSet() {
        return capitalOrExpenseSet;
    }

    public void setCapitalOrExpenseSet(HashSet<String> capitalOrExpenseSet) {
        this.capitalOrExpenseSet = capitalOrExpenseSet;
    }

    public BigDecimal getItemUnitPrice() {
        return itemUnitPrice;
    }

    public void setItemUnitPrice(BigDecimal itemUnitPrice) {
        this.itemUnitPrice = itemUnitPrice;
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

    public List<ItemCapitalAsset> getAssets() {
        return assets;
    }

    public void setAssets(List<ItemCapitalAsset> assets) {
        this.assets = assets;
    }

    public CapitalAssetBuilderAssetTransactionType getCapitalAssetBuilderAssetTransactionType() {
        return capitalAssetTransactionType;
    }

    public void setCapitalAssetBuilderAssetTransactionType(CapitalAssetBuilderAssetTransactionType capitalAssetTransactionType) {
        this.capitalAssetTransactionType = capitalAssetTransactionType;
    }

    public RecurringPaymentType getRecurringPaymentType() {
        return recurringPaymentType;
    }

    public void setRecurringPaymentType(RecurringPaymentType recurringPaymentType) {
        this.recurringPaymentType = recurringPaymentType;
    }
    
    
}
