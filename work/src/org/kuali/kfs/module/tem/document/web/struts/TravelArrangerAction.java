/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import static org.kuali.kfs.module.tem.TemConstants.TEM_PROFILE_LOOKUPABLE;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.tem.document.TravelArrangerDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.util.ObjectUtils;

public class TravelArrangerAction extends KualiTransactionalDocumentActionBase {

    public static Logger LOG = Logger.getLogger(TravelArrangerAction.class);

    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        final TravelArrangerDocument document = (TravelArrangerDocument) kualiDocumentFormBase.getDocument();
        document.initiateDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelArrangerForm arrgrForm = (TravelArrangerForm) form;
        String refreshCaller = arrgrForm.getRefreshCaller();

        LOG.debug("refresh call is:  " + refreshCaller);

        ActionForward actionAfterTravelerLookup = this.refreshAfterProfileLookup(mapping, arrgrForm, request);
        if (actionAfterTravelerLookup != null) {
            return actionAfterTravelerLookup;
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * This method is called during a refresh from lookup, it checks to see if it is being called for Group Traveler or the initial
     * Traveler lookup
     *
     * @param mapping
     * @param reqForm
     * @param request
     * @return null, no special page to return to
     */
    protected ActionForward refreshAfterProfileLookup(ActionMapping mapping, TravelArrangerForm arrangerForm, HttpServletRequest request) {
        String refreshCaller = arrangerForm.getRefreshCaller();

        boolean isProfileLookupable = StringUtils.equals(refreshCaller, TEM_PROFILE_LOOKUPABLE);

        // if a cancel occurred on address lookup we need to reset the payee id and type, rest of fields will still have correct
        // information
        if (refreshCaller == null) {
            return null;
        }

        // do not execute the further refreshing logic if the refresh caller is not a traveler profile lookupable
        if (!isProfileLookupable) {
            return null;
        }

        TravelArrangerDocument doc = (TravelArrangerDocument)arrangerForm.getDocument();
        doc.refreshReferenceObject("profile");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        TravelArrangerForm arrgrForm = (TravelArrangerForm) form;
        TravelArrangerDocument doc = (TravelArrangerDocument)arrgrForm.getDocument();
        if(doc.getResign()) {
            if(ObjectUtils.isNull(doc.getProfile())) {
                doc.refreshReferenceObject("profile");
            }
            if(ObjectUtils.isNotNull(doc.getProfile().getPrincipalId())) {
                getTravelDocumentService().addAdHocFYIRecipient(doc, doc.getProfile().getPrincipalId());
            }
        }

        return super.route(mapping, form, request, response);
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }


}
