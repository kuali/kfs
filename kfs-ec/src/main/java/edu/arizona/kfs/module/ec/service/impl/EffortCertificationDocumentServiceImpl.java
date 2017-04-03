package edu.arizona.kfs.module.ec.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDocumentBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.document.EffortCertificationDocument;
import org.kuali.kfs.module.ec.document.validation.impl.EffortCertificationDocumentRuleUtil;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionRequestType;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.bo.AdHocRoutePerson;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.module.ec.EffortConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

@Transactional
public class EffortCertificationDocumentServiceImpl extends org.kuali.kfs.module.ec.service.impl.EffortCertificationDocumentServiceImpl {

    @Override
    public boolean populateEffortCertificationDocument(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        super.populateEffortCertificationDocument(effortCertificationDocument, effortCertificationDocumentBuild);

        // populate the document header of the document
        FinancialSystemDocumentHeader documentHeader = effortCertificationDocument.getFinancialSystemDocumentHeader();
        String description = this.buildDocumentDescription(effortCertificationDocument, effortCertificationDocumentBuild);
        documentHeader.setDocumentDescription(description);

        return true;
    }

    protected String buildDocumentDescription(EffortCertificationDocument effortCertificationDocument, EffortCertificationDocumentBuild effortCertificationDocumentBuild) {
        StringBuilder description = new StringBuilder();

        String employeeName = effortCertificationDocumentBuild.getEmployee().getName();
        description.append(employeeName).append(", ");

        EffortCertificationReportDefinition reportDefinition = effortCertificationDocumentBuild.getEffortCertificationReportDefinition();
        if (reportDefinition == null) {  // get report title from the document's report definition object
            reportDefinition = effortCertificationDocument.getEffortCertificationReportDefinition();
        }

        String reportPeriodTitle = reportDefinition.getEffortCertificationReportPeriodTitle();
        description.append(reportPeriodTitle);

        int maxLengthOfDocumentDescription = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(FinancialSystemDocumentHeader.class, KFSPropertyConstants.DOCUMENT_DESCRIPTION);
        return StringUtils.left(description.toString(), maxLengthOfDocumentDescription);
    }
    
    @Override
    public void addRouteLooping(EffortCertificationDocument effortCertificationDocument) {
        WorkflowDocument workflowDocument = effortCertificationDocument.getDocumentHeader().getWorkflowDocument();
        Set<Person> priorApprovers = getPriorApprovers(workflowDocument);

        for (EffortCertificationDetail detailLine : effortCertificationDocument.getEffortCertificationDetailLines()) {
            boolean hasBeenChanged = EffortCertificationDocumentRuleUtil.isPayrollAmountChangedFromPersisted(detailLine);
            boolean percentHasBeenChanged = EffortCertificationDocumentRuleUtil.isEffortPercentChangedFromPersisted(detailLine);
            if (!hasBeenChanged && !percentHasBeenChanged) {
                continue;
            }
            boolean isNewLine = detailLine.isNewLineIndicator();
            if ( LOG.isInfoEnabled() ) {
                LOG.info( "EC Detail Line has been changed: " + detailLine );
            }

            Account account = detailLine.getAccount();
            Person fiscalOfficer = account.getAccountFiscalOfficerUser();
            if ( fiscalOfficer != null && StringUtils.isNotBlank(fiscalOfficer.getPrincipalName())) {
                // KULEFR-206
                // String actionRequestOfOfficer = this.getActionRequest(routeLevelName, KFSConstants.RouteLevelNames.ACCOUNT);
                AdHocRoutePerson adHocRoutePerson = buildAdHocRouteRecipient(fiscalOfficer.getPrincipalName(), ActionRequestType.APPROVE);

                addAdHocRoutePerson(effortCertificationDocument.getAdHocRoutePersons(), priorApprovers, adHocRoutePerson, isNewLine);
            } else {
                LOG.warn( "Unable to obtain a fiscal officer for the detail line's account: " + account.getChartOfAccountsCode() + "-" + account.getAccountNumber() );
            }

            // only add project director if not at level defined in ROUTE_NODE_FOR_ADHOC_EXCLUSION
            if (!isDocumentStoppedInRouteNode(workflowDocument, EffortConstants.ROUTE_NODE_FOR_ADHOC_EXCLUSION)) {            
	            Person projectDirector = contractsAndGrantsModuleService.getProjectDirectorForAccount(account);
	            if (projectDirector != null) {
	                String accountProjectDirectorPersonUserId = projectDirector.getPrincipalName();
	                // KULEFR-206
	                // String actionRequestOfDirector = this.getActionRequest(routeLevelName,
	                // KFSConstants.RouteLevelNames.PROJECT_MANAGEMENT);
	                AdHocRoutePerson adHocRoutePerson = buildAdHocRouteRecipient(accountProjectDirectorPersonUserId, ActionRequestType.APPROVE);
	
	                addAdHocRoutePerson(effortCertificationDocument.getAdHocRoutePersons(), priorApprovers, adHocRoutePerson, isNewLine);
	            }
            }
        }
    }
    
    @Override
    protected void addAdHocRoutePerson(Collection<AdHocRoutePerson> adHocRoutePersonList, Set<Person> priorApprovers, AdHocRoutePerson adHocRoutePerson, boolean isNewLine) {
        boolean canBeAdded = true;
        
        // We don't want to adHoc route to the current user
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        String currentPrincipalName = currentUser.getPrincipalName();
        String adHocName = adHocRoutePerson.getId();
        if (StringUtils.isNotEmpty(currentPrincipalName)) {
            if ((StringUtils.equals(currentPrincipalName, adHocName))) {
                canBeAdded = false;
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
   
    protected boolean isDocumentStoppedInRouteNode(WorkflowDocument workflowDocument, String nodeName) {
        Set<String> names = workflowDocument.getCurrentNodeNames();
        if (CollectionUtils.isNotEmpty(names)) {
            List<String> currentRouteLevels = new ArrayList<String>(names);
            if (currentRouteLevels.contains(nodeName) && workflowDocument.isApprovalRequested()) {
                return true;
            }
        }
        return false;
    }
}