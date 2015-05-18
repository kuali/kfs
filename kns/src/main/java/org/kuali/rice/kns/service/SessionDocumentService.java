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
package org.kuali.rice.kns.service;

import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.UserSession;

import java.sql.Timestamp;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Deprecated
public interface SessionDocumentService {

    /**
     * Retrieves a document from the user session for the given document id
     */
    public WorkflowDocument getDocumentFromSession(UserSession userSession, String docId);

    /**
     * This method places a document into the user session.
     */
    public void addDocumentToUserSession(UserSession userSession, WorkflowDocument document);

    /**
     * Delete KualiDocumentFormBase from session and database.
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @throws
     */
    public void purgeDocumentForm(String documentNumber, String docFormKey, UserSession userSession, String ipAddress);

    /**
     * Delete KualiDocumentFormBases from database.
     *
     * @param documentNumber
     * @throws
     */
    public void purgeAllSessionDocuments(Timestamp expirationDate);

    /**
     * Returns KualiDocumentFormBase object. It will check userSession first, if it failed then check database
     *
     * @param documentNumber
     * @param docFormKey
     * @param userSession
     * @return KualiDocumentFormBase
     * @throws
     */
    public KualiDocumentFormBase getDocumentForm(String documentNumber, String docFormKey, UserSession userSession,
            String ipAddress);

    /**
     * Store KualiDocumentFormBase into session and database.
     *
     * @param KualiDocumentFormBase
     * @param userSession
     * @throws
     */
    public void setDocumentForm(KualiDocumentFormBase form, UserSession userSession, String ipAddress);
}
