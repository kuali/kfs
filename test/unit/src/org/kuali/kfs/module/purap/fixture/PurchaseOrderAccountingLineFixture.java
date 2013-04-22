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
