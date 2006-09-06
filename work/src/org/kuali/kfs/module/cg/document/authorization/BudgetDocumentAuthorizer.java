/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.document;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.authorization.DocumentActionFlags;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentAuthorizerBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.service.BudgetPermissionsService;

/**
 * DocumentAuthorizer class for KRA Budget Documents.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetDocumentAuthorizer extends DocumentAuthorizerBase {
    private static Log LOG = LogFactory.getLog(BudgetDocumentAuthorizer.class);
    
    /**
     * @see org.kuali.core.authorization.DocumentAuthorizer#getEditMode(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public Map getEditMode(Document d, KualiUser u) {
        Map editModeMap = new HashMap();
        editModeMap.put(SpringServiceLocator.getBudgetPermissionsService().getUserPermissionCode((BudgetDocument) d, u), "TRUE");
        return editModeMap;
    }
    
    /**
     * Overrides most of the inherited flags so that the buttons behave exactly like they used to in the obsoleted
     * budgetDocumentControls.tag
     * 
     * @see org.kuali.core.authorization.DocumentAuthorizer#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    public DocumentActionFlags getDocumentActionFlags(Document document, KualiUser user) {
        LOG.debug("calling BudgetDocumentAuthorizer.getDocumentActionFlags");

        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        flags.setCanAcknowledge(false);
        flags.setCanApprove(false);
        flags.setCanBlanketApprove(false);
        flags.setCanCancel(false);
        flags.setCanDisapprove(false);
        flags.setCanFYI(false);
        flags.setCanClose(false);

        BudgetDocument budgetDocument = (BudgetDocument) document;
        
        
        if (user.getPersonUniversalIdentifier().equals(budgetDocument.getBudget().getBudgetProjectDirectorSystemId())) {
            flags.setCanSave(true);
        }

        // else use inherited canSave, canRoute, canAnnotate, and canReload values

        return flags;
    }
}
