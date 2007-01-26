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
package org.kuali.module.cg.rules;

import java.util.Collection;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.module.cg.bo.Proposal;
import org.kuali.module.cg.bo.ProposalOrganization;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants;

/**
 * Rules for the Proposal maintenance document.
 */
public class ProposalRule extends MaintenanceDocumentRuleBase {
    
    // private Proposal oldProposal;
    private Proposal newProposal;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        processCustomRouteDocumentBusinessRules(document);
        return true;  // save dispite error messages
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        setupConvenienceObjects();
        boolean success = true;
        success &= checkOrgs();
        return success;
    }

    private boolean checkOrgs() {
        boolean success = true;
        boolean foundPrimary = false;
        int n = 0;
        for (ProposalOrganization po : newProposal.getProposalOrganizations()) {
            String propertyName = PropertyConstants.PROPOSAL_ORGANIZATIONS + "[" + (n++) + "]." + PropertyConstants.ORGANIZATION_CODE;
            if (po.isProposalPrimaryOrganizationIndicator()) {
                if (foundPrimary) {
                    putFieldError(PropertyConstants.PROPOSAL_ORGANIZATIONS, KeyConstants.ERROR_MULTIPLE_PRIMARY_ORGS);
                    success = false;
                }
                foundPrimary = true;
            }
            if (countObjectsWithIdentitcalKey(newProposal.getProposalOrganizations(), po) > 1) {
                putFieldError(propertyName, KeyConstants.ERROR_DUPLICATE_ORGS, po.getChartOfAccountsCode(), po.getOrganizationCode());
                success = false;
            }
            else {
                po.refreshNonUpdateableReferences();
                if (ObjectUtils.isNull(po.getOrganization())) {
                    // todo: add an attribute to some DD for the organization label?  e.g., getFieldLabel(ProposalOrganization.class, PropertyConstants.ORGANIZATION)
                    putFieldError(propertyName, KeyConstants.ERROR_EXISTENCE, PropertyConstants.ORGANIZATION);
                    success = false;
                }
            }
        }
        if (!foundPrimary) {
            putFieldError(PropertyConstants.PROPOSAL_ORGANIZATIONS, KeyConstants.ERROR_NO_PRIMARY_ORG);
            success = false;
        }
        return success;
    }

    public void setupConvenienceObjects() {
        // oldProposal = (Proposal) super.getOldBo();
        newProposal = (Proposal) super.getNewBo();
    }

    /**
     * Compares a business object with a Collection of BOs to count how many have the same key as the BO.
     *
     * @param collection - The collection of items to check
     * @param bo - The BO whose keys we are looking for in the collection
     * @return how many have the same keys
     */
    private static int countObjectsWithIdentitcalKey(Collection<? extends PersistableBusinessObject> collection, PersistableBusinessObject bo) {
        // todo: move this method to ObjectUtils (and genericize collectionContainsObjectWithIdentitcalKey() to leverage this method?)
        int n = 0;
        for (PersistableBusinessObject item : collection) {
            if (ObjectUtils.equalByKeys(item, bo)) {
                n++;
            }
        }
        return n;
    }
    
    // todo: change the super method to accept var args
    @Override
    protected void putFieldError(String propertyName, String errorConstant, String... parameters) {
        super.putFieldError(propertyName, errorConstant, parameters);
    }

    // todo: change the super method to accept var args
    @Override
    protected void putDocumentError(String propertyName, String errorConstant, String... parameters) {
        super.putDocumentError(propertyName, errorConstant, parameters);
    }
}
