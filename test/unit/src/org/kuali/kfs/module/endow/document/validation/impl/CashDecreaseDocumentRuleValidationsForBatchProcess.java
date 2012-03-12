/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.endow.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class tests the rules in CashDecreaseDocumentRulesTest class
 */
@ConfigureContext(session = kfs)
public class CashDecreaseDocumentRuleValidationsForBatchProcess {
    private static final Logger LOG = Logger.getLogger(CashDecreaseDocumentRuleValidationsForBatchProcess.class);

    private CashDecreaseDocumentRules rule;
    
    public CashDecreaseDocumentRuleValidationsForBatchProcess() {
        rule = new CashDecreaseDocumentRules();
    }
    
    /**
     * call the method validateCashTransactionLine and remove any errors messages
     * from global error map related to validateChartMatch.  This method will always return
     * false or put an error message because there is an inverse FK relation between kemid 
     * and kemidgeneralledgeraccounts table...
     */
    public boolean checkValidateCashTransactionLine(EndowmentTransactionLinesDocument doc, EndowmentTransactionLine line, int lineIndex) {
        String prefix = rule.getErrorPrefix(line, 0);
        
        rule.validateCashTransactionLine(doc, line, lineIndex);
        MessageMap map = GlobalVariables.getMessageMap();
        
        String validateCharFieldKey = prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE;
        map.removeAllErrorMessagesForProperty(validateCharFieldKey);
        
        validateCharFieldKey = prefix + EndowPropertyConstants.TRANSACTION_LINE_ENDOWMENT_TRANSACTION_CODE;
        map.removeAllErrorMessagesForProperty(validateCharFieldKey);
        
        return map.getErrorCount() == 0;
    }
    
    /**
     * IsEndowmentTransactionCodeEmpty that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkIsEndowmentTransactionCodeEmpty(EndowmentTransactionLine line, String prefix) {
        return rule.isEndowmentTransactionCodeEmpty(line, prefix);
    }
    
    /**
     * validateEndowmentTransactionCode that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkValidateEndowmentTransactionCode(EndowmentTransactionLine line, String prefix) {
        return rule.validateEndowmentTransactionCode(line, prefix);
    }
    
    /**
     * validateEndowmentTransactionTypeCode that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkValidateEndowmentTransactionTypeCode(EndowmentTransactionLinesDocument doc, EndowmentTransactionLine line, String prefix) {
        return rule.validateEndowmentTransactionTypeCode(doc, line, prefix);
    }
    
    /**
     * canKEMIDHaveAPrincipalTransaction that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkCanKEMIDHaveAPrincipalTransaction(EndowmentTransactionLine line, String prefix) {
        return rule.canKEMIDHaveAPrincipalTransaction(line, prefix);
    }
    
    /**
     * validateTransactionAmountLessThanZero that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkValidateTransactionAmountLessThanZero(EndowmentTransactionLine line, String prefix) {
        return rule.validateTransactionAmountLessThanZero(line, prefix);
    }
    
    /**
     * validateTransactionAmountGreaterThanZero that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkValidateTransactionAmountGreaterThanZero(EndowmentTransactionLine line, String prefix) {
        return rule.validateTransactionAmountGreaterThanZero(line, prefix);
    }

    /**
     * checkWhetherReducePermanentlyRestrictedFund that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkWhetherReducePermanentlyRestrictedFund(EndowmentTransactionLine line, String prefix) {
        GlobalVariables.getMessageMap().clearErrorMessages();
        
        rule.checkWhetherReducePermanentlyRestrictedFund(line, prefix);
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }

    /**
     * checkWhetherHaveSufficientFundsForCashBasedTransaction that will be called from the batch test
     * ProcessFeeTransactionsServiceImplTest class.
     */
    public boolean checkWhetherHaveSufficientFundsForCashBasedTransaction(EndowmentTransactionLine line, String prefix) {
        GlobalVariables.getMessageMap().clearErrorMessages();
        
        rule.checkWhetherHaveSufficientFundsForCashBasedTransaction(line, prefix);
        return GlobalVariables.getMessageMap().getErrorCount() == 0;
    }
}