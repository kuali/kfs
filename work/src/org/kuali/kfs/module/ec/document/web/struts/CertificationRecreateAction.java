/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceConstants;
import org.kuali.RiceKeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.effort.bo.EffortCertificationDetail;
import org.kuali.module.effort.document.EffortCertificationDocument;
import org.kuali.module.effort.rule.event.LoadDetailLineEvent;
import org.kuali.module.effort.web.struts.form.CertificationRecreateForm;
import org.kuali.rice.KNSServiceLocator;

public class CertificationRecreateAction extends KualiTransactionalDocumentActionBase {

    /**
     * load the detail lines with the given information
     */
    public ActionForward loadDetailLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CertificationRecreateForm certificationRecreateForm = (CertificationRecreateForm) form;
        EffortCertificationDocument effortCertificationDocument = (EffortCertificationDocument) certificationRecreateForm.getDocument();

        boolean isRulePassed = this.invokeRules(new LoadDetailLineEvent("", "", effortCertificationDocument));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * execute the rules associated with the given event
     * 
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, fals
     */
    private boolean invokeRules(KualiDocumentEvent event) {
        return SpringContext.getBean(KualiRuleService.class).applyRules(event);
    }
}
