/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.document;

import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.document.GeneralLedgerPostingDocument;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.PendingLedgerEntry;

/**
 * Interface for Expense Transfer Documents
 */
public interface LaborExpenseTransferDocument extends GeneralLedgerPostingDocument {
    public String getEmplid();
    public void setEmplid(String emplid);

    /**
     * @see org.kuali.kfs.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    public void addExpenseTransferSourceAccountingLine(ExpenseTransferSourceAccountingLine line);

    /**
     * @see org.kuali.kfs.document.AccountingDocument#addTargetAccountingLine(TargetAccountingLine)
     */
    public void addExpenseTransferTargetAccountingLine(ExpenseTransferTargetAccountingLine line);

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLine(int)
     */
    public ExpenseTransferSourceAccountingLine getExpenseTransferSourceAccountingLine(int index);

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLine(int)
     */
    public ExpenseTransferTargetAccountingLine getExpenseTransferTargetAccountingLine(int index);

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
    public void addSourceAccountingLine(ExpenseTransferSourceAccountingLine line);

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
    public ExpenseTransferSourceAccountingLine getSourceAccountingLine(int index);

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
    public void addTargetAccountingLine(ExpenseTransferTargetAccountingLine line);

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
    public ExpenseTransferTargetAccountingLine getTargetAccountingLine(int index);

    /**
     * This method sets the list of target accounting lines for this document.
     * 
     * @param targetLines
     */
    public void setTargetAccountingLines(List targetLines);
   
    /**
     * This method retrieves the list of Labor Ledgre Pending Entries for the document.
     * 
     * @return A list of labor ledger pending entries.
     */
    public List<PendingLedgerEntry> getLaborLedgerPendingEntries();

    /**
     * This method sets the list of labor ledger pending entries for the document.
     * 
     * @param laborLedgerPendingEntries the given labor ledger pending entries
     */
    public void setLaborLedgerPendingEntries(List<PendingLedgerEntry> laborLedgerPendingEntries);

    /**
     * Get the pending entry with the given index in the list of labor ledger pending entries
     * @param index the given index
     * @return the pending entry with the given index in the list of labor ledger pending entries
     */
    public PendingLedgerEntry getLaborLedgerPendingEntry(int index);

}

