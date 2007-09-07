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
package org.kuali.module.purap.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.bo.PurApItem;

public class SummaryAccount {

    private SourceAccountingLine account;
    private List<PurApItem> items;
   
    public SummaryAccount() {
        super();
        items = new ArrayList<PurApItem>();
    }

    /**
     * Gets the account attribute. 
     * @return Returns the account.
     */
    public SourceAccountingLine getAccount() {
        return account;
    }

    /**
     * Sets the account attribute value.
     * @param account The account to set.
     */
    public void setAccount(SourceAccountingLine account) {
        this.account = account;
    }

    /**
     * Gets the items attribute. 
     * @return Returns the items.
     */
    public List<PurApItem> getItems() {
        return items;
    }

    /**
     * Sets the items attribute value.
     * @param items The items to set.
     */
    public void setItems(List<PurApItem> items) {
        this.items = items;
    }
    
}
