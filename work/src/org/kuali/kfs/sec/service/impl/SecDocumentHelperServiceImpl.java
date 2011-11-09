/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import java.util.List;

import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.document.authorization.SecTransactionalDocumentAuthorizer;
import org.kuali.kfs.sys.service.impl.DocumentHelperServiceImpl;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.core.framework.parameter.ParameterService; import java.util.ArrayList;


/**
 * Override of document helper service to return a custom document authorizer for document types that have access security restrictions
 */
public class SecDocumentHelperServiceImpl extends DocumentHelperServiceImpl {
    protected ParameterService parameterService;

    /**
     * Checks to see if the document type has access security restrictions and if so returns a new SecTransactionalDocumentAuthorizer instance, otherwise returns the document
     * authorizer configured in the data dictionary
     * 
     * @see org.kuali.rice.krad.service.impl.DocumentHelperServiceImpl#getDocumentAuthorizer(java.lang.String)
     * @see org.kuali.kfs.sec.document.authorization.SecTransactionalDocumentAuthorizer
     */
    @Override
    public DocumentAuthorizer getDocumentAuthorizer(String documentType) {
        // get document authorizer instance configured in data dictionary
        DocumentAuthorizer configuredDocumentAuthorizer = super.getDocumentAuthorizer(documentType);

        // list of document types configured for access security
        List<String> documentTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES) );

        if (documentTypes.contains(documentType)) {
            try {
                DocumentAuthorizer secDocumentAuthorizer = SecTransactionalDocumentAuthorizer.class.newInstance();
                ((SecTransactionalDocumentAuthorizer) secDocumentAuthorizer).setDocumentAuthorizer((TransactionalDocumentAuthorizer) configuredDocumentAuthorizer);

                return secDocumentAuthorizer;
            }
            catch (Exception e) {
                throw new RuntimeException("Unable to create new instance of SecTransactionalDocumentAuthorizer: " + e.getMessage(), e);
            }
        }

        return configuredDocumentAuthorizer;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
