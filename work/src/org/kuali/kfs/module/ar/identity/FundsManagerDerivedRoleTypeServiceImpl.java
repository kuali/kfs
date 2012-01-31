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

import static org.kuali.rice.kim.bo.impl.KimAttributes.DOCUMENT_NUMBER;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsFundManager;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiModuleService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Returns the fund managers of a given award using the proposal number. *
 */
public class FundsManagerDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(FundsManagerDerivedRoleTypeServiceImpl.class);
    private DocumentService documentService;

    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("getRoleMembersFromApplicationRole for " + namespaceCode + ", " + roleName);
        }
        List<RoleMembershipInfo> roleMembers = super.getRoleMembersFromApplicationRole(namespaceCode, roleName, qualification);

        if (ObjectUtils.isNotNull(qualification) && !qualification.isEmpty()) {
            String documentNumber = qualification.get(DOCUMENT_NUMBER);
            if (StringUtils.isNotBlank(documentNumber)) {
                ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = (ContractsGrantsInvoiceDocument) getDocument(documentNumber);
                ContractsAndGrantsCGBAward award = contractsGrantsInvoiceDocument.getAward();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("proposalNumber", award.getProposalNumber());
                List<ContractsAndGrantsFundManager> awardFundManagers = (List) SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsFundManager.class).getExternalizableBusinessObjectsList(ContractsAndGrantsFundManager.class, map);
                for (ContractsAndGrantsFundManager awardFundManager : awardFundManagers) {
                    roleMembers.add(new RoleMembershipInfo(null, null, awardFundManager.getPrincipalId(), Role.PRINCIPAL_MEMBER_TYPE, null));
                }
            }
        }
        return roleMembers;
    }

    /**
     * This method retrieves the CGIN document given the document number.
     * 
     * @param String documentNumber
     * @return Document
     */
    protected Document getDocument(String documentNumber) {
        try {
            return (Document) getDocumentService().getByDocumentHeaderId(documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Workflow problem while trying to get document using doc id '" + documentNumber + "'", e);
        }
    }

    /**
     * Retrieves the document service.
     * @return documentService
     */
    protected DocumentService getDocumentService() {
        if (ObjectUtils.isNull(documentService)) {
            documentService = SpringContext.getBean(DocumentService.class);
        }
        return documentService;
    }
}
