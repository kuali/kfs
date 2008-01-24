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

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.kfs.util.Message;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborTransaction;
import org.kuali.module.labor.rules.TransactionFieldValidator;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a validator for the transactions processed by Labor Poster.
 */
@Transactional
public class LaborPosterTransactionValidator implements VerifyTransaction {
    
    /**
     * @see org.kuali.module.gl.batch.poster.VerifyTransaction#verifyTransaction(org.kuali.module.gl.bo.Transaction)
     */
    public List<Message> verifyTransaction(Transaction transaction) {
        List<Message> messageList = new ArrayList<Message>();

        LaborTransaction laborTransaction = (LaborTransaction) transaction;

        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkUniversityFiscalYear(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkChartOfAccountsCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkAccountNumber(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkSubAccountNumber(laborTransaction, LaborConstants.ANNUAL_CLOSING_DOCUMENT_TYPE_CODE));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkUniversityFiscalPeriodCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialBalanceTypeCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialObjectCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialSubObjectCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialObjectTypeCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialDocumentTypeCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialDocumentNumber(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkFinancialSystemOriginationCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkTransactionDebitCreditCode(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkTransactionLedgerEntrySequenceNumber(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkEmplid(laborTransaction));

        return messageList;
    }
}
