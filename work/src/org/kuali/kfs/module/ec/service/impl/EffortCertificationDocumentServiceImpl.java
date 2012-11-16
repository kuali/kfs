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
package org.kuali.kfs.module.ec.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferSourceAccountingLine;
import org.kuali.kfs.integration.ld.LaborLedgerExpenseTransferTargetAccountingLine;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortKeyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailLineOverride;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.validation.impl.EffortCertificationDocumentRuleUtil;
import org.kuali.kfs.module.ec.service.EffortCertificationDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride.COMPONENT;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.action.ActionType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

/**
 * To implement the services related to the effort certification document
 */
@Transactional
public class EffortCertificationDocumentServiceImpl implements EffortCertificationDocumentService {
    public static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDocumentServiceImpl.class);

    private LaborModuleService laborModuleService;
    private KualiModuleService kualiModuleService;
    private ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    private DocumentService documentService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#processApprovedEffortCertificationDocument(org.kuali.kfs.module.ec.document.EffortCertificationDocument)
     */
    @Override
    public void processApprovedEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument) {
        WorkflowDocument workflowDocument = effortCertificationDocument.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isProcessed()) {
            GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
            generateSalaryExpenseTransferDocument(effortCertificationDocument);
        }
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#createAndRouteEffortCertificationDocument(org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild)
     */

    @Override
    public boolean createAndRouteEffortCertificationDocument(EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        try {
            EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) documentService.getNewDocument(EffortConstants.EffortDocumentTypes.EFFORT_CERTIFICATION_DOCUMENT);
            populateEffortCertificationDocument(effortCertificationDocument, effortCertificationDocumentBuild);
            documentService.routeDocument(effortCertificationDocument, KFSConstants.EMPTY_STRING, null);
        }
        catch (WorkflowException we) {
            LOG.error( "Unable to route ECD document: " + effortCertificationDocumentBuild, we);
            throw new RuntimeException("Unable to route ECD document: " + effortCertificationDocumentBuild, we);
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#populateEffortCertificationDocument(org.kuali.kfs.module.ec.document.EffortCertificationDocument,
     *      org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild)
     */

    @Override
    public boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        // populate the fields of the document
        effortCertificationDocument.setUniversityFiscalYear(effortCertificationDocumentBuild.getUniversityFiscalYear());
        effortCertificationDocument.setEmplid(effortCertificationDocumentBuild.getEmplid());
        effortCertificationDocument.setEffortCertificationReportNumber(effortCertificationDocumentBuild.getEffortCertificationReportNumber());
        effortCertificationDocument.setEffortCertificationDocumentCode(effortCertificationDocumentBuild.getEffortCertificationDocumentCode());

        // populate the detail line of the document
        List<EffortCertificationDetail> detailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        detailLines.clear();

        List<EffortCertificationDetailBuild> detailLinesBuild = effortCertificationDocumentBuild.getEffortCertificationDetailLinesBuild();
        for (EffortCertificationDetailBuild detailLineBuild : detailLinesBuild) {
            detailLines.add(new EffortCertificationDetail(detailLineBuild));
        }

        // populate the document header of the document
        FinancialSystemDocumentHeader documentHeader = effortCertificationDocument.getFinancialSystemDocumentHeader();
        documentHeader.setDocumentDescription(effortCertificationDocumentBuild.getEmplid());
        documentHeader.setFinancialDocumentTotalAmount(EffortCertificationDocument.getDocumentTotalAmount(effortCertificationDocument));

        return true;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#resetEffortCertificationDetailLines(org.kuali.kfs.module.ec.document.EffortCertificationDocument)
     */

    @Override
    public void removeEffortCertificationDetailLines(EffortCertificationDocument effortCertificationDocument) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, effortCertificationDocument.getDocumentNumber());

        businessObjectService.deleteMatching(EffortCertificationDetail.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#generateSalaryExpenseTransferDocument(org.kuali.kfs.module.ec.document.EffortCertificationDocument)
     */

    @Override
    public boolean generateSalaryExpenseTransferDocument(EffortCertificationDocument effortCertificationDocument) {
        List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines = this.buildSourceAccountingLines(effortCertificationDocument);
        List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines = this.buildTargetAccountingLines(effortCertificationDocument);

        if (sourceAccoutingLines.isEmpty() || targetAccoutingLines.isEmpty()) {
            return true;
        }

        String description = effortCertificationDocument.getEmplid();
        String explanation = MessageBuilder.buildMessageWithPlaceHolder(EffortKeyConstants.MESSAGE_CREATE_SET_DOCUMENT_DESCRIPTION, effortCertificationDocument.getDocumentNumber()).toString();

        String annotation = KFSConstants.EMPTY_STRING;
        List<String> adHocRecipients = new ArrayList<String>();
        adHocRecipients.addAll(this.getFiscalOfficersIfAmountChanged(effortCertificationDocument));

        try {
            laborModuleService.createAndBlankApproveSalaryExpenseTransferDocument(description, explanation, annotation, adHocRecipients, sourceAccoutingLines, targetAccoutingLines);
        }
        catch (WorkflowException we) {
            LOG.error( "Error while routing SET document created from ECD: " + effortCertificationDocument, we);
            throw new RuntimeException("Error while routing SET document created from ECD: " + effortCertificationDocument, we);
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDocumentService#addRouteLooping(org.kuali.kfs.module.ec.document.EffortCertificationDocument)
     */

    @Override
    public void addRouteLooping(EffortCertificationDocument effortCertificationDocument) {
        WorkflowDocument workflowDocument = effortCertificationDocument.getDocumentHeader().getWorkflowDocument();
        Set<Person> priorApprovers = getPriorApprovers(workflowDocument);

        for (EffortCertificationDetail detailLine : effortCertificationDocument.getEffortCertificationDetailLines()) {
            boolean hasBeenChanged = EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromPersisted(detailLine);
            if (!hasBeenChanged) {
                continue;
            }
            if ( LOG.isInfoEnabled() ) {
                LOG.info( "EC Detail Line has been changed: " + detailLine );
            }

            Account account = detailLine.getAccount();
            Person fiscalOfficer = account.getAccountFiscalOfficerUser();
            if ( fiscalOfficer != null && StringUtils.isNotBlank(fiscalOfficer.getPrincipalName())) {
                // KULEFR-206
                // String actionRequestOfOfficer = this.getActionRequest(routeLevelName, KFSConstants.RouteLevelNames.ACCOUNT);
                AdHocRoutePerson adHocRoutePerson = buildAdHocRouteRecipient(fiscalOfficer.getPrincipalName(), ActionRequestType.APPROVE);

                addAdHocRoutePerson(effortCertificationDocument.getAdHocRoutePersons(), priorApprovers, adHocRoutePerson);
            } else {
                LOG.warn( "Unable to obtain a fiscal officer for the detail line's account: " + account.getChartOfAccountsCode() + "-" + account.getAccountNumber() );
            }

            Person projectDirector = contractsAndGrantsModuleService.getProjectDirectorForAccount(account);
            if (projectDirector != null) {
                String accountProjectDirectorPersonUserId = projectDirector.getPrincipalName();
                // KULEFR-206
                // String actionRequestOfDirector = this.getActionRequest(routeLevelName,
                // KFSConstants.RouteLevelNames.PROJECT_MANAGEMENT);
                AdHocRoutePerson adHocRoutePerson = buildAdHocRouteRecipient(accountProjectDirectorPersonUserId, ActionRequestType.APPROVE);

                addAdHocRoutePerson(effortCertificationDocument.getAdHocRoutePersons(), priorApprovers, adHocRoutePerson);
            }
        }
    }

    // add the given ad hoc route person in the list if the person is one of prior approvers and is not in the list
    protected void addAdHocRoutePerson(Collection<AdHocRoutePerson> adHocRoutePersonList, Set<Person> priorApprovers, AdHocRoutePerson adHocRoutePerson) {
        boolean canBeAdded = false;

        if (priorApprovers == null) {
            canBeAdded = true;
        } else {
            // we only want to ad-hoc if the user previously approved this document
            for (Person approver : priorApprovers) {
                if (StringUtils.equals(approver.getPrincipalName(), adHocRoutePerson.getId())) {
                    canBeAdded = true;
                    break;
                }
            }
        }

        if (canBeAdded) {
            // check that we have not already added them for the same action
            for (AdHocRoutePerson person : adHocRoutePersonList) {
                if (isSameAdHocRoutePerson(person, adHocRoutePerson)) {
                    canBeAdded = false;
                    break;
                }
            }
        }

        if (canBeAdded) {
            adHocRoutePersonList.add(adHocRoutePerson);
        }
    }

    protected boolean isSameAdHocRoutePerson(AdHocRoutePerson person1, AdHocRoutePerson person2) {
        if (person1 == null || person2 == null) {
            return false;
        }

        boolean isSameAdHocRoutePerson = StringUtils.equals(person1.getId(), person2.getId());
        isSameAdHocRoutePerson &= person1.getType().equals(person2.getType());
        isSameAdHocRoutePerson &= StringUtils.equals(person1.getActionRequested(), person2.getActionRequested());

        return isSameAdHocRoutePerson;
    }

    protected Set<Person> getPriorApprovers(WorkflowDocument workflowDocument) {
        PersonService personService = KimApiServiceLocator.getPersonService();
        List<ActionTaken> actionsTaken = workflowDocument.getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();

        for (ActionTaken actionTaken : actionsTaken) {
            if (ActionType.APPROVE.equals(actionTaken.getActionTaken())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }

    /**
     * build an adhoc route recipient from the given person user id and action request
     *
     * @param personUserId the given person user id
     * @param actionRequest the given action request
     * @return an adhoc route recipient built from the given information
     */
    protected AdHocRoutePerson buildAdHocRouteRecipient(String personUserId, ActionRequestType actionRequest) {
        AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
        adHocRoutePerson.setActionRequested(actionRequest.getCode());
        adHocRoutePerson.setId(personUserId);

        return adHocRoutePerson;
    }

    /**
     * build the source accounting lines for a salary expense transfer document from the given effort certification document. In the
     * holder, the first item is source accounting line list and the second the target accounting line list.
     *
     * @param effortCertificationDocument the given effort certification document
     * @return the source accounting lines for a salary expense transfer document built from the given effort certification document
     */
    protected List<LaborLedgerExpenseTransferAccountingLine> buildSourceAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<LaborLedgerExpenseTransferAccountingLine> sourceAccountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();

        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            if (this.getDifference(detailLine).isPositive()) {
                LaborLedgerExpenseTransferSourceAccountingLine sourceLine = kualiModuleService.getResponsibleModuleService(LaborLedgerExpenseTransferSourceAccountingLine.class).createNewObjectFromExternalizableClass(LaborLedgerExpenseTransferSourceAccountingLine.class);
                this.addAccountingLineIntoList(sourceAccountingLines, sourceLine, effortCertificationDocument, detailLine);
            }
        }
        return sourceAccountingLines;
    }

    /**
     * build the target accounting lines for a salary expense transfer document from the given effort certification document. In the
     * holder, the first item is source accounting line list and the second the target accounting line list.
     *
     * @param effortCertificationDocument the given effort certification document
     * @return the target accounting lines for a salary expense transfer document built from the given effort certification document
     */
    protected List<LaborLedgerExpenseTransferAccountingLine> buildTargetAccountingLines(EffortCertificationDocument effortCertificationDocument) {
        List<LaborLedgerExpenseTransferAccountingLine> targetAccountingLines = new ArrayList<LaborLedgerExpenseTransferAccountingLine>();

        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            if (this.getDifference(detailLine).isNegative()) {
                LaborLedgerExpenseTransferTargetAccountingLine targetLine = kualiModuleService.getResponsibleModuleService(LaborLedgerExpenseTransferTargetAccountingLine.class).createNewObjectFromExternalizableClass(LaborLedgerExpenseTransferTargetAccountingLine.class);
                this.addAccountingLineIntoList(targetAccountingLines, targetLine, effortCertificationDocument, detailLine);
            }
        }
        return targetAccountingLines;
    }

    /**
     * get all fiscal officers of the detail line accounts where the salary amounts are changed
     *
     * @param effortCertificationDocument the given document that contains the detail lines
     * @return all fiscal officers of the detail line accounts where the salary amounts are changed
     */
    protected Set<String> getFiscalOfficersIfAmountChanged(EffortCertificationDocument effortCertificationDocument) {
        Set<String> fiscalOfficers = new HashSet<String>();

        List<EffortCertificationDetail> effortCertificationDetailLines = effortCertificationDocument.getEffortCertificationDetailLines();
        for (EffortCertificationDetail detailLine : effortCertificationDetailLines) {
            if (this.getDifference(detailLine).isNonZero()) {
                Account account = detailLine.getAccount();
                String accountFiscalOfficerPersonUserId = account.getAccountFiscalOfficerUser().getPrincipalName();

                if (StringUtils.isEmpty(accountFiscalOfficerPersonUserId)) {
                    fiscalOfficers.add(accountFiscalOfficerPersonUserId);
                }
            }
        }
        return fiscalOfficers;
    }

    /**
     * add a new accounting line into the given accounting line list. The accounting line is generated from the given detail line
     *
     * @param accountingLines a list of accounting lines
     * @param clazz the specified class of the accounting line
     * @param effortCertificationDocument the given effort certification document that contains the given detail line
     * @param detailLine the given detail line that is used to generate an accounting line
     */
    protected void addAccountingLineIntoList(List<LaborLedgerExpenseTransferAccountingLine> accountingLineList, LaborLedgerExpenseTransferAccountingLine accountingLine, EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail detailLine) {
        accountingLine.setSequenceNumber(accountingLineList.size() + 1);

        this.populateAccountingLine(effortCertificationDocument, detailLine, accountingLine);
        accountingLineList.add(accountingLine);
    }

    /**
     * populate an accounting line from the given detail line
     *
     * @param effortCertificationDocument the given effort certification document that contains the given detail line
     * @param detailLine the given detail line
     * @param accountingLine the accounting line needed to be populated
     */
    protected void populateAccountingLine(EffortCertificationDocument effortCertificationDocument, EffortCertificationDetail detailLine, LaborLedgerExpenseTransferAccountingLine accountingLine) {
        // if an expired account override is needed, set it, otherwise validations on the downstream SET doc could fail
        AccountingLineOverride override = EffortCertificationDetailLineOverride.determineNeededOverrides(detailLine);
        accountingLine.setAccountExpiredOverrideNeeded(override.hasComponent(COMPONENT.EXPIRED_ACCOUNT));
        accountingLine.setAccountExpiredOverride(accountingLine.getAccountExpiredOverrideNeeded());
        if (accountingLine.getAccountExpiredOverrideNeeded()) {
            accountingLine.setOverrideCode(override.getCode());
        }

        accountingLine.setChartOfAccountsCode(detailLine.getChartOfAccountsCode());
        accountingLine.setAccountNumber(detailLine.getAccountNumber());
        accountingLine.setSubAccountNumber(detailLine.getSubAccountNumber());

        accountingLine.setPostingYear(detailLine.getUniversityFiscalYear());
        accountingLine.setFinancialObjectCode(detailLine.getFinancialObjectCode());
        accountingLine.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        accountingLine.setAmount(this.getDifference(detailLine).abs());

        accountingLine.setFinancialSubObjectCode(null);
        accountingLine.setProjectCode(null);
        accountingLine.setOrganizationReferenceId(null);

        accountingLine.setEmplid(effortCertificationDocument.getEmplid());
        accountingLine.setPositionNumber(detailLine.getPositionNumber());
        accountingLine.setPayrollTotalHours(BigDecimal.ZERO);

        EffortCertificationReportDefinition reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();
        accountingLine.setPayrollEndDateFiscalYear(reportDefinition.getExpenseTransferFiscalYear());
        accountingLine.setPayrollEndDateFiscalPeriodCode(reportDefinition.getExpenseTransferFiscalPeriodCode());

        accountingLine.refreshNonUpdateableReferences();
    }

    /**
     * get the difference between the original amount and updated amount of the given detail line
     *
     * @param detailLine the given detail line
     * @return the difference between the original amount and updated amount of the given detail line
     */
    protected KualiDecimal getDifference(EffortCertificationDetail detailLine) {
        return detailLine.getEffortCertificationOriginalPayrollAmount().subtract(detailLine.getEffortCertificationPayrollAmount());
    }

    /**
     * Sets the laborModuleService attribute value.
     *
     * @param laborModuleService The laborModuleService to set.
     */
    public void setLaborModuleService(LaborModuleService laborModuleService) {
        this.laborModuleService = laborModuleService;
    }

    /**
     * Sets the documentService attribute value.
     *
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
