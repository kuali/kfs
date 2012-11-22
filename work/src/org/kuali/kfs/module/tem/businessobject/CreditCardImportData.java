/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.ArrayList;
import java.util.List;

public class CreditCardImportData {
    private String importBy;
    private List<CreditCardStagingData> creditCardData;

    public CreditCardImportData(){
        creditCardData = new ArrayList<CreditCardStagingData>();
    }

    public void setImportBy(String argImportBy){
        importBy = argImportBy;
    }
    public String getImportBy(){
        return importBy;
    }

    public void seetCreditCardData(List<CreditCardStagingData> argCreditCardData){
        creditCardData = argCreditCardData;
    }

    public List<CreditCardStagingData> getCreditCardData(){
        return creditCardData;
    }

    /**
     *
     * This method adds the {@link CreditCardStagingData} to the list.
     * @param creditCard
     */
    public void addCreditCard(CreditCardStagingData creditCard) {
        this.getCreditCardData().add(creditCard);
    }
}
