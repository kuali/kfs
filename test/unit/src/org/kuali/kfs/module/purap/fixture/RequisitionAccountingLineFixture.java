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
package org.kuali.module.purap.fixtures;

import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.RequisitionAccount;
import org.kuali.module.purap.bo.RequisitionItem;
import org.kuali.test.fixtures.AccountingLineFixture;

public enum RequisitionAccountingLineFixture {
    BASIC_REQ_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.LINE2 // AccountingLineFixture
    ),BASIC_REQ_ACCOUNT_2(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.LINE1 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE1 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_2(PurApAccountingLineFixture.ACCOUNT_50_PERCENT, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE2 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_3(PurApAccountingLineFixture.ACCOUNT_50_PERCENT, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE3 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_4(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE4 // AccountingLineFixture
    ), REQ_ACCOUNT_MULTI_QUANTITY(PurApAccountingLineFixture.REQ_ACCOUNT_MULTI, // PurApAccountingLineFixture
            AccountingLineFixture.REQ_ACCOUNT_MULTI_QUANTITY // AccountingLineFixture
    ), REQ_ACCOUNT_MULTI_NON_QUANTITY(PurApAccountingLineFixture.REQ_ACCOUNT_MULTI, // PurApAccountingLineFixture
            AccountingLineFixture.REQ_ACCOUNT_MULTI_NON_QUANTITY // AccountingLineFixture
    );

    private PurApAccountingLineFixture purApAccountingLineFixture;
    private AccountingLineFixture accountingLineFixture;

    private RequisitionAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
    }

    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;

        // TODO: what should this debit code really be
        line = (PurApAccountingLine) puralFixture.createPurApAccountingLine(RequisitionAccount.class, alFixture);

        return line;
    }

    public void addTo(RequisitionItem item) {
        item.getSourceAccountingLines().add(createPurApAccountingLine(item.getAccountingLineClass(), purApAccountingLineFixture, accountingLineFixture));
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
        // purApaccountFixture.createPurApAccountingLine(RequisitionAccount.class, alFixture);
        if (0 == 0)
            ;
    }

}
