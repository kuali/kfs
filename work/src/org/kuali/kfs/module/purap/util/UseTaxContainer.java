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
