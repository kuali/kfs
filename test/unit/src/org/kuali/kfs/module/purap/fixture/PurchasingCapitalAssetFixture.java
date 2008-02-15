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

import java.util.HashSet;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.PurapConstants.CAMSValidationStrings;
import org.kuali.module.purap.fixtures.PurapTestConstants.PurCams;

public enum PurchasingCapitalAssetFixture {
    
    TWO_CAPITAL(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.CAPITAL),
    TWO_EXPENSE(CAMSValidationStrings.EXPENSE,CAMSValidationStrings.EXPENSE),
    CAPITAL_EXPENSE(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.EXPENSE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_CAP_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.CAPITAL_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_CAPITAL_PRICE_EXPENSE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.EXPENSE_OBJECT_CODE),
    ZERO_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.ZERO_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NEGATIVE_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(PurCams.NEGATIVE_AMOUNT,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    NULL_QUANTIY_CAPITAL_PRICE_POSSIBLE_OBJECT_CODE(null,PurCams.CAPITAL_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    POSITIVE_QUANTITY_POSITIVE_PRICE_POSSIBLE_OBJECT_CODE(PurCams.POSITIVE_AMOUNT,PurCams.POSITIVE_AMOUNT,PurCams.POSSIBLE_OBJECT_CODE),
    ;
    
    HashSet capitalOrExpenseSet = new HashSet();
    ObjectCode objectCode;
    KualiDecimal quantity;
    KualiDecimal extendedPrice;
    String objectCodeSubtypeCode;
    String transactionTypeCode;
    
    private PurchasingCapitalAssetFixture(String capOrExp1, String capOrExp2) {
        this.capitalOrExpenseSet.add(capOrExp1);
        this.capitalOrExpenseSet.add(capOrExp2);
        this.objectCode = PurCams.EXPENSE_OBJECT_CODE; //Used in these tests only for display of errors.
    }
    
    private PurchasingCapitalAssetFixture(KualiDecimal quantity, KualiDecimal extendedPrice, ObjectCode objectCode) {
        this.quantity = quantity;
        this.extendedPrice = extendedPrice;
        this.objectCode = objectCode;
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
    
}
