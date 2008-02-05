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

import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.purap.PurapConstants.CAMSValidationStrings;
import org.kuali.module.purap.fixtures.PurapTestConstants.PurCams;

public enum PurchasingCapitalAssetFixture {
    
    TWO_CAPITAL(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.CAPITAL),
    TWO_EXPENSE(CAMSValidationStrings.EXPENSE,CAMSValidationStrings.EXPENSE),
    CAPITAL_EXPENSE(CAMSValidationStrings.CAPITAL,CAMSValidationStrings.EXPENSE),;
    
    HashSet capitalOrExpenseSet = new HashSet();
    ObjectCode objectCode;
    
    private PurchasingCapitalAssetFixture(String capOrExp1, String capOrExp2) {
        this.capitalOrExpenseSet.add(capOrExp1);
        this.capitalOrExpenseSet.add(capOrExp2);
        this.objectCode = PurCams.OBJECT_CODE;
    }

    public HashSet populateForCapitalAndExpenseCheck() {
        return capitalOrExpenseSet;
    }
    
    public ObjectCode getObjectCode() {
        return this.objectCode;
    }
}
