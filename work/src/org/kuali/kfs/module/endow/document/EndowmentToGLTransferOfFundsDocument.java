/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.sys.document.AmountTotaling;

public class EndowmentToGLTransferOfFundsDocument extends EndowmentAccountingLinesDocumentBase implements AmountTotaling {

    /**
     * Constructs a EndowmentToGLTransferOfFundsDocument.
     */
    public EndowmentToGLTransferOfFundsDocument() {
        super();
        setTransactionSubTypeCode(EndowConstants.TransactionSubTypeCode.CASH);
        initializeSubType();
    }

    @Override
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceLines) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetLines) {
        // TODO Auto-generated method stub
        
    }
}
