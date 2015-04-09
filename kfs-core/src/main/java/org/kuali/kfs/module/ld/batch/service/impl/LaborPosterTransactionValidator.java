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
