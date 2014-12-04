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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.RequisitionAccount;
import org.kuali.kfs.module.purap.businessobject.RequisitionItem;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum RequisitionAccountingLineFixture {
    BASIC_REQ_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_LINE3 // AccountingLineFixture
    ),BASIC_REQ_ACCOUNT_2(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_LINE1 // AccountingLineFixture
    ), PERFORMANCE_ACCOUNT(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_PERFORMANCE_LINE // AccountingLineFixture
    ), APO_REQ_ACCOUNT_1(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE1 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_2(PurApAccountingLineFixture.ACCOUNT_50_PERCENT, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE2 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_3(PurApAccountingLineFixture.ACCOUNT_50_PERCENT, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE3 // AccountingLineFixture
    ), APO_REQ_ACCOUNT_4(PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE4 // AccountingLineFixture
    ), REQ_ACCOUNT_NEGATIVE_AMOUNT (PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.PURAP_LINE_NEGATIVE_AMT // AccountingLineFixture
    ), REQ_ACCOUNT_MULTI_QUANTITY(PurApAccountingLineFixture.REQ_ACCOUNT_MULTI, // PurApAccountingLineFixture
            AccountingLineFixture.REQ_ACCOUNT_MULTI_QUANTITY // AccountingLineFixture
    ), REQ_ACCOUNT_MULTI_NON_QUANTITY(PurApAccountingLineFixture.REQ_ACCOUNT_MULTI, // PurApAccountingLineFixture
            AccountingLineFixture.REQ_ACCOUNT_MULTI_NON_QUANTITY // AccountingLineFixture
    ), APO_ACCOUNT_VALID_CAPITAL_ASSET_OBJECT_CODE (PurApAccountingLineFixture.BASIC_ACCOUNT_1, // PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE2, // AccountingLineFixture
            "7001" // objectCode
    ), APO_ACCOUNT_VALID_CAPITAL_ASSET_OBJECT_CODE_50_PERCENT (PurApAccountingLineFixture.ACCOUNT_50_PERCENT, //PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE4, // AccountingLineFixture
            "7001" // objectCode
    ), APO_ACCOUNT_VALID_EXPENSE_OBJECT_CODE_50_PERCENT (PurApAccountingLineFixture.ACCOUNT_50_PERCENT, //PurApAccountingLineFixture
            AccountingLineFixture.APO_LINE4, // AccountingLineFixture
            "5000" // objectCode
    ),;

    private PurApAccountingLineFixture purApAccountingLineFixture;
    private AccountingLineFixture accountingLineFixture;
    private String objectCode;

    private RequisitionAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
    }

    private RequisitionAccountingLineFixture(PurApAccountingLineFixture purApAccountingLineFixture, AccountingLineFixture accountingLineFixture, String objectCode) {
        this.purApAccountingLineFixture = purApAccountingLineFixture;
        this.accountingLineFixture = accountingLineFixture;
        this.objectCode = objectCode;
    }

    public PurApAccountingLine createPurApAccountingLine(Class clazz, PurApAccountingLineFixture puralFixture, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;
        // TODO: what should this debit code really be
        line = puralFixture.createPurApAccountingLine(RequisitionAccount.class, alFixture);
        if (StringUtils.isNotBlank(objectCode)) {
            line.setFinancialObjectCode(objectCode);
            line.refreshReferenceObject("objectCode");
        }

        return line;
    }

    public void addTo(RequisitionItem item) {
        PurApAccountingLine purApAccountingLine = createPurApAccountingLine(item.getAccountingLineClass(), purApAccountingLineFixture, accountingLineFixture);
        //fix item reference
        purApAccountingLine.setPurapItem(item);
        // fix amount
        purApAccountingLine.setAmount(item.calculateExtendedPrice().multiply(new KualiDecimal(purApAccountingLine.getAccountLinePercent())).divide(new KualiDecimal(100)));
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
        // purApaccountFixture.createPurApAccountingLine(RequisitionAccount.class, alFixture);
        if (0 == 0) {
            ;
        }
    }

}
