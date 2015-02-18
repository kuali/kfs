/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
