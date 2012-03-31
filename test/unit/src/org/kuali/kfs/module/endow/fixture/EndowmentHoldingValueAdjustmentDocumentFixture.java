/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;

import org.kuali.kfs.module.endow.document.HoldingHistoryValueAdjustmentDocument;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

public enum EndowmentHoldingValueAdjustmentDocumentFixture {
    // Endowment Holding Value Adjustment Document FIXTURE
    EHVA_ONLY_REQUIRED_FIELDS("TESTSECID",  // securityId
            new KualiInteger(1),  // holdingMonthEndDate
            BigDecimal.ONE,  // securityUnitValue
            BigDecimal.ONE, // securityMarketValue
            false  // transactionPosted
    );

    public final String securityId;
    public final KualiInteger holdingMonthEndDate;
    public final BigDecimal securityUnitValue;
    public final BigDecimal securityMarketValue;
    public final boolean transactionPosted;

    private EndowmentHoldingValueAdjustmentDocumentFixture(String securityId, KualiInteger holdingMonthEndDate, BigDecimal securityUnitValue, BigDecimal securityMarketValue, boolean transactionPosted) {
        this.securityId = securityId;
        this.holdingMonthEndDate = holdingMonthEndDate;
        this.securityUnitValue = securityUnitValue;
        this.securityMarketValue = securityMarketValue;
        this.transactionPosted = transactionPosted;
    }

    /**
     * This method creates a Endowment Holding Value Adjustment document and puts it into the workflow
     * @param clazz
     * @return
     */
    private HoldingHistoryValueAdjustmentDocument createHoldingHistoryValueAdjustmentDocument(Class clazz) {
        try {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);

            HoldingHistoryValueAdjustmentDocument ehva = null;

            try {
                ehva = (HoldingHistoryValueAdjustmentDocument) DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), clazz);
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Document creation failed.");
            }

            ehva.getDocumentHeader().setDocumentDescription("Created for testing purpose");
            ehva.setSecurityId(this.securityId);
            ehva.setHoldingMonthEndDate(this.holdingMonthEndDate);
            ehva.setSecurityUnitValue(this.securityUnitValue);
            ehva.setSecurityMarketValue(this.securityMarketValue);
            ehva.setTransactionPosted(this.transactionPosted);
            ehva.setObjectId("1234567890");
            ehva.setVersionNumber(1L);
            ehva.refreshReferenceObject("security");

            documentService.routeDocument(ehva, "We want to see the document status as final", null);
            WorkflowTestUtils.waitForDocumentApproval(ehva.getDocumentNumber());

            return ehva;
        } catch (WorkflowException wfe) {
            return null;
        }
    }

    public HoldingHistoryValueAdjustmentDocument createHoldingHistoryValueAdjustmentDocument() {
        return createHoldingHistoryValueAdjustmentDocument(HoldingHistoryValueAdjustmentDocument.class);
    }
}

