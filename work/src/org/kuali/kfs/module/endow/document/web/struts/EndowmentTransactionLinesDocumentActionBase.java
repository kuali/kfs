/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.document.EndowmentSecurityDetailsDocumentBase;
import org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.RegistrationCodeService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.validation.event.AddTransactionLineEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.PersistenceService;

public abstract class EndowmentTransactionLinesDocumentActionBase extends FinancialSystemTransactionalDocumentActionBase {
    
    private static final String SECURITY_REFRESH = "document.sourceTransactionSecurity.securityID";
    private static final String REGISTRATION_REFRESH = "document.sourceTransactionSecurity.registrationCode";

    /**
     * This action executes an insert of an EndowmentSourceTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward insertSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetIncreaseDocumentForm documentForm = (AssetIncreaseDocumentForm) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentTransactionLine transLine = documentForm.getNewSourceTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent("", EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            // insertAccountingLine(true, financialDocumentForm, line);
            endowmentDocument.getSourceTransactionLines().add(transLine);

            // clear the used newTargetLine
            documentForm.setNewSourceTransactionLine(new EndowmentSourceTransactionLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action executes an insert of an EndowmentTargetTransactionLine into a document only after validating the Transaction
     * line and checking any appropriate business rules.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AssetIncreaseDocumentForm documentForm = (AssetIncreaseDocumentForm) form;
        EndowmentTransactionLinesDocument endowmentDocument = (EndowmentTransactionLinesDocument) documentForm.getDocument();

        EndowmentTransactionLine transLine = documentForm.getNewSourceTransactionLine();

        boolean rulePassed = true;

        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddTransactionLineEvent("", EndowConstants.NEW_SOURCE_TRAN_LINE_PROPERTY_NAME, endowmentDocument, transLine));

        if (rulePassed) {
            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(transLine);
            // insertAccountingLine(true, financialDocumentForm, line);
            endowmentDocument.getSourceTransactionLines().add(transLine);

            // clear the used newTargetLine
            documentForm.setNewSourceTransactionLine(new EndowmentSourceTransactionLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action deletes an EndowmentSourceTransactionLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteSourceTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This action deletes an EndowmentTargetTransactionLine from a document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteTargetTransactionLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {


        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        // To Determine if the refresh is coming from Security lookup
        if (request.getParameterMap().containsKey(SECURITY_REFRESH)) {
            refreshSecurityDetails(mapping, form, request, response);
        }

        // To Determine if the refresh is coming from Registration lookup
        if (request.getParameterMap().containsKey(REGISTRATION_REFRESH)) {
            refreshRegistrationDetails(mapping, form, request, response);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward refreshSecurityDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception 
    {
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = ( EndowmentSecurityDetailsDocumentBase) ((EndowmentTransactionLinesDocumentFormBase) form).getDocument() ;

        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().getSecurityID());
        ClassCode classCode = SpringContext.getBean(ClassCodeService.class).getByPrimaryKey(security.getSecurityClassCode());
        security.setClassCode(classCode);
        EndowmentTransactionCode endowmentTransactionCode  = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(classCode.getSecurityEndowmentTransactionCode());
        classCode.setEndowmentTransactionCode(endowmentTransactionCode);
        endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().setSecurity(security);

        return null;

    }
    
    public ActionForward refreshRegistrationDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception 
    {
        EndowmentSecurityDetailsDocumentBase endowmentSecurityDetailsDocumentBase = ( EndowmentSecurityDetailsDocumentBase) ((EndowmentTransactionLinesDocumentFormBase) form).getDocument() ;

        RegistrationCode registrationCode = SpringContext.getBean(RegistrationCodeService.class).getByPrimaryKey(endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().getRegistrationCode());
        endowmentSecurityDetailsDocumentBase.getSourceTransactionSecurity().setRegistrationCodeObj(registrationCode);
        
        return null;

    }
}
