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
package org.kuali.kfs.module.cab.document.web.struts;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceKeyConstants;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionForm;
import org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.kns.util.KNSConstants;

public class PurApLineAction extends KualiAction {
    private static final Logger LOG = Logger.getLogger(PurApLineAction.class);
    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    
    public ActionForward start(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm)form;
        PurApLineService purApLineService = SpringContext.getBean(PurApLineService.class);
        purApLineService.setPurApInformation(purApLineForm,request);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm)form;

        //TODO: if yes button clicked - save the doc
        
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Purchasing/Account Payable Transactions ");

        return mapping.findForward(KFSConstants.MAPPING_PORTAL);
    }
    
    /**
     * Handling for screen close. Default action is return to caller.
     * 
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurApLineForm purApLineForm = (PurApLineForm)form;
        
        Object question = request.getParameter(KNSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = KNSServiceLocator.getKualiConfigurationService();

        // logic for close question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, kualiConfiguration.getPropertyString(RiceKeyConstants.QUESTION_SAVE_BEFORE_CLOSE), KNSConstants.CONFIRMATION_QUESTION, KNSConstants.MAPPING_CLOSE, "");
        }
        else {
            Object buttonClicked = request.getParameter(KNSConstants.QUESTION_CLICKED_BUTTON);
            if ((KNSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                //TODO: if yes button clicked - save the doc
            }
            // else go to close logic below
        }
        
        return returnToSender(mapping, purApLineForm);
    }
    

    /**
     * 
     * This method...
     * @param mapping
     * @param purApLineForm
     * @return
     */
    protected ActionForward returnToSender(ActionMapping mapping, PurApLineForm purApLineForm) {
        return mapping.findForward(KNSConstants.MAPPING_PORTAL);
    }
    
    //TODO:
    public ActionForward splitItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    //TODO:
    public ActionForward merge(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    //TODO:
    public ActionForward percentPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    //TODO:
    public ActionForward allocate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    //TODO:
    public ActionForward submit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
