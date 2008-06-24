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
package org.kuali.kfs.sys.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentActionFlags;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.util.RiceConstants;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentActionBase extends KualiTransactionalDocumentActionBase {

    /**
     * This action method triggers a correct of the transactional document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward correct(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();
        FinancialSystemTransactionalDocumentActionFlags flags = getDocumentActionFlags(document);
        if (!flags.getCanErrorCorrect()) {
            throw buildAuthorizationException("error correct", document);
        }

        ((Correctable) tmpForm.getTransactionalDocument()).toErrorCorrection();

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Convenience method for retrieving current DocumentActionFlags
     *
     * @param document
     */
    @Override
    protected FinancialSystemTransactionalDocumentActionFlags getDocumentActionFlags(Document document) {
        UniversalUser kualiUser = GlobalVariables.getUserSession().getUniversalUser();

        DocumentAuthorizationService documentAuthorizationService = KNSServiceLocator.getDocumentAuthorizationService();
        FinancialSystemTransactionalDocumentActionFlags flags = new FinancialSystemTransactionalDocumentActionFlags(documentAuthorizationService.getDocumentAuthorizer(document).getDocumentActionFlags(document, kualiUser));

        return flags;
    }

}
