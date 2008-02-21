/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.effort.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.event.LoadDetailLineEvent;
import org.kuali.module.effort.service.EffortCertificationDocumentService;
import org.kuali.module.effort.service.EffortCertificationExtractService;
import org.kuali.module.effort.web.struts.form.EffortCertificationForm;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;

/**
 * This class handles Actions for EffortCertification.
 */
public class EffortCertificationAction extends KualiTransactionalDocumentActionBase {
    
    public ActionForward initiate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
        
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EffortCertificationForm effortForm = (EffortCertificationForm) form;
        EffortCertificationDocument document = (EffortCertificationDocument) effortForm.getDocument();
        //document.getEffortCertificationDetailLines().get();
        //EffortCertificationDetail document.g
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward add(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /*@Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        super.docHandler(mapping, form, request, response);
        
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }*/
        
    /**
     * load the detail lines with the given information 
     */
    public ActionForward loadDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        EffortCertificationForm effortCertificationForm = (EffortCertificationForm) form;
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) effortCertificationForm.getDocument();
        
        boolean isRulePassed = this.invokeRules(new LoadDetailLineEvent("", "", effortCertificationDocument));   

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }  
    
    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    private boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
    }
}