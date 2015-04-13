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
