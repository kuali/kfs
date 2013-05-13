/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import org.kuali.kfs.sec.document.authorization.SecTransactionalDocumentAuthorizer;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.DocumentHelperServiceImpl;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;


/**
 * Override of document helper service to return a custom document authorizer for document types that have access security restrictions
 */
public class SecDocumentHelperServiceImpl extends DocumentHelperServiceImpl {
    private AccessSecurityService accessSecurityService;

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
        if (getAccessSecurityService().isAccessSecurityControlledDocumentType(documentType)) {
            if  ( configuredDocumentAuthorizer instanceof TransactionalDocumentAuthorizer ) {
                try {
                    SecTransactionalDocumentAuthorizer secDocumentAuthorizer = SecTransactionalDocumentAuthorizer.class.newInstance();
                    secDocumentAuthorizer.setDocumentAuthorizer((TransactionalDocumentAuthorizer) configuredDocumentAuthorizer);

                    return secDocumentAuthorizer;
                } catch (Exception e) {
                    throw new RuntimeException("Unable to create new instance of SecTransactionalDocumentAuthorizer for document type: " + documentType, e);
                }
            } else {
                throw new RuntimeException( "Original DocumentAuthorizer for " + documentType + " is not an instance of " + TransactionalDocumentAuthorizer.class.getName() + ". It can not be wrapped.  This is a configuration error." );
            }
        }

        return configuredDocumentAuthorizer;
    }

    public AccessSecurityService getAccessSecurityService() {
        if ( accessSecurityService == null ) {
            accessSecurityService = SpringContext.getBean(AccessSecurityService.class);
        }
        return accessSecurityService;
    }

}
