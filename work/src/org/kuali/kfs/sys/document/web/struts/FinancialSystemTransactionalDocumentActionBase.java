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
package org.kuali.kfs.sys.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentActionBase extends KualiTransactionalDocumentActionBase {
    public static final String MODULE_LOCKED_MESSAGE = "moduleLockedMessage";
    public static final String MODULE_LOCKED_URL_SUFFIX = "/moduleLocked.do";

    
    /**
     * save the document without any validations.....
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        FinancialSystemTransactionalDocument financialSystemTransactionalDocument = (FinancialSystemTransactionalDocument) kualiDocumentFormBase.getDocument();
        
        //clear any error messages but there should not be any currently.
        GlobalVariables.getMessageMap().clearErrorMessages();
        SpringContext.getBean(FinancialSystemDocumentService.class).saveDocumentNoValidation(financialSystemTransactionalDocument);        
        
        KNSGlobalVariables.getMessageList().add(RiceKeyConstants.MESSAGE_SAVED);
        kualiDocumentFormBase.setAnnotation("");
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Method that will take the current document and call its copy method if Copyable.
     */
    @Override
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.copy(mapping, form, request, response);
        
        KualiAccountingDocumentFormBase kadfb = (KualiAccountingDocumentFormBase) form;
        String templateNumber = kadfb.getDocument().getDocumentHeader().getDocumentTemplateNumber();
        
        createNoteForCopy(templateNumber, kadfb.getDocument().getNotes());
        
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * removes existing notes copied during copy operation and creates a new using
     * the document number of the copied document.
     * 
     * @param documentNumber
     * @param notes
     */
    public void createNoteForCopy(String documentNumber, List<Note> notes) {
        String noteText = "Copied from document: " + documentNumber;
        String authorUniversalIdentifier = GlobalVariables.getUserSession().getPrincipalId();
        Note copyNote = new Note();
     
        notes.clear();
        
        copyNote.setNoteText(noteText);
        copyNote.setNotePostedTimestampToCurrent();
        copyNote.setAuthorUniversalIdentifier(authorUniversalIdentifier);
        
        notes.add(copyNote);
    }
    
    /**
     * This action method triggers a correct of the transactional document.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     * KRAD Conversion: Customizing the extra buttons on the form
     */
    public ActionForward correct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();

        ((Correctable) tmpForm.getTransactionalDocument()).toErrorCorrection();
        tmpForm.setExtraButtons(new ArrayList<ExtraButton>());

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    @Override
    /**
     * KFSMI-4452
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


        ActionForward returnForward =  super.execute(mapping, form, request, response);
        if( isDocumentLocked(mapping, form, request)) {
            String message  = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KFSConstants.CoreModuleNamespaces.KFS, KfsParameterConstants.PARAMETER_ALL_DETAIL_TYPE, KFSConstants.DOCUMENT_LOCKOUT_DEFAULT_MESSAGE);
            request.setAttribute(MODULE_LOCKED_MESSAGE, message);
            returnForward = mapping.findForward(RiceConstants.MODULE_LOCKED_MAPPING);
        }

        return returnForward;

    }

    /**
     * KFSMI-4452
     */
    protected boolean isDocumentLocked(ActionMapping mapping , ActionForm form, HttpServletRequest request) {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;
        Document document = tmpForm.getDocument();

        boolean exists = SpringContext.getBean(ParameterService.class).parameterExists(document.getClass(),KFSConstants.DOCUMENT_LOCKOUT_PARM_NM );
        if(exists) {
            boolean documentLockedOut = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(document.getClass(),KFSConstants.DOCUMENT_LOCKOUT_PARM_NM );
            if(documentLockedOut) {
                return tmpForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_EDIT);
            }
        }

        return false;
    }
}

