/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetAccountsGroupDetails;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * class which defines behavior common for capital asset information lines.
 */
 public class CapitalAssetInformationDocumentBase extends AccountingDocumentBase implements CapitalAssetEditable {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CapitalAssetInformationDocumentBase.class);
    protected Integer nextCapitalAssetLineNumber;

    // capital asset
    protected List<CapitalAssetInformation> capitalAssetInformation;
    
    /**
     * Constructs a CapitalAssetInformationDocumentBase
     */
    public CapitalAssetInformationDocumentBase() {
        super();
        
        this.setCapitalAssetInformation(new ArrayList());
        this.setNextCapitalAssetLineNumber(1);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        String documentNumber = getDocumentHeader().getDocumentNumber();
        // update gl status as processed when all the capital assets have been processed...
        SpringContext.getBean(CapitalAssetBuilderModuleService.class).markProcessedGLEntryLine(documentNumber);
    }
    
    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();
        
        List<CapitalAssetInformation> capitalAssets = new ArrayList<CapitalAssetInformation>();
        capitalAssets.addAll(this.getCapitalAssetInformation());

        managedLists.add(capitalAssets); 
        
        return managedLists;
    }

    /**
     * Return true if account line is debit
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line from accounting document
     * @return true is account line is debit
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        return isDebitUtils.isDebitConsideringSectionAndTypePositiveOnly(this, (AccountingLine) postable);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return ObjectUtils.isNull(capitalAssetInformation) ? null : capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;
    }
    
    /**
     * Gets the nextCapitalAssetLineNumber attribute.
     * 
     * @return Returns the nextCapitalAssetLineNumber
     */
    
    public Integer getNextCapitalAssetLineNumber() {
        return nextCapitalAssetLineNumber;
    }

    /** 
     * Sets the nextCapitalAssetLineNumber attribute.
     * 
     * @param nextCapitalAssetLineNumber The nextCapitalAssetLineNumber to set.
     */
    public void setNextCapitalAssetLineNumber(Integer nextCapitalAssetLineNumber) {
        this.nextCapitalAssetLineNumber = nextCapitalAssetLineNumber;
    }
}
