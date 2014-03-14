/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Returns the active fund managers of a given award using the proposal number. *
 */
public class FundsManagerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(FundsManagerDerivedRoleTypeServiceImpl.class);
    protected DocumentService documentService;
    protected KualiModuleService kualiModuleService;

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        List<RoleMembership> roleMembers = new ArrayList<RoleMembership>();

        if (ObjectUtils.isNotNull(qualification) && !qualification.isEmpty()) {
            String documentNumber = qualification.get("documentNumber");
            if (StringUtils.isNotBlank(documentNumber)) {
                ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument) getDocument(documentNumber);
                ContractsAndGrantsBillingAward award = contractsGrantsInvoiceDocument.getAward();
            
                Map<String, Object> map = new HashMap<String, Object>();
                map.put(KFSPropertyConstants.ACTIVE, true);
                map.put(KFSPropertyConstants.PROPOSAL_NUMBER, award.getProposalNumber());
                List<ContractsAndGrantsFundManager> awardFundManagers = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsFundManager.class).getExternalizableBusinessObjectsList(ContractsAndGrantsFundManager.class, map);
                for (ContractsAndGrantsFundManager awardFundManager : awardFundManagers) {
                    roleMembers.add(RoleMembership.Builder.create(null, null, awardFundManager.getPrincipalId(), MemberType.PRINCIPAL, null).build());
                }
            }
        }
        
        return roleMembers;
    }
    
    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String,String> qualification) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getRoleMembersFromApplicationRole for " + namespaceCode + ", " + roleName);
        }
        
        return getRoleMembersFromDerivedRole(namespaceCode, roleName, qualification);
    }

    /**
     * This method retrieves the Contracts Grants Invoice document given the document number.
     *
     * @param String documentNumber
     * @return Document
     */
    protected Document getDocument(String documentNumber) {
        try {
            return getDocumentService().getByDocumentHeaderId(documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

    /**
     * Retrieves the document service.
     * @return documentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }
}
