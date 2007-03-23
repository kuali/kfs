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
import org.kuali.module.labor.util.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class...
 */
@Transactional
public class LaborPosterTransactionValidator implements VerifyTransaction {
    /**
     * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public List<Message> verifyTransaction(Transaction tranaction) {        
        List<Message> messageList = new ArrayList<Message>();
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkUniversityFiscalYear(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkChartOfAccountsCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkAccountNumber(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkSubAccountNumber(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkUniversityFiscalPeriodCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialBalanceTypeCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialObjectCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialSubObjectCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialObjectTypeCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialDocumentTypeCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialDocumentNumber(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialSystemOriginationCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkTransactionDebitCreditCode(tranaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkTransactionLedgerEntrySequenceNumber(tranaction));
                
        return messageList;
    }
}
