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
package org.kuali.module.gl.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.comparators.ComparableComparator;
import org.kuali.core.dao.DocumentDao;
import org.kuali.core.web.comparator.NumericValueComparator;
import org.kuali.core.web.comparator.StringValueComparator;
import org.kuali.core.web.comparator.TemporalValueComparator;
import org.kuali.core.web.ui.Column;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.dao.CorrectionChangeDao;
import org.kuali.module.gl.dao.CorrectionChangeGroupDao;
import org.kuali.module.gl.dao.CorrectionCriteriaDao;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.CorrectionDocumentService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CorrectionDocumentServiceImpl implements CorrectionDocumentService {

    private CorrectionChangeGroupDao correctionChangeGroupDao;
    private CorrectionChangeDao correctionChangeDao;
    private CorrectionCriteriaDao correctionCriteriaDao;
    private DocumentDao documentDao;

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionChangeGroupNumber(java.lang.String, int)
     */
    public CorrectionChangeGroup findByDocumentNumberAndCorrectionChangeGroupNumber(String docId, int i) {

        return correctionChangeGroupDao.findByDocumentNumberAndCorrectionChangeGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentHeaderIdAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentHeaderIdAndCorrectionGroupNumber(String docId, int i) {

        return correctionChangeDao.findByDocumentHeaderIdAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByDocumentNumberAndCorrectionGroupNumber(java.lang.String, int)
     */
    public List findByDocumentNumberAndCorrectionGroupNumber(String docId, int i) {

        return correctionCriteriaDao.findByDocumentNumberAndCorrectionGroupNumber(docId, i);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.CorrectionDocumentService#findByCorrectionDocumentHeaderId(java.lang.String)
     */
    public CorrectionDocument findByCorrectionDocumentHeaderId(String docId) {

        return (CorrectionDocument) documentDao.findByDocumentHeaderId(CorrectionDocument.class, docId);
    }

    public void setCorrectionChangeDao(CorrectionChangeDao correctionChangeDao) {
        this.correctionChangeDao = correctionChangeDao;
    }

    public void setCorrectionChangeGroupDao(CorrectionChangeGroupDao correctionChangeGroupDao) {
        this.correctionChangeGroupDao = correctionChangeGroupDao;
    }

    public void setCorrectionCriteriaDao(CorrectionCriteriaDao correctionCriteriaDao) {
        this.correctionCriteriaDao = correctionCriteriaDao;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    private List<Column> cachedColumns = null;
    
    /**
     * @see org.kuali.module.gl.service.CorrectionDocumentService#getTableRenderColumnMetadata(java.lang.String)
     */
    public List<Column> getTableRenderColumnMetadata(String docId) {
        synchronized (this) {
            if (cachedColumns == null) {
                cachedColumns = new ArrayList<Column>();
                Column columnToAdd;
                
                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Year");
                columnToAdd.setPropertyName("universityFiscalYear");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Chart Code");
                columnToAdd.setPropertyName("chartOfAccountsCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Account Number");
                columnToAdd.setPropertyName("accountNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Account Number");
                columnToAdd.setPropertyName("subAccountNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Code");
                columnToAdd.setPropertyName("financialObjectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sub Object Code");
                columnToAdd.setPropertyName("financialSubObjectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Balance Type");
                columnToAdd.setPropertyName("financialBalanceTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Object Type");
                columnToAdd.setPropertyName("financialObjectTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Fiscal Period");
                columnToAdd.setPropertyName("universityFiscalPeriodCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Type");
                columnToAdd.setPropertyName("financialDocumentTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Origin Code");
                columnToAdd.setPropertyName("financialSystemOriginationCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Document Number");
                columnToAdd.setPropertyName("documentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Sequence Number");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName("transactionLedgerEntrySequenceNumber");
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Description");
                columnToAdd.setPropertyName("transactionLedgerEntryDescription");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Amount");
                columnToAdd.setValueComparator(NumericValueComparator.getInstance());
                columnToAdd.setPropertyName("transactionLedgerEntryAmount");
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Debit Credit Indicator");
                columnToAdd.setPropertyName("transactionDebitCreditCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Transaction Date");
                columnToAdd.setPropertyName("transactionDate");
                columnToAdd.setValueComparator(TemporalValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Doc Number");
                columnToAdd.setPropertyName("organizationDocumentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Project Code");
                columnToAdd.setPropertyName("projectCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Org Ref ID");
                columnToAdd.setPropertyName("organizationReferenceId");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Type");
                columnToAdd.setPropertyName("referenceFinancialDocumentTypeCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Origin Code");
                columnToAdd.setPropertyName("referenceFinancialSystemOriginationCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Ref Doc Number");
                columnToAdd.setPropertyName("referenceFinancialDocumentNumber");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Reversal Date");
                columnToAdd.setPropertyName("financialDocumentReversalDate");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);

                columnToAdd = new Column();
                columnToAdd.setColumnTitle("Enc Update Code");
                columnToAdd.setPropertyName("transactionEncumbranceUpdateCode");
                columnToAdd.setValueComparator(StringValueComparator.getInstance());
                cachedColumns.add(columnToAdd);
                
                cachedColumns = Collections.unmodifiableList(cachedColumns);
            }
        }
        return cachedColumns;
    }
}
