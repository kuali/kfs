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
package org.kuali.kfs.module.purap.util;

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * .
 * This is a special class used to hold accounts that are used for use tax and the associated offset
 */
public class UseTaxAccount {

    private PurApItemUseTax useTax;
    private List<SourceAccountingLine> accounts;
    
    public UseTaxAccount() {
        super();
        accounts = new TypedArrayList(SourceAccountingLine.class);
    }

    public UseTaxAccount(PurApItemUseTax useTax) {
        super();
        this.useTax = useTax;
        accounts = new TypedArrayList(SourceAccountingLine.class);
    }
    
    public PurApItemUseTax getUseTax() {
        return useTax;
    }
    public void setUseTax(PurApItemUseTax useTax) {
        this.useTax = useTax;
    }
    public List<SourceAccountingLine> getAccounts() {
        return accounts;
    }
    public void setAccounts(List<SourceAccountingLine> accounts) {
        this.accounts = accounts;
    }

 
}
