/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.purap.businessobject.PurApSummaryItem;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

/**
 * 
 * Summary Account.
 * This is a special class used to display summary information i.e. what items are associated
 * with this account. 
 */
public class SummaryAccount {

    private SourceAccountingLine account;
    private List<PurApSummaryItem> items;

    /**
     * Constructs a Summary Account
     */
    public SummaryAccount() {
        super();
        items = new ArrayList<PurApSummaryItem>();
    }

    public SummaryAccount(SourceAccountingLine account) {
        super();
        setAccount(account);
        items = new ArrayList<PurApSummaryItem>();
    }

    public SourceAccountingLine getAccount() {
        return account;
    }

    public void setAccount(SourceAccountingLine account) {
        this.account = account;
    }

    public List<PurApSummaryItem> getItems() {
        return items;
    }

    public void setItems(List<PurApSummaryItem> items) {
        this.items = items;
    }

}
