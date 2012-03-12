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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum EndowmentTransactionLineFixture {
    // Endowment Transaction Line Fixture
    ENDOWMENT_TRANSACTIONAL_LINE_REQUIRED_FIELDS_RECORD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100.20"), // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_AMT(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_ZERO_AMT(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            KualiDecimal.ZERO, // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_ZERO_UNITS(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            KualiDecimal.ZERO, // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_POSITIVE_UNITS(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            KualiDecimal.ZERO, // transactionAmount
            new KualiDecimal("100"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_INCOME(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "I", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_PRINCIPAL(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_EAD_NO_ETRAN_CD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            null,// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_EAD_WITH_ETRAN_CD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    
      ENDOWMENT_TRANSACTIONAL_LINE_ECDD_WITH_ETRAN_CD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "I", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),    
    ENDOWMENT_TRANSACTIONAL_LINE_STD_BASIC(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_EGLT_BASIC(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            null, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_EUSA_BASIC(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECI(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "I", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ), ENDOWMENT_TRANSACTIONAL_LINE_FOR_ECDD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            "TST123",// etranCode
            "I", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_EAI(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            null,// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("100"), // transactionAmount
            new KualiDecimal("10"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_EAD(new Integer(1), // transactionLineNumber
            "TESTKEMID", // kemid
            null,// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("10000"), // transactionAmount
            new KualiDecimal("20"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_ECI(new Integer(1), // transactionLineNumber
            "037B011AG1", // kemid
            "40000",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("600"), // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_ECDD(new Integer(1), // transactionLineNumber
            "032A017014", // kemid
            "40000",// etranCode
            "I", // transactionIPIndicatorCode
            new KualiDecimal("75"), // transactionAmount
            KualiDecimal.ZERO, // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAI_CASH(new Integer(1), // transactionLineNumber
            "038B011179", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("10000"), // transactionAmount
            new KualiDecimal("1000"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAI_NON_CASH(new Integer(1), // transactionLineNumber
            "037A017013", // kemid
            "40000",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("4000"), // transactionAmount
            new KualiDecimal("800"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAD_CASH(new Integer(1), // transactionLineNumber
            "038G003452", // kemid
            "",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("5000"), // transactionAmount
            new KualiDecimal("5200"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    ),
    ENDOWMENT_TRANSACTIONAL_LINE_FOR_POSTING_EDOC_EAD_NON_CASH(new Integer(1), // transactionLineNumber
            "037F002010", // kemid
            "40000",// etranCode
            "P", // transactionIPIndicatorCode
            new KualiDecimal("115.79"), // transactionAmount
            new KualiDecimal("1000"), // transactionUnits
            false, // corpusIndicator
            false // linePosted
    )
    ;

    public final Integer transactionLineNumber;
    public final String kemid;
    private String etranCode;
    public final String transactionIPIndicatorCode;
    public final KualiDecimal transactionAmount;
    private KualiDecimal transactionUnits;
    public final boolean corpusIndicator;
    public final boolean linePosted;

    private EndowmentTransactionLineFixture(Integer transactionLineNumber, String kemid, String etranCode, String transactionIPIndicatorCode, KualiDecimal transactionAmount, KualiDecimal transactionUnits, boolean corpusIndicator, boolean linePosted) {
        this.transactionLineNumber = transactionLineNumber;
        this.kemid = kemid;
        this.etranCode = etranCode;
        this.transactionIPIndicatorCode = transactionIPIndicatorCode;
        this.transactionAmount = transactionAmount;
        this.transactionUnits = transactionUnits;
        this.corpusIndicator = corpusIndicator;
        this.linePosted = linePosted;
    }

    /**
     * This method creates a Endowment Transaction Line record
     * 
     * @return endowmentTransactionLine
     */
    public EndowmentTransactionLineBase createEndowmentTransactionLine(boolean isSource, Integer transactionLineNumber, String kemid, String etranCode, String transactionIPIndicatorCode, KualiDecimal transactionAmount, KualiDecimal transactionUnits, boolean corpusIndicator, boolean linePosted) {
        EndowmentTransactionLineBase endowmentTransactionLine = null;

        if (isSource) {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentSourceTransactionLine();
        }
        else {
            endowmentTransactionLine = (EndowmentTransactionLineBase) new EndowmentTargetTransactionLine();
        }

        endowmentTransactionLine.setTransactionLineNumber(this.transactionLineNumber);
        endowmentTransactionLine.setKemid(this.kemid);
        endowmentTransactionLine.setEtranCode(this.etranCode);
        endowmentTransactionLine.setTransactionIPIndicatorCode(this.transactionIPIndicatorCode);
        endowmentTransactionLine.setTransactionAmount(this.transactionAmount);
        endowmentTransactionLine.setTransactionUnits(transactionUnits);
        endowmentTransactionLine.setCorpusIndicator(this.corpusIndicator);
        endowmentTransactionLine.setLinePosted(this.linePosted);

        endowmentTransactionLine.refreshNonUpdateableReferences();

        return endowmentTransactionLine;
    }

    /**
     * This method creates a Endowment Transaction Line record
     * 
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
        endowmentTransactionLine.setEtranCode(this.etranCode);
        endowmentTransactionLine.setTransactionIPIndicatorCode(this.transactionIPIndicatorCode);
        endowmentTransactionLine.setTransactionAmount(this.transactionAmount);
        endowmentTransactionLine.setTransactionUnits(this.transactionUnits);
        endowmentTransactionLine.setCorpusIndicator(this.corpusIndicator);
        endowmentTransactionLine.setLinePosted(this.linePosted);

        endowmentTransactionLine.refreshNonUpdateableReferences();

        return endowmentTransactionLine;
    }
}