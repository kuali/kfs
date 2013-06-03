/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.web.struts;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ProjectCodeImportDetail;
import org.kuali.kfs.coa.document.ProjectCodeImportDocument;
import org.kuali.kfs.sys.document.web.struts.MassImportTransactionalDocumentActionBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 */
public class ProjectCodeImportAction extends MassImportTransactionalDocumentActionBase {
    private static final Logger LOG = Logger.getLogger(ProjectCodeImportAction.class);

    /**
     * Overrides the supper method so that project manager names can be pre-populated into the details
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        // set user principal name from universalID
        ProjectCodeImportDocument document = (ProjectCodeImportDocument) kualiDocumentFormBase.getDocument();

        for (ProjectCodeImportDetail importDetail : document.getProjectCodeImportDetails()) {
            Person projectManager = importDetail.getProjectManagerUniversal();

            if (StringUtils.isBlank(importDetail.getProjectManagerPrincipalName()) && ObjectUtils.isNotNull(projectManager)) {
                importDetail.setProjectManagerPrincipalName(projectManager.getPrincipalName());
            }
        }
    }
}
