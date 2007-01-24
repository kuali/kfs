/*
 * Copyright (c) 2005, 2006 The National Association of College and University Business Officers,
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
package org.kuali.module.cg.maintenance;

import java.util.Map;

import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.cg.bo.Proposal;

/**
 * Methods for the Proposal maintenance document UI.
 */
public class ProposalMaintainableImpl extends KualiMaintainableImpl {

    /**
     * This method is called for refreshing the Agency before display to show the full name
     * in case the agency number was changed by hand before any submit that causes a redisplay.
     */
    @Override
    public void processAfterRetrieve() {
        refreshProposal();
        super.processAfterRetrieve();
    }

    /**
     * This method is called for refreshing the Agency before a save to display the full name
     * in case the agency number was changed by hand just before the save.
     */
    @Override
    public void prepareForSave() {
        refreshProposal();
        super.prepareForSave();
    }

    /**
     *  This method is called for refreshing the Agency after a lookup to display its full name without AJAX.
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        refreshProposal();
        super.refresh(refreshCaller, fieldValues, document);
    }

    private void refreshProposal() {
        getProposal().refreshNonUpdateableReferences();
    }
    
    private Proposal getProposal() {
        return (Proposal) getBusinessObject();
    }
}
