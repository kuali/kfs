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
package org.kuali.module.labor.batch.poster.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.Message;
import org.kuali.module.labor.rules.TransactionFieldValidator;

/**
 * This class...
 */
public class LaborPosterTransactionValidator implements VerifyTransaction {
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public List<Message> verifyTransaction(Transaction tranaction) {
        TransactionFieldValidator fieldValidator = new TransactionFieldValidator(kualiConfigurationService);
        
        List<Message> messageList = new ArrayList<Message>();
        addMessageIntoList(messageList, fieldValidator.checkUniversityFiscalYear(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkChartOfAccountsCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkAccountNumber(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkSubAccountNumber(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkUniversityFiscalPeriodCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialBalanceTypeCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialObjectCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialSubObjectCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialObjectTypeCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialDocumentTypeCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialDocumentNumber(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkFinancialSystemOriginationCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkTransactionDebitCreditCode(tranaction));
        addMessageIntoList(messageList, fieldValidator.checkTransactionLedgerEntrySequenceNumber(tranaction));
                
        return messageList;
    }
    
    private void addMessageIntoList(List<Message> messageList, Message message){
        if(message != null){
            messageList.add(message);
        }
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * @param kualiConfigurationService The kualiConfigurationService to set.
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
