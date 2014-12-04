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
package org.kuali.kfs.module.ar.document.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.document.service.AccountsReceivablePendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

public class AccountsReceivablePendingEntryServiceImpl implements AccountsReceivablePendingEntryService {
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected ParameterService parameterService;

    /**
     * This method creates and adds generic invoice related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasClaimOnCashOffset
     * @param amount
     */
    @Override
    public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasClaimOnCashOffset, KualiDecimal amount){
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(glpeSource, glpeSourceDetail, sequenceHelper, explicitEntry);
        explicitEntry.setTransactionLedgerEntryAmount(amount.abs());
        explicitEntry.setTransactionDebitCreditCode(isDebit? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);

        //add explicit entry
        glpeSource.addPendingEntry(explicitEntry);

        //add claim on cash offset entry
        if( hasClaimOnCashOffset ){
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
            generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(glpeSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
            glpeSource.addPendingEntry(offsetEntry);
        }
    }

    /**
     * This method creates and adds generic invoice related GLPE's
     * @param glpeSource
     * @param glpeSourceDetail
     * @param sequenceHelper
     * @param isDebit
     * @param hasReceivableClaimOnCashOffset
     * @param writeoffTaxGenerationMethodDisallowFlag
     * @param amount
     */
    @Override
    public void createAndAddGenericInvoiceRelatedGLPEs(GeneralLedgerPendingEntrySource glpeSource, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, boolean isDebit, boolean hasReceivableClaimOnCashOffset, boolean writeoffTaxGenerationMethodDisallowFlag, KualiDecimal amount){
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.populateExplicitGeneralLedgerPendingEntry(glpeSource, glpeSourceDetail, sequenceHelper, explicitEntry);
        explicitEntry.setTransactionLedgerEntryAmount(amount.abs());
        explicitEntry.setTransactionDebitCreditCode(isDebit? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);

        boolean overrideFinancialObjectCodeFlag = isDebit && writeoffTaxGenerationMethodDisallowFlag  && !hasReceivableClaimOnCashOffset;

        if (!overrideFinancialObjectCodeFlag) {
            //add explicit entry
            glpeSource.addPendingEntry(explicitEntry);
        }

        // do not add claim on cash offset entry if GLPLE_RECEIVABLE_OFFSET_METHOD = 3 && GLPLE_WRITEOFF_TAX_GENERATION_METHOD = D
        if (hasReceivableClaimOnCashOffset && writeoffTaxGenerationMethodDisallowFlag ) {
            return;
        }

        //add claim on cash offset entry
        if( hasReceivableClaimOnCashOffset || writeoffTaxGenerationMethodDisallowFlag ){
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
            generalLedgerPendingEntryService.populateOffsetGeneralLedgerPendingEntry(glpeSource.getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
            if (overrideFinancialObjectCodeFlag) {
                explicitEntry.setFinancialObjectCode(offsetEntry.getFinancialObjectCode());
                glpeSource.addPendingEntry(explicitEntry);
            }
            // TODO
            // override (here or above???) account and object when hasReceivableClaimOnCashOffset = true and writeoffTaxGenerationMethodDisallowFlag = true
            glpeSource.addPendingEntry(offsetEntry);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.AccountsReceivablePendingEntryService#getAccountsReceivableObjectCode(org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied)
     */
    @Override
    public ObjectCode getAccountsReceivableObjectCode(InvoicePaidApplied invoicePaidApplied) {
        // make sure its all re-connected with child objects
        invoicePaidApplied.getInvoiceDetail().refresh();
        invoicePaidApplied.getInvoiceDetail().refreshNonUpdateableReferences();

        invoicePaidApplied.getInvoiceDetail().refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        ObjectCode objectCode = invoicePaidApplied.getInvoiceDetail().getObjectCode();

        return objectCode;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService#getAccountsReceivableObjectCodeBasedOnReceivableParameter(org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail)
     */
    @Override
    public String getAccountsReceivableObjectCode(CustomerInvoiceDetail customerInvoiceDetail) {
        if (StringUtils.isNotEmpty(customerInvoiceDetail.getChartOfAccountsCode())) {
            customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.CHART);
            return customerInvoiceDetail.getChart().getFinAccountsReceivableObj().getFinancialObjectCode();
        }

        return null;
    }

    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
