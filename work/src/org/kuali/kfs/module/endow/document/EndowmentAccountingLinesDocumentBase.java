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
package org.kuali.kfs.module.endow.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;

/**
 * Provides a base class for the Endowment Transfer of Funds documents.
 */
public abstract class EndowmentAccountingLinesDocumentBase extends EndowmentSecurityDetailsDocumentBase implements EndowmentAccountingLinesDocument {
    protected Integer nextSourceAccountingLineNumber;
    protected Integer nextTargetAccountingLineNumber;
    protected List<SourceEndowmentAccountingLine> sourceAccountingLines;
    protected List<TargetEndowmentAccountingLine> targetAccountingLines;

    protected transient FinancialSystemTransactionalDocumentEntry dataDictionaryEntry;

    protected transient Class sourceAccountingLineClass;
    protected transient Class targetAccountingLineClass;

    /**
     * Constructs a EndowmentTransferOfFundsDocument.
     */
    public EndowmentAccountingLinesDocumentBase() {
        super();
        this.nextSourceAccountingLineNumber = new Integer(1);
        this.nextTargetAccountingLineNumber = new Integer(1);
        sourceAccountingLines = new ArrayList<SourceEndowmentAccountingLine>();
        targetAccountingLines = new ArrayList<TargetEndowmentAccountingLine>();
    }


    /**
     * Gets the nextSourceAccountingLineNumber.
     * 
     * @return nextSourceAccountingLineNumber
     */
    public Integer getNextSourceAccountingLineNumber() {
        return nextSourceAccountingLineNumber;
    }

    /**
     * Sets the nextSourceAccountingLineNumber.
     * 
     * @param nextSourceAccountingLineNumber
     */
    public void setNextSourceAccountingLineNumber(Integer nextSourceAccountingLineNumber) {
        this.nextSourceAccountingLineNumber = nextSourceAccountingLineNumber;
    }

    /**
     * Gets the nextTargetAccountingLineNumber.
     * 
     * @return nextTargetAccountingLineNumber
     */
    public Integer getNextTargetAccountingLineNumber() {
        return nextTargetAccountingLineNumber;
    }

    /**
     * Sets the nextTargetAccountingLineNumber.
     * 
     * @param nextTargetAccountingLineNumber
     */
    public void setNextTargetAccountingLineNumber(Integer nextTargetAccountingLineNumber) {
        this.nextTargetAccountingLineNumber = nextTargetAccountingLineNumber;
    }

    /**
     * Gets the sourceAccountingLines.
     * 
     * @return sourceAccountingLines
     */
    public List<SourceEndowmentAccountingLine> getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    /**
     * Sets the sourceAccountingLines.
     * 
     * @param sourceAccountingLines
     */
    public void setSourceAccountingLines(List<SourceEndowmentAccountingLine> sourceAccountingLines) {
        this.sourceAccountingLines = sourceAccountingLines;
    }

    /**
     * Gets the targetAccountingLines.
     * 
     * @return targetAccountingLines
     */
    public List<TargetEndowmentAccountingLine> getTargetAccountingLines() {
        return targetAccountingLines;
    }

    /**
     * Sets the targetAccountingLines.
     * 
     * @param targetAccountingLines
     */
    public void setTargetAccountingLines(List<TargetEndowmentAccountingLine> targetAccountingLines) {
        this.targetAccountingLines = targetAccountingLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addSourceTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine)
     */
    public void addSourceAccountingLine(SourceEndowmentAccountingLine line) {
        line.setAccountingLineNumber(this.getNextSourceAccountingLineNumber());
        this.sourceAccountingLines.add(line);
        this.nextSourceAccountingLineNumber = new Integer(this.getNextSourceAccountingLineNumber().intValue() + 1);

    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addTargetTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine)
     */
    public void addTargetAccountingLine(TargetEndowmentAccountingLine line) {
        line.setAccountingLineNumber(this.getNextTargetAccountingLineNumber());
        this.targetAccountingLines.add(line);
        this.nextTargetAccountingLineNumber = new Integer(this.getNextTargetAccountingLineNumber().intValue() + 1);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getSourceAccountingLine(int)
     */
    public SourceEndowmentAccountingLine getSourceAccountingLine(int index) {

        while (getSourceAccountingLines().size() <= index) {
            getSourceAccountingLines().add(new SourceEndowmentAccountingLine());
        }
        return (SourceEndowmentAccountingLine) getSourceAccountingLines().get(index);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getTargetAccountingLine(int)
     */
    public TargetEndowmentAccountingLine getTargetAccountingLine(int index) {
        while (getTargetAccountingLines().size() <= index) {
            getTargetAccountingLines().add(new TargetEndowmentAccountingLine());
        }
        return (TargetEndowmentAccountingLine) getTargetAccountingLines().get(index);
    }

    /**
     * Gets the total amount of the accounting lines on this document.
     * 
     * @return total amount of the accounting lines
     */
    public KualiDecimal getTotalAccountingLinesAmount() {

        KualiDecimal totalAmount = KualiDecimal.ZERO;

        if (sourceAccountingLines.size() > 0) {
            for (EndowmentAccountingLine accountingLine : sourceAccountingLines) {
                if (accountingLine.getAmount() != null) {
                    totalAmount = totalAmount.add(accountingLine.getAmount());
                }
            }
        }
        else if (targetAccountingLines.size() > 0) {
            for (EndowmentAccountingLine accountingLine : targetAccountingLines) {
                if (accountingLine.getAmount() != null) {
                    totalAmount = totalAmount.add(accountingLine.getAmount());
                }
            }
        }

        return totalAmount;
    }


    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        List managedList = super.buildListOfDeletionAwareLists();

        managedList.add(getTargetAccountingLines());
        managedList.add(getSourceAccountingLines());

        return managedList;
    }

    /**
     * Used to get the appropriate <code>{@link EndowmentAccountingLineParser}</code> for the <code>Document</code>
     * 
     * @return EndowmentAccountingLineParser
     */
    public EndowmentAccountingLineParser getEndowmentAccountingLineParser() {
        // TODO: check if this is needed
        // try {
        // if (getDataDictionaryEntry().getImportedLineParserClass() != null) {
        // return getDataDictionaryEntry().getImportedLineParserClass().newInstance();
        // }
        // }
        // catch (InstantiationException ie) {
        // throw new IllegalStateException("Accounting Line Parser class " +
        // getDataDictionaryEntry().getImportedLineParserClass().getName() + " cannot be instantiated", ie);
        // }
        // catch (IllegalAccessException iae) {
        // throw new IllegalStateException("Illegal Access Exception while attempting to instantiate Accounting Line Parser class "
        // + getDataDictionaryEntry().getImportedLineParserClass().getName(), iae);
        // }
        return new EndowmentAccountingLineParserBase();
    }

    /**
     * @return the data dictionary entry for this document
     */
    public FinancialSystemTransactionalDocumentEntry getDataDictionaryEntry() {
        if (dataDictionaryEntry == null) {
            dataDictionaryEntry = (FinancialSystemTransactionalDocumentEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(SpringContext.getBean(DataDictionaryService.class).getValidDocumentTypeNameByClass(getClass()));
        }
        return dataDictionaryEntry;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        if (sourceAccountingLineClass == null) {
            sourceAccountingLineClass = (getDataDictionaryEntry().getAccountingLineGroups() != null && getDataDictionaryEntry().getAccountingLineGroups().containsKey("source") && getDataDictionaryEntry().getAccountingLineGroups().get("source").getAccountingLineClass() != null) ? getDataDictionaryEntry().getAccountingLineGroups().get("source").getAccountingLineClass() : SourceEndowmentAccountingLine.class;
        }
        return sourceAccountingLineClass;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getTargetAccountingLineClass()
     */
    public Class getTargetAccountingLineClass() {
        if (targetAccountingLineClass == null) {
            targetAccountingLineClass = (getDataDictionaryEntry().getAccountingLineGroups() != null && getDataDictionaryEntry().getAccountingLineGroups().containsKey("target") && getDataDictionaryEntry().getAccountingLineGroups().get("target").getAccountingLineClass() != null) ? getDataDictionaryEntry().getAccountingLineGroups().get("target").getAccountingLineClass() : TargetEndowmentAccountingLine.class;
        }
        return targetAccountingLineClass;
    }
}
