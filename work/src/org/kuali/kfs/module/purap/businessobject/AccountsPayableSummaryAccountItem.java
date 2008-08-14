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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class AccountsPayableSummaryAccountItem extends PersistableBusinessObjectBase{
    private Integer accountIdentifier;
    private Integer itemLineNumber;
    private KualiDecimal itemAccountTotalAmount;
    
    public AccountsPayableSummaryAccountItem() {
        super();
    }
    public AccountsPayableSummaryAccountItem(Integer accountIdentifier, Integer itemLineNumber, KualiDecimal itemAccountTotalAmount) {
        super();
        this.accountIdentifier = accountIdentifier;
        this.itemLineNumber = itemLineNumber;
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("accountIdentifier", getAccountIdentifier());
        m.put("itemLineNumber", getItemLineNumber());
        m.put("itemAccountTotalAmount", getItemAccountTotalAmount());
        
        return m;
    }
    public Integer getAccountIdentifier() {
        return accountIdentifier;
    }
    public void setAccountIdentifier(Integer accountIdentifier) {
        this.accountIdentifier = accountIdentifier;
    }
    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }
    public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

}
