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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurityBase;
import org.kuali.rice.kns.util.KualiDecimal;

public enum EndowmentTransactionLineFixture {
    // Endowment Transaction Line Fixture
    ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD(new Integer(1), //transactionLineNumber
            "TESTKEMID", //kemid
            "P", //transactionIPIndicatorCode 
            new KualiDecimal("100.20"), // transactionAmount
            false, // corpusIndicator
            false // linePosted
    );
    
    public final Integer transactionLineNumber;
    public final String kemid;
    public final String transactionIPIndicatorCode;
    public final KualiDecimal transactionAmount;
    public final boolean corpusIndicator;
    public final boolean linePosted;

    private EndowmentTransactionLineFixture(Integer transactionLineNumber,
                                            String kemid, String transactionIPIndicatorCode,
                                            KualiDecimal transactionAmount,
                                            boolean corpusIndicator, boolean linePosted) {
        this.transactionLineNumber = transactionLineNumber;
        this.kemid = kemid;
        this.transactionIPIndicatorCode = transactionIPIndicatorCode;
        this.transactionAmount = transactionAmount;
        this.corpusIndicator = corpusIndicator;
        this.linePosted = linePosted;
    }

    /**
     * This method creates a Endowment Transaction Line record
     * @return endowmentTransactionLine
     */
    public EndowmentTransactionLineBase createEndowmentTransactionLine(boolean isSource, Integer transactionLineNumber,
                                                                       String kemid, String transactionIPIndicatorCode,
                                                                       KualiDecimal transactionAmount,
                                                                       boolean corpusIndicator, boolean linePosted) {
        EndowmentTransactionLineBase endowmentTransactionLine = null;
        
        if (isSource) {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentSourceTransactionLine();
        }
        else {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentTargetTransactionLine();
        }
        
        endowmentTransactionLine.setTransactionLineNumber(this.transactionLineNumber);
        endowmentTransactionLine.setKemid(this.kemid);
        endowmentTransactionLine.setTransactionIPIndicatorCode(this.transactionIPIndicatorCode);
        endowmentTransactionLine.setTransactionAmount(this.transactionAmount);
        endowmentTransactionLine.setCorpusIndicator(this.corpusIndicator);
        endowmentTransactionLine.setLinePosted(this.linePosted);

        endowmentTransactionLine.refreshNonUpdateableReferences();
        
        return endowmentTransactionLine;
    }
    
    /**
     * This method creates a Endowment Transaction Line record
     * @return endowmentTransactionLine
     */
    public EndowmentTransactionLineBase createEndowmentTransactionLine(boolean isSource) {
        EndowmentTransactionLineBase endowmentTransactionLine = null;
        
        if (isSource) {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentSourceTransactionLine();
        }
        else {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentTargetTransactionLine();
        }
        
        endowmentTransactionLine.setTransactionLineNumber(this.transactionLineNumber);
        endowmentTransactionLine.setKemid(this.kemid);
        endowmentTransactionLine.setTransactionIPIndicatorCode(this.transactionIPIndicatorCode);
        endowmentTransactionLine.setTransactionAmount(this.transactionAmount);
        endowmentTransactionLine.setCorpusIndicator(this.corpusIndicator);
        endowmentTransactionLine.setLinePosted(this.linePosted);

        endowmentTransactionLine.refreshNonUpdateableReferences();
        
        return endowmentTransactionLine;
    }
}