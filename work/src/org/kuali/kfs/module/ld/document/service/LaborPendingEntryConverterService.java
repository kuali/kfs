/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.service;

import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class provides a set of facilities that can convert the accounting document and its accounting lines into labor pending
 * entries
 */
public interface LaborPendingEntryConverterService {
    /**
     * convert the given document and accounting line into the expense pending entries
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @return a set of expense pending entries
     */
    public abstract LaborLedgerPendingEntry getExpensePendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
    
    /**
     * convert the given document and accounting line into the expense pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @return a set of expense pending entries for effort reporting
     */
    public abstract LaborLedgerPendingEntry getExpenseA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
    
    /**
     * convert the given document and accounting line into the expense reversal pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @return a set of expense reversal pending entries for effort reporting
     */
    public abstract LaborLedgerPendingEntry getExpenseA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
    
    /**
     * convert the given document and accounting line into the benefit pending entries
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given fringe benefit object code
     * @return a set of benefit pending entries
     */
    public abstract LaborLedgerPendingEntry getBenefitPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode);
    
    /**
     * convert the given document and accounting line into the benefit pending entry for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given fringe benefit object code
     * @return a set of benefit pending entries for effort reporting
     */
    public abstract LaborLedgerPendingEntry getBenefitA21PendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode);
    
    /**
     * convert the given document and accounting line into the benefit reversal pending entries for effort reporting
     * 
     * @param document the given accounting document
     * @param accountingLine the given accounting line
     * @param sequenceHelper the given sequence helper
     * @param benefitAmount the given benefit amount
     * @param fringeBenefitObjectCode the given fringe benefit object code
     * @return a set of benefit reversal pending entries for effort reporting
     */
    public abstract LaborLedgerPendingEntry getBenefitA21ReversalPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, KualiDecimal benefitAmount, String fringeBenefitObjectCode);
    
    /**
     * convert the given document into benefit clearing pending entries with the given account, chart, amount and benefit type
     * 
     * @param document the given accounting document
     * @param sequenceHelper the given sequence helper
     * @param accountNumber the given account number that the benefit clearing amount can be charged
     * @param chartOfAccountsCode the given chart of accounts code that the benefit clearing amount can be charged
     * @param benefitTypeCode the given benefit type code
     * @param clearingAmount the benefit clearing amount
     * @return a set of benefit clearing pending entries
     */
    public abstract LaborLedgerPendingEntry getBenefitClearingPendingEntry(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String accountNumber, String chartOfAccountsCode, String benefitTypeCode, KualiDecimal clearingAmount);
    
    /**
     * construct a LaborLedgerPendingEntry object based on the information in the given document and accounting line. The object can
     * be used as a template
     * 
     * @param document the given document
     * @param accountingLine the given accounting line
     * @return a LaborLedgerPendingEntry object based on the information in the given document and accounting line
     */
    public abstract LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document, ExpenseTransferAccountingLine accountingLine);
    
    /**
     * construct a LaborLedgerPendingEntry object based on the information in the given document. The object can be used as a
     * template
     * 
     * @param document the given document
     * @return a LaborLedgerPendingEntry object based on the information in the given document
     */
    public abstract LaborLedgerPendingEntry getDefaultPendingEntry(LaborLedgerPostingDocument document);
    
    /**
     * construct a LaborLedgerPendingEntry object based on the information in the given document and accounting line. The object can
     * be used as a template
     * 
     * @param document the given document
     * @param accountingLine the given accounting line
     * @return a LaborLedgerPendingEntry object based on the information in the given document and accounting line
     */
    public abstract LaborLedgerPendingEntry getSimpleDefaultPendingEntry();



 




}
