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

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.businessobject.CapitalAssetInformationDetail;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Abstract class which defines behavior common for capital asset information lines.
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
        
        if (ObjectUtils.isNotNull(this.getGeneralLedgerPendingEntries())) {
            int caiSize = this.getGeneralLedgerPendingEntries().size();
        }
        
        if (ObjectUtils.isNotNull(this.getCapitalAssetInformation())) {
            int caiSize = this.getCapitalAssetInformation().size();
        }
        
        this.setCapitalAssetInformation(new ArrayList());
        this.setNextCapitalAssetLineNumber(1);
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();
        
        List<CapitalAssetInformation> capitalAssets = this.getCapitalAssetInformation();
        managedLists.add(capitalAssets); 
        List<CapitalAssetInformationDetail> capitalAssetDetails = new ArrayList();
        
        if (capitalAssets != null && capitalAssets.size() > 0) {
            for (CapitalAssetInformation capitalAsset : capitalAssets) {
                capitalAssetDetails.addAll(capitalAsset.getCapitalAssetInformationDetails());               
            }
        }
        
        managedLists.add(capitalAssetDetails);  
       
        return managedLists;
    }

    /**
     * Return true if account line is debit
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine accounting line from accounting document
     * @return true is account line is debit
     * @see IsDebitUtils#isDebitConsideringSectionAndTypePositiveOnly(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * @see org.kuali.rice.kns.rule.AccountingLineRule#isDebit(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.rice.kns.bo.AccountingLine)
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
