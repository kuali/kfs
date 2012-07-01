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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * This class...
 */
public class FinancialSystemTransactionalDocumentActionBase extends KualiTransactionalDocumentActionBase {
    public static final String MODULE_LOCKED_MESSAGE = "moduleLockedMessage";
    public static final String MODULE_LOCKED_URL_SUFFIX = "/moduleLocked.do";

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

