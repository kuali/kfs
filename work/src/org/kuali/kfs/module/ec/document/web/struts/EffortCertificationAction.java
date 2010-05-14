/*
 * Copyright 2005-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.web.struts;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailLineOverride;
import org.kuali.kfs.sys.businessobject.AccountingLineOverride;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * To define Actions for EffortCertification document.
 */
public class EffortCertificationAction extends FinancialSystemTransactionalDocumentActionBase {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase#populateAuthorizationFields(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void populateAuthorizationFields(KualiDocumentFormBase formBase){
        super.populateAuthorizationFields(formBase);
        
        Map<String, String> documentActions = formBase.getDocumentActions();
        
        String principalId = UserSession.getAuthenticatedUser().getPrincipalId();
        try {
            if (formBase.isFormDocumentInitialized() && formBase.isDocumentEnRoute()) {
                Set<Person> priorApprovers = formBase.getDocument().getDocumentHeader().getWorkflowDocument().getAllPriorApprovers();
                for (Person priorApprover : priorApprovers) {
                    if (principalId.equals(priorApprover.getPrincipalId())) {
                        documentActions.put(KNSConstants.KUALI_ACTION_CAN_EDIT, "true");
                        documentActions.put(KNSConstants.KUALI_ACTION_CAN_SAVE, "true");
                    }
                 }
            }
        }
        catch (WorkflowException wfe) {
            throw new RuntimeException("Unable to retrieve prior Approvers list");
        }
    }
    
    
    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    protected boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
    }

    /**
     * Processes detail line overrides for output to JSP
     */
    protected void processDetailLineOverrides(List<EffortCertificationDetail> detailLines) {
        if (!detailLines.isEmpty()) {
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(detailLines, EffortCertificationDetailLineOverride.REFRESH_FIELDS);

            for (EffortCertificationDetail detailLine : detailLines) {
                EffortCertificationDetailLineOverride.processForOutput(detailLine);
            }
        }
    }

    /**
     * For the given detail line, set the corresponding override code
     * 
     * @param line the given detail line
     */
    protected void updateDetailLineOverrideCode(EffortCertificationDetail detailLine) {
        AccountingLineOverride override = EffortCertificationDetailLineOverride.determineNeededOverrides(detailLine);
        detailLine.setOverrideCode(override.getCode());
    }
}
