/*
 * Copyright 2007-2008 The Kuali Foundation
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
