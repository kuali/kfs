/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.action;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentPresentationController;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class handles UI actions for all shared methods of transactional documents.
 */
public class KualiTransactionalDocumentActionBase extends KualiDocumentActionBase {
//    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiTransactionalDocumentActionBase.class);

    /**
     * Method that will take the current document and call its copy method if Copyable.
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiTransactionalDocumentFormBase tmpForm = (KualiTransactionalDocumentFormBase) form;

        Document document = tmpForm.getDocument();
        
        if (!tmpForm.getDocumentActions().containsKey(KRADConstants.KUALI_ACTION_CAN_COPY)) {
            throw buildAuthorizationException("copy", document);
        }

        ((Copyable) tmpForm.getTransactionalDocument()).toCopy();

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    @SuppressWarnings("unchecked")
	protected void populateAuthorizationFields(KualiDocumentFormBase formBase){
    	super.populateAuthorizationFields(formBase);
    	Document document = formBase.getDocument();
    	Map editMode = new HashMap();
    	
    	if (formBase.isFormDocumentInitialized()) {
        	Person user = GlobalVariables.getUserSession().getPerson();
        	
        	TransactionalDocumentPresentationController documentPresentationController = (TransactionalDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(document);
            TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) KNSServiceLocator
                    .getDocumentHelperService().getDocumentAuthorizer(document);
            Set<String> editModes = documentAuthorizer.getEditModes(document, user, documentPresentationController.getEditModes(document));
            editMode = this.convertSetToMap(editModes);
            if (getDataDictionaryService().getDataDictionary().getDocumentEntry(document.getClass().getName()).getUsePessimisticLocking()) {
                editMode = getPessimisticLockService().establishLocks(document, editMode, user);
            }            
    	}
    	formBase.setEditingMode(editMode);
    }
}
