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
package org.kuali.kfs.module.purap.fixture;

import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;

public enum CreditMemoAccountingLineFixture {
    BASIC_CM_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.LINE2 // AccountingLineFixture
    );


    private PurApAccountingLineFixture purApAccountingLineFixture;
    private AccountingLineFixture accountingLineFixture;

    private CreditMemoAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
    }

    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;

        // TODO: what should this debit code really be
        line = (PurApAccountingLine) puralFixture.createPurApAccountingLine(CreditMemoAccount.class, alFixture);

        return line;
    }

    public void addTo(CreditMemoItem item) {
        PurApAccountingLine purApAccountingLine = createPurApAccountingLine(item.getAccountingLineClass(), purApAccountingLineFixture, accountingLineFixture);
        purApAccountingLine.setPurapItem(item);
        item.getSourceAccountingLines().add(purApAccountingLine);
    }

    /**
     * This method adds an account to an item
     * 
     * @param document
     * @param purApItemFixture
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public void addTo(PurApItem item, PurApAccountingLineFixture purApaccountFixture, AccountingLineFixture alFixture) throws IllegalAccessException, InstantiationException {
        // purApaccountFixture.createPurApAccountingLine(CreditMemoAccount.class, alFixture);
        if (0 == 0)
            ;
    }

}
