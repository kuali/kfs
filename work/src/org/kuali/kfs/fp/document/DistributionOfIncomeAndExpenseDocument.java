/*
 * Copyright 2005-2006 The Kuali Foundation
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

import java.util.List;

import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ElectronicPaymentClaim;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.ElectronicPaymentClaiming;
import org.kuali.kfs.sys.service.ElectronicPaymentClaimingService;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;

/**
 * The Distribution of Income and Expense (DI) document is used to distribute income or expense, or assets and liabilities. Amounts
 * being distributed are usually the result of an accumulation of transactions that need to be divided up between various accounts.
 */
public class DistributionOfIncomeAndExpenseDocument extends CapitalAccountingLinesDocumentBase implements Copyable, Correctable, AmountTotaling, ElectronicPaymentClaiming {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DistributionOfIncomeAndExpenseDocument.class);

    protected List<ElectronicPaymentClaim> electronicPaymentClaims;
    protected transient CapitalAssetManagementModuleService capitalAssetManagementModuleService;

    /**
     * Constructs a DistributionOfIncomeAndExpenseDocument.java.
     */
    public DistributionOfIncomeAndExpenseDocument() {
        super();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.FROM;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TO;
    }


    /**
     * @see org.kuali.kfs.sys.document.ElectronicPaymentClaiming#declaimElectronicPaymentClaims()
     */
    public void declaimElectronicPaymentClaims() {
        SpringContext.getBean(ElectronicPaymentClaimingService.class).declaimElectronicPaymentClaimsForDocument(this);
    }

    /**
     * Gets the electronicPaymentClaims attribute.
     * 
     * @return Returns the electronicPaymentClaims.
     */
    public List<ElectronicPaymentClaim> getElectronicPaymentClaims() {
        return electronicPaymentClaims;
    }

    /**
     * Sets the electronicPaymentClaims attribute value.
     * 
     * @param electronicPaymentClaims The electronicPaymentClaims to set.
     * @deprecated
     */
    public void setElectronicPaymentClaims(List<ElectronicPaymentClaim> electronicPaymentClaims) {
        this.electronicPaymentClaims = electronicPaymentClaims;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        this.getCapitalAssetManagementModuleService().deleteDocumentAssetLocks(this);
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#postProcessSave(org.kuali.rice.krad.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);
        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            String documentTypeName = SpringContext.getBean(DataDictionaryService.class).getDocumentTypeNameByClass(this.getClass());
            this.getCapitalAssetManagementModuleService().generateCapitalAssetLock(this, documentTypeName);
        }
    }

    /**
     * @return CapitalAssetManagementModuleService
     */
    CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        if (capitalAssetManagementModuleService == null) {
            capitalAssetManagementModuleService = SpringContext.getBean(CapitalAssetManagementModuleService.class);
        }
        return capitalAssetManagementModuleService;
    }
}
