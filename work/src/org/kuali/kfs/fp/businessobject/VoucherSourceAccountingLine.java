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
package org.kuali.module.financial.bo;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;


/**
 * Special case <code>{@link SourceAccountingLine}</code> type for
 * <code>{@link org.kuali.module.financial.document.VoucherDocument}</code>
 */
public class VoucherSourceAccountingLine extends SourceAccountingLine {

    /**
     * Constructs a VoucherSourceAccountingLine.java.
     */
    public VoucherSourceAccountingLine() {
        super();

        // default is debit. This is important for single sided accounting lines (example: JV w/BB) so that
        // totals get calculated correctly
        this.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
    }
}