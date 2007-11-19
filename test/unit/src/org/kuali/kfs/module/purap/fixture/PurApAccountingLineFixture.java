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

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.purap.bo.PurApAccountingLine;
import org.kuali.test.fixtures.AccountingLineFixture;

public enum PurApAccountingLineFixture {
    BASIC_ACCOUNT_1(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), BASIC_ACCOUNT_2(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), ACCOUNT_50_PERCENT(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("50"), // accountLinePercent;
            null // alternateAmountForGLEntryCreation;
    ), REQ_ACCOUNT_MULTI(null, // accountIdentifier;
            null, // itemIdentifier;
            new BigDecimal("100"), // accountLinePercent;
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
        line.setAccountIdentifier(this.accountIdentifier);
        line.setItemIdentifier(this.itemIdentifier);
        line.setAccountLinePercent(this.accountLinePercent);
        line.setAlternateAmountForGLEntryCreation(this.alternateAmountForGLEntryCreation);
        line.refreshNonUpdateableReferences();
        return line;
    }

}
