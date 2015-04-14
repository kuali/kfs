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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kew.api.document.DocumentStatus;

/**
 * Abstract base class of the suspended invoice reports
 */
public abstract class ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImplBase extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected FinancialSystemDocumentService financialSystemDocumentService;

    protected String buildProcessingDocumentStatusesForLookup() {
        List<String> processingDocumentStatuses = new ArrayList<String>();
        processingDocumentStatuses.addAll(getFinancialSystemDocumentService().getPendingDocumentStatuses());
        processingDocumentStatuses.add(DocumentStatus.PROCESSED.getCode());
        final String processingDocumentStatusesForLookup = StringUtils.join(processingDocumentStatuses, SearchOperator.OR.op());
        return processingDocumentStatusesForLookup;
    }

    public FinancialSystemDocumentService getFinancialSystemDocumentService() {
        return financialSystemDocumentService;
    }

    public void setFinancialSystemDocumentService(FinancialSystemDocumentService financialSystemDocumentService) {
        this.financialSystemDocumentService = financialSystemDocumentService;
    }
}
