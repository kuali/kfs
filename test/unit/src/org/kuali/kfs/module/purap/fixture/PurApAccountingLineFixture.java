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
