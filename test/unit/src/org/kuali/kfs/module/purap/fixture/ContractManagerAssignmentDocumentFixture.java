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
package org.kuali.kfs.module.purap.fixture;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.ContractManagerAssignmentDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum ContractManagerAssignmentDocumentFixture {
    ACM_DOCUMENT_VALID (new ContractManagerAssignmentDetailFixture[] {ContractManagerAssignmentDetailFixture.ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS, ContractManagerAssignmentDetailFixture.ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS_2 } ),
    ACM_DOCUMENT_VALID_2 (new ContractManagerAssignmentDetailFixture[] {ContractManagerAssignmentDetailFixture.ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS_2 } ),
    ACM_DOCUMENT_PERFORMANCE(new ContractManagerAssignmentDetailFixture[] {ContractManagerAssignmentDetailFixture.ACM_DETAIL_PERFORMANCE})
 ;

    private ContractManagerAssignmentDetailFixture[] acmDetailFixtures;
    private List <ContractManagerAssignmentDetail> contractManagerAssignmentDetails;
    private ContractManagerAssignmentDocumentFixture(ContractManagerAssignmentDetailFixture[] acmDetailFixtures) {
        this.acmDetailFixtures = acmDetailFixtures;
        contractManagerAssignmentDetails = new ArrayList();
        for (ContractManagerAssignmentDetailFixture detail : acmDetailFixtures) {
            contractManagerAssignmentDetails.add(detail.createContractManagerAssignmentDetail());
        }
    }

    public List<ContractManagerAssignmentDetail> getContractManagerAssignmentDetails() {
        return contractManagerAssignmentDetails;
    }
    
    public ContractManagerAssignmentDocument createContractManagerAssignmentDocument() {
        ContractManagerAssignmentDocument doc = null;
        try {
            doc = (ContractManagerAssignmentDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), ContractManagerAssignmentDocument.class);
            doc.setContractManagerAssignmentDetailss(contractManagerAssignmentDetails);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        return doc;
    }

}
