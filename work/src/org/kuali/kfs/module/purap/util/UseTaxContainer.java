/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurApItemBase;
import org.kuali.kfs.module.purap.businessobject.PurApItemUseTax;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * .
 * This is a special class used to hold accounts that are used for use tax and the associated offset
 */
public class UseTaxContainer {

    private PurApItemUseTax useTax;
    private List<SourceAccountingLine> accounts;
    private List<PurApItem> items;
    
    public UseTaxContainer() {
        super();
        accounts = new ArrayList<SourceAccountingLine>();
        items = new ArrayList<PurApItem>();
    }

    public UseTaxContainer(PurApItemUseTax useTax,PurApItem item) {
        super();
        this.useTax = useTax;
        accounts = new ArrayList<SourceAccountingLine>();
        items = new ArrayList<PurApItem>();
        items.add(item);
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

    public List<PurApItem> getItems() {
        return items;
    }

    public void setItems(List<PurApItem> items) {
        this.items = items;
    }

 
}
