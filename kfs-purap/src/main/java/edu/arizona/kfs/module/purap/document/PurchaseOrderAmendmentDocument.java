package edu.arizona.kfs.module.purap.document;

import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapWorkflowConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PurchaseOrderAmendmentDocument extends org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument {
    private static final long serialVersionUID = -1995809378517425032L;

    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(PurapWorkflowConstants.AMOUNT_REQUIRES_SEPARATION_OF_DUTIES_REVIEW_SPLIT)) {
            boolean isSeparationOfDutiesReviewRequired = isSeparationOfDutiesReviewRequired();
            LOG.debug("isSeparationOfDutiesReviewRequired=" + isSeparationOfDutiesReviewRequired);
            return isSeparationOfDutiesReviewRequired;
        }
        if (nodeName.equals(PurapWorkflowConstants.CONTRACT_MANAGEMENT_REVIEW_REQUIRED)) {
            boolean isContractManagementReviewRequired = isContractManagementReviewRequired();
            LOG.debug("isSeparationOfDutiesReviewRequired=" + isContractManagementReviewRequired);
            return isContractManagementReviewRequired;
        }
        boolean superAnswerSplitNodeQuestion = super.answerSplitNodeQuestion(nodeName);
        return superAnswerSplitNodeQuestion;
    }

    public Set<Person> getAllPriorApprovers() throws WorkflowException {
        PersonService personService = KimApiServiceLocator.getPersonService();
        List<ActionTaken> actionsTaken = this.getFinancialSystemDocumentHeader().getWorkflowDocument().getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();

        for (ActionTaken actionTaken : actionsTaken) {
            if (KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken().getCode())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }

    protected boolean isSeparationOfDutiesReviewRequired() {
        try {
            Set<Person> priorApprovers = this.getAllPriorApprovers();

            // If there are more than 1 prior approvers there is no need for SOD.
            // If 1 approver exists, check that approver is not the initiator.
            if (priorApprovers.size() > 0) {
                if (priorApprovers.size() > 1) {
                    return false;
                } else {
                    for (Person priorApprover : priorApprovers) {
                        String initiatorPrincipalId = this.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
                        if (!initiatorPrincipalId.equals(priorApprover.getPrincipalId())) {
                            return false;
                        }
                    }
                }
            }
            // If there was no prior approver or if the initiator and the approver are the same person,
            // then we have to check the amounts to determine whether to route to separation of duties.
            ParameterService parameterService = SpringContext.getBean(ParameterService.class);
            KualiDecimal maxAllowedAmount = new KualiDecimal(parameterService.getParameterValueAsString(PurchaseOrderAmendmentDocument.class, PurapParameterConstants.SEPARATION_OF_DUTIES_DOLLAR_AMOUNT));
            // if app param amount is greater than or equal to documentTotalAmount... no need for separation of duties
            KualiDecimal totalAmount = getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount();
            if (ObjectUtils.isNotNull(maxAllowedAmount) && ObjectUtils.isNotNull(totalAmount) && (maxAllowedAmount.compareTo(totalAmount) >= 0)) {
                return false;
            } else {
                return true;
            }
        } catch (WorkflowException we) {
            LOG.error("Exception while attempting to retrieve all prior approvers from workflow: ", we);
        }
        return false;
    }

}
