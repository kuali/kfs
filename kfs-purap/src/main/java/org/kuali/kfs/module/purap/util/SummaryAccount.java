/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
