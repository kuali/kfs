/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.fixtures;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.test.DocumentTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

public enum AssignContractManagerDocumentFixture {
    ACM_DOCUMENT_VALID (new AssignContractManagerDetailFixture[] {AssignContractManagerDetailFixture.ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS } )
 ;

    private AssignContractManagerDetailFixture[] acmDetailFixtures;
    private List <AssignContractManagerDetail> assignContractManagerDetails;
    private AssignContractManagerDocumentFixture(AssignContractManagerDetailFixture[] acmDetailFixtures) {
        this.acmDetailFixtures = acmDetailFixtures;
        assignContractManagerDetails = new ArrayList();
        for (AssignContractManagerDetailFixture detail : acmDetailFixtures) {
            assignContractManagerDetails.add(detail.createAssignContractManagerDetail());
        }
    }

    public AssignContractManagerDocument createAssignContractManagerDocument() {
        AssignContractManagerDocument doc = null;
        try {
            doc = (AssignContractManagerDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), AssignContractManagerDocument.class);
            doc.setAssignContractManagerDetails(assignContractManagerDetails);
        }
        catch (WorkflowException e) {
            throw new RuntimeException("Document creation failed.");
        }
        return doc;
    }

}
