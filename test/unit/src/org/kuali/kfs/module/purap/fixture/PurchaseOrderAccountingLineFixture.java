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

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderAccount;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for Purchase Order Accounting Line.
 */
public enum PurchaseOrderAccountingLineFixture {
    BASIC_PO_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_LINE1 // AccountingLineFixture
    ),
    PO_APO_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE1 // AccountingLineFixture
    );

    private PurApAccountingLineFixture purApAccountingLineFixture;
    private AccountingLineFixture accountingLineFixture;

    /**
     * Private Constructor.
     */
    private PurchaseOrderAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
    }

    /**
     * Creates a PurAp Accounting Line using the specified PurAp Accounting Line Fixture and Accounting Line Fixture.
     *
     * @param clazz the Purchase Order Account class.
     * @param puralFixture the specified PurAp Accounting Line Fixture.
     * @param alFixture the specified Accounting Line Fixture.
     * @return the created PurAp Accounting Line.
     */
    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;
        line = puralFixture.createPurApAccountingLine(PurchaseOrderAccount.class, alFixture);
        return line;
    }

    /**
     * Creates a PurAp Accounting Line from this fixture and adds it to the specified item.
     *
     * @param item the specified item.
     */
    public void addTo(PurchaseOrderItem item) {
        PurApAccountingLine purApAccountingLine = createPurApAccountingLine(item.getAccountingLineClass(), purApAccountingLineFixture, accountingLineFixture);
        purApAccountingLine.setPurapItem(item);
        // fix amount
        purApAccountingLine.setAmount(item.calculateExtendedPrice().multiply(new KualiDecimal(purApAccountingLine.getAccountLinePercent())).divide(new KualiDecimal(100)));
        item.getSourceAccountingLines().add(purApAccountingLine);
    }
}
