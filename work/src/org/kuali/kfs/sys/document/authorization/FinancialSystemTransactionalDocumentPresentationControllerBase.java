/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.authorization;

import java.util.Set;

import org.joda.time.DateTime;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.LedgerPostingDocument;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationControllerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.ObjectUtils;

/**
 * Base class for all FinancialSystemDocumentPresentationControllers.
 */
public class FinancialSystemTransactionalDocumentPresentationControllerBase extends TransactionalDocumentPresentationControllerBase implements FinancialSystemTransactionalDocumentPresentationController {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemTransactionalDocumentPresentationControllerBase.class);

    private static ParameterEvaluatorService parameterEvaluatorService;
    private static BankService bankService;
    private static UniversityDateService universityDateService;
    private static DataDictionaryService dataDictionaryService;

    /**
     * Makes sure that the given document implements error correction, that error correction is turned on for the document in the
     * data dictionary, and that the document is in a workflow state that allows error correction.
     *
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentPresentationController#canErrorCorrect(org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument)
     */
    @Override
    public boolean canErrorCorrect(FinancialSystemTransactionalDocument document) {
        if (!(document instanceof Correctable)) {
            return false;
        }

        if (!this.canCopy(document)) {
            return false;
        }
        DocumentEntry documentEntry = getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName());
        //FinancialSystemTransactionalDocumentEntry documentEntry = (FinancialSystemTransactionalDocumentEntry) ();

        if ( !(documentEntry instanceof FinancialSystemTransactionalDocumentEntry)
                || !((FinancialSystemTransactionalDocumentEntry)documentEntry).getAllowsErrorCorrection()) {
            return false;
        }

        if (document.getFinancialSystemDocumentHeader().getCorrectedByDocumentId() != null) {
            return false;
        }

        if (document.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber() != null) {
            return false;
        }

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (!isApprovalDateWithinFiscalYear(workflowDocument)) {
            return false;
        }

        return workflowDocument.isApproved();
    }

    protected boolean isApprovalDateWithinFiscalYear(WorkflowDocument workflowDocument) {
        if ( workflowDocument != null ) {
            DateTime approvalDate = workflowDocument.getDateApproved();
            if ( approvalDate != null ) {
                // compare approval fiscal year with current fiscal year
                Integer approvalYear = getUniversityDateService().getFiscalYear(approvalDate.toDate());
                Integer currentFiscalYear = getUniversityDateService().getCurrentFiscalYear();
                return ObjectUtils.nullSafeEquals(currentFiscalYear, approvalYear);
            }
        }
        return true;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.DocumentPresentationControllerBase#getDocumentActions(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getDocumentActions(Document document) {
        Set<String> documentActions = super.getDocumentActions(document);

        if (document instanceof FinancialSystemTransactionalDocument) {
            if (canErrorCorrect((FinancialSystemTransactionalDocument) document)) {
                documentActions.add(KFSConstants.KFS_ACTION_CAN_ERROR_CORRECT);
            }

            if (canHaveBankEntry(document)) {
                documentActions.add(KFSConstants.KFS_ACTION_CAN_EDIT_BANK);
            }

            if (canViewSecuredField(document)) {
                 documentActions.add(KFSConstants.KFS_ACTION_CAN_VIEW_SECURED_FIELD);
            }


        }

        // CSU 6702 BEGIN
        // rSmart-jkneal-KFSCSU-199-begin mod for adding accounting period view action
        if (document instanceof LedgerPostingDocument) {
            // check account period selection is enabled
            // PERFORMANCE: cache this setting - move call to service
            boolean accountingPeriodEnabled = getParameterService().getParameterValueAsBoolean(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.ENABLE_FISCAL_PERIOD_SELECTION_IND, false);
            if ( accountingPeriodEnabled) {
                // check accounting period is enabled for doc type in system parameter
                String docType = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                // PERFORMANCE: cache this setting - move call to service
                ParameterEvaluator evaluator = getParameterEvaluatorService().getParameterEvaluator(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.DETAIL_PARAMETER_TYPE, KfsParameterConstants.YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES.FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES, docType);
                if (evaluator.evaluationSucceeds()) {
                    documentActions.add(KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION);
                }
            }
        }
        // rSmart-jkneal-KFSCSU-199-end mod
        // CSU 6702 END

        return documentActions;
    }

    /**
     * @see org.kuali.rice.krad.document.authorization.TransactionalDocumentPresentationControllerBase#getEditModes(org.kuali.rice.krad.document.Document)
     */
    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);

        if (document instanceof AmountTotaling) {
            editModes.add(KFSConstants.AMOUNT_TOTALING_EDITING_MODE);
        }

        if (this.canHaveBankEntry(document)) {
            editModes.add(KFSConstants.BANK_ENTRY_VIEWABLE_EDITING_MODE);
        }

        return editModes;
    }


    /**
         * Determines whether the current user can view the secured field in
         * the document overview tab. If the current user is the initiator of
         * the document, then the current user can view the secured field
         * without any masking.
         *
         * @param  document The document that the user is viewing.
         * @return true if the current user can view the secured field and false otherwise.
         */
        public boolean canViewSecuredField(Document document) {
            String currentUser = GlobalVariables.getUserSession().getPrincipalId();

            if (document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId().equals(currentUser)) {
                return true;
            }
            else {
                return false;
            }

        }





    // check if bank entry should be viewable for the given document
    protected boolean canHaveBankEntry(Document document) {
        boolean bankSpecificationEnabled = getBankService().isBankSpecificationEnabled();

        if (bankSpecificationEnabled) {
            String documentTypeName = document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName();

            return getBankService().isBankSpecificationEnabledForDocument(document.getClass());
        }

        return false;
    }

    protected ParameterEvaluatorService getParameterEvaluatorService() {
        if (parameterEvaluatorService == null) {
            parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        }
        return parameterEvaluatorService;
    }

    protected BankService getBankService() {
        if (bankService == null) {
            bankService = SpringContext.getBean(BankService.class);
        }
        return bankService;
    }

    public DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

    public UniversityDateService getUniversityDateService() {
        if (universityDateService == null) {
            universityDateService = SpringContext.getBean(UniversityDateService.class);
        }
        return universityDateService;
    }
}
