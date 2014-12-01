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
public class CashIncreaseDocumentRuleValidationsForBatchProcess {
    private static final Logger LOG = Logger.getLogger(CashIncreaseDocumentRuleValidationsForBatchProcess.class);

    private CashIncreaseDocumentRules rule;
    
    public CashIncreaseDocumentRuleValidationsForBatchProcess() {
        rule = new CashIncreaseDocumentRules();
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
