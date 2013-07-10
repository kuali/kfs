/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.document.CardApplicationDocument;
import org.kuali.kfs.module.tem.document.TemCorporateCardApplicationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

public class TemCardApplicationAction extends FinancialSystemTransactionalDocumentActionBase {

    private final static String CTS_ACTION = "temCTSCardApplication.do";
    private final static String CORP_ACTION = "temCorporateCardApplication.do";
    private final static String QUESTION_FORWARD = "cardQuestion";
    private final static String ERROR_FORWARD = "cardApplicationError";

    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;

        String command = applicationForm.getCommand();
        if (StringUtils.equals(KewApiConstants.INITIATE_COMMAND,command)) {
            final TEMProfile profile = SpringContext.getBean(TemProfileService.class).findTemProfileByPrincipalId(currentUser.getPrincipalId());
            if (profile == null){
                applicationForm.setEmptyProfile(true);
                return mapping.findForward(ERROR_FORWARD);
            } else {
                if (StringUtils.isEmpty(profile.getDefaultAccount())){
                    applicationForm.setEmptyAccount(true);
                    return mapping.findForward(ERROR_FORWARD);
                }
            }
        }

        return super.docHandler(mapping, form, request, response);
    }


    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();
        if (document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus().equals(TemWorkflowConstants.RouteNodeNames.FISCAL_OFFICER_REVIEW)){
            if (document instanceof TemCorporateCardApplicationDocument){
                if (!((TemCorporateCardApplicationDocument)document).isDepartmentHeadAgreement()){
                    GlobalVariables.getMessageMap().putError("departmentHeadAgreement", TemKeyConstants.ERROR_TEM_CARD_APP_NO_AGREEMENT, "Department Head");
                }
            }

        }
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser)
                && document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus().equals(TemWorkflowConstants.RouteNodeNames.APPLIED_TO_BANK)){
            document.sendAcknowledgement();
            document.approvedByBank();
        }
        return super.approve(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#disapprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser)
                && document.getDocumentHeader().getWorkflowDocument().getApplicationDocumentStatus().equals(TemWorkflowConstants.RouteNodeNames.APPLIED_TO_BANK)){
            document.sendAcknowledgement();
        }
        return super.disapprove(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();

        if (applicationForm.getDocument().getDocumentHeader().getWorkflowDocument().isInitiated()){
            if (!document.isUserAgreement()){
                GlobalVariables.getMessageMap().putError("userAgreement", TemKeyConstants.ERROR_TEM_CARD_APP_NO_AGREEMENT, "User");
            }
            if (StringUtils.isEmpty(document.getDocumentHeader().getDocumentDescription())){
                GlobalVariables.getMessageMap().putError("document.documentHeader.documentDescription", RiceKeyConstants.ERROR_REQUIRED, "Description");
            }
        }

        return super.route(mapping, form, request, response);
    }

    public ActionForward applyToBank(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser)){
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), "Apply To Bank",this.getClass().getSimpleName());
        }
        document.applyToBank();
        document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentStatus(TemWorkflowConstants.RouteNodeNames.APPLIED_TO_BANK);
        document.saveAppDocStatus();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    public ActionForward submit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        if (!SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser)){
            throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), "Submit",this.getClass().getSimpleName());
        }
        document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentStatus(TemWorkflowConstants.RouteNodeNames.PENDING_BANK_APPLICATION);
        document.saveAppDocStatus();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }



    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        TemCardApplicationForm applicationForm = (TemCardApplicationForm) kualiDocumentFormBase;
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        TEMProfile profile = SpringContext.getBean(TemProfileService.class).findTemProfileByPrincipalId(currentUser.getPrincipalId());

        CardApplicationDocument document = (CardApplicationDocument)applicationForm.getDocument();
        document.getDocumentHeader().getWorkflowDocument().setApplicationDocumentStatus(TemWorkflowConstants.RouteNodeNames.APPLICATION);
        document.setTemProfile(profile);
        document.setTemProfileId(profile.getProfileId());
        profile.getTravelerTypeCode();
        applicationForm.setInitiator(true);
    }

    /**
     * Initializes the profile on the document
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase form) throws WorkflowException {
        super.loadDocument(form);
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        TemCardApplicationForm applicationForm = (TemCardApplicationForm)form;
        TEMProfile profile = null;
        CardApplicationDocument document = (CardApplicationDocument) applicationForm.getDocument();

        profile = document.getTemProfile();
        //If not the user's profile, check if they are the FO or Travel Manager.
        if (!currentUser.getPrincipalId().equals(profile.getPrincipalId())) {
            if (!applicationForm.isTravelManager() || !applicationForm.isFiscalOfficer()) {
                throw new AuthorizationException(GlobalVariables.getUserSession().getPerson().getPrincipalName(), "view",this.getClass().getSimpleName());
            }
        }
    }


    public ActionForward checkExistingCard(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = null;

        TemCardApplicationForm applicationForm = (TemCardApplicationForm) form;
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        TEMProfile profile = SpringContext.getBean(TemProfileService.class).findTemProfileByPrincipalId(currentUser.getPrincipalId());

        if (profile == null) {
            applicationForm.setEmptyProfile(true);
            return mapping.findForward(ERROR_FORWARD);
        }

        if (profile.getAccounts() != null && profile.getAccounts().size() > 0){
            boolean hasCardType = false;
            for (TEMProfileAccount account : profile.getAccounts()){
                if (account.getCreditCardAgency().getTravelCardTypeCode().equals(TemConstants.TRAVEL_TYPE_CTS) && applicationForm.getDocTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_CTS_CARD_DOCUMENT) ){
                    hasCardType = true;
                    break;
                }
                else if (account.getCreditCardAgency().getTravelCardTypeCode().equals(TemConstants.TRAVEL_TYPE_CORP) && applicationForm.getDocTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_CORP_CARD_DOCUMENT)){
                    hasCardType = true;
                    break;
                }
            }
            if (hasCardType){
                ActionMessage message = new ActionMessage(TemKeyConstants.MESSAGE_CARD_EXISTS_PROMPT);
                ActionMessages messages = new ActionMessages();
                messages.add(ActionMessages.GLOBAL_MESSAGE, message);
                saveMessages(request, messages);
                forward = mapping.findForward("QUESTION_FORWARD");
            }

        }
        if (forward == null) {
            String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.DOC_HANDLER_METHOD);
            parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.INITIATE_COMMAND);
            parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, applicationForm.getDocTypeName());

            String action = applicationForm.getDocTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_CTS_CARD_DOCUMENT) ? CTS_ACTION : CORP_ACTION;

            String lookupUrl = UrlFactory.parameterizeUrl(basePath +"/"+ action, parameters);
            forward = new ActionForward(lookupUrl, true);

        }
        return forward;

    }

    /**
     * Returns the user to the index page.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward returnToIndex(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_PORTAL);
    }

    public ActionForward openNew(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TemCardApplicationForm applicationForm = (TemCardApplicationForm) form;
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.DOC_HANDLER_METHOD);
        parameters.put(KFSConstants.PARAMETER_COMMAND, KewApiConstants.INITIATE_COMMAND);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, applicationForm.getDocTypeName());

        String action = applicationForm.getDocTypeName().equals(TemConstants.TravelDocTypes.TRAVEL_CTS_CARD_DOCUMENT) ? CTS_ACTION : CORP_ACTION;

        String lookupUrl = UrlFactory.parameterizeUrl(basePath +"/"+ action, parameters);

        return new ActionForward(lookupUrl, true);
    }

}
