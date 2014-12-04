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

