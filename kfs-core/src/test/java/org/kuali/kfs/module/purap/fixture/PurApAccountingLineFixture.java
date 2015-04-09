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

import java.math.BigDecimal;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum PurApAccountingLineFixture {
    BASIC_ACCOUNT_1(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
            new KualiDecimal(10) // alternateAmountForGLEntryCreation;
    ), BASIC_ACCOUNT_2(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), ACCOUNT_50_PERCENT(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("50"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), ACCOUNT_ONE_THIRD(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("33.33"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), ACCOUNT_ONE_THIRD_PLUS_ONE_HUNDREDTH(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("33.34"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), REQ_ACCOUNT_MULTI(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), BAD_ACCOUNT_PERCENT_TOO_HIGH(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("101"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), BAD_ACCOUNT_PERCENT_ZERO(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("0"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), BAD_ACCOUNT_PERCENT_NEGATIVE(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("-1"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ); 
    
    private Integer accountIdentifier;
    private Integer itemIdentifier;
    private BigDecimal accountLinePercent;
    private KualiDecimal alternateAmountForGLEntryCreation;

    private PurApAccountingLineFixture(Integer accountIdentifier, Integer itemIdentifier, BigDecimal accountLinePercent, KualiDecimal alternateAmountForGLEntryCreation) {
        this.accountIdentifier = accountIdentifier;
        this.itemIdentifier = itemIdentifier;
        this.accountLinePercent = accountLinePercent;
        this.alternateAmountForGLEntryCreation = alternateAmountForGLEntryCreation;
    }

    public PurApAccountingLine createPurApAccountingLine(Class clazz, AccountingLineFixture alFixture) {
        PurApAccountingLine line = null;
        try {
            // TODO: what should this debit code really be
            line = (PurApAccountingLine) alFixture.createAccountingLine(clazz, KFSConstants.GL_DEBIT_CODE);
        }
        catch (InstantiationException e) {
            throw new RuntimeException("item creation failed. class = " + clazz);
        }
        catch (IllegalAccessException e) {
            throw new RuntimeException("item creation failed. class = " + clazz);
        }
        line.setSequenceNumber(0);
        line.setAccountIdentifier(this.accountIdentifier);
        line.setItemIdentifier(this.itemIdentifier);
        line.setAccountLinePercent(this.accountLinePercent);
        line.setAlternateAmountForGLEntryCreation(this.alternateAmountForGLEntryCreation);
        line.refreshNonUpdateableReferences();
        return line;
    }

}
