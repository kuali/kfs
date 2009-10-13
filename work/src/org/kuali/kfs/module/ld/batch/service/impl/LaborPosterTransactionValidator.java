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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.document.validation.impl.TransactionFieldValidator;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;

/**
 * This class is a validator for the transactions processed by Labor Poster.
 */
public class LaborPosterTransactionValidator implements VerifyTransaction {
    
    /**
     * @see org.kuali.kfs.gl.batch.service.VerifyTransaction#verifyTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public List<Message> verifyTransaction(Transaction transaction) {
        List<Message> messageList = new ArrayList<Message>();
        TransactionFieldValidator transactionFieldValidator = new TransactionFieldValidator();
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
        // Don't need to check SequenceNumber because it sets in each poster (LaborLedgerEntryPoster and LaborGLLedgerEntryPoster), so commented out
        //MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkTransactionLedgerEntrySequenceNumber(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkEmplid(laborTransaction));
        MessageBuilder.addMessageIntoList(messageList, TransactionFieldValidator.checkEncumbranceUpdateCode(laborTransaction));

        return messageList;
    }
}
