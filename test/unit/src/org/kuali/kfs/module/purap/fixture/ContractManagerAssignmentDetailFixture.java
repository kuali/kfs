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

import org.kuali.kfs.module.purap.businessobject.ContractManagerAssignmentDetail;
import org.kuali.kfs.module.purap.document.RequisitionDocument;

public enum ContractManagerAssignmentDetailFixture {

    ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS("", // documentNumber
            new Integer(12),   // contractManagerCode
            RequisitionDocumentFixture.REQ_NO_APO_VALID.createRequisitionDocument() // purchasingDocumentFixture
    ),
    ACM_DETAIL_REQ_ONLY_REQUIRED_FIELDS_2("", // documentNumber
            new Integer(12),   // contractManagerCode
            RequisitionDocumentFixture.REQ_NO_APO_VALID_2.createRequisitionDocument() // purchasingDocumentFixture
    ),
    ACM_DETAIL_PERFORMANCE("", // documentNumber
            new Integer(10),   // contractManagerCode
            RequisitionDocumentFixture.REQ_PERFORMANCE.createRequisitionDocument() // purchasingDocumentFixture
    ),
 ;

    private String documentNumber;
    private Integer requisitionIdentifier;
    private Integer contractManagerCode;

    private RequisitionDocument requisition;

    private ContractManagerAssignmentDetailFixture(String documentNumber, Integer contractManagerCode, RequisitionDocument requisition) {
        this.documentNumber = documentNumber;
        this.contractManagerCode = contractManagerCode;
        this.requisition = requisition;
        this.requisitionIdentifier = requisition.getPurapDocumentIdentifier();
    }

    public ContractManagerAssignmentDetail createContractManagerAssignmentDetail() {
        ContractManagerAssignmentDetail detail = new ContractManagerAssignmentDetail();
        detail.setDocumentNumber(documentNumber);
        detail.setRequisitionIdentifier(requisitionIdentifier);
        detail.setContractManagerCode(contractManagerCode);
        detail.refreshNonUpdateableReferences();
        detail.setRequisition(requisition);
        return detail;
    }

}
