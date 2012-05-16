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
package org.kuali.kfs.sys.document;

import java.sql.Date;
import java.util.List;

import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This is the FinancialDocument interface. The TransactionalDocument interface should extend this. It represents any document that
 * exists within the Financial Transactions module, but isn't transactional (i.e. no accounting lines). This interface was put in
 * place to facilitate the CashManagementDocument which is a Financial Transaction module document, but doesn't have accounting
 * lines.
 */
public interface AccountingDocument extends GeneralLedgerPostingDocument, GeneralLedgerPendingEntrySource {
    /**
     * This method is used to return the title that a transactional document should give to it's source accounting line section.
     *
     * @return The source accounting line section's title.
     */
    public String getSourceAccountingLinesSectionTitle();

    /**
     * This method is used to return the title that a transactional document should give to it's source accounting line section.
     *
     * @return The target accounting line section's title.
     */
    public String getTargetAccountingLinesSectionTitle();

    /**
     * Sums up the amounts of all of the target accounting lines.
     */
    public KualiDecimal getTargetTotal();

    /**
     * Sums up the amounts of all of the source accounting lines.
     */
    public KualiDecimal getSourceTotal();


    /**
     * @return AccountingLineParser instance appropriate for importing AccountingLines for this document type
     */
    public AccountingLineParser getAccountingLineParser();

    /*
     * @return Class of the document's source accounting lines
     */
    public Class getSourceAccountingLineClass();


    /*
     * @return Class of the document's target accounting lines
     */
    public Class getTargetAccountingLineClass();

    /*
     * @return Name of the document's source accounting lines
     */
    public String getSourceAccountingLineEntryName();


    /*
     * @return Name of the document's target accounting lines
     */
    public String getTargetAccountingLineEntryName();

    /**
     * Retrieves the next line sequence number for an accounting line in the Source accounting line section on a transactional
     * document.
     *
     * @return The next available source line number.
     */
    public Integer getNextSourceLineNumber();

    /**
     * @param nextLineNumber
     */
    public void setNextSourceLineNumber(Integer nextLineNumber);

    /**
     * Retrieves the next line sequence number for an accounting line in the Target accounting line section on a transactional
     * document.
     *
     * @return The next available target line number.
     */
    public Integer getNextTargetLineNumber();

    /**
     * @param nextLineNumber
     */
    public void setNextTargetLineNumber(Integer nextLineNumber);

    /**
     * This method adds a source accounting line.
     *
     * @param line
     */
    public void addSourceAccountingLine(SourceAccountingLine line);

    /**
     * This method returns a list of target accounting lines.
     *
     * @return The list of source accounting lines.
     */
    public List getSourceAccountingLines();

    /**
     * This method returns the accounting line at a particular spot in the overall list of accounting lines.
     *
     * @param index
     * @return The source accounting line at the specified index.
     */
    public SourceAccountingLine getSourceAccountingLine(int index);

    /**
     * This method sets the list of source accounting lines for this document.
     *
     * @param sourceLines
     */
    public void setSourceAccountingLines(List sourceLines);

    /**
     * This method adds a target accounting line to the document.
     *
     * @param line
     */
    public void addTargetAccountingLine(TargetAccountingLine line);

    /**
     * This method retrieves all of the target accounting lines associated with this document.
     */
    public List getTargetAccountingLines();

    /**
     * This method retrieves the target accounting line at the specified index.
     *
     * @param index
     * @return The target accounting line at the passed in index.
     */
    public TargetAccountingLine getTargetAccountingLine(int index);

    /**
     * This method sets the list of target accounting lines for this document.
     *
     * @param targetLines
     */
    public void setTargetAccountingLines(List targetLines);


    /**
     * This method returns the Class to use for AccountingLingValuesAllowedValidation.
     */
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation();

    /**
     *This method check the document status to determine whether the document is final/processed or not.
     *@return true if documentFinalOrProcessed otherwise false
     */
    public boolean isDocumentFinalOrProcessed();
}
