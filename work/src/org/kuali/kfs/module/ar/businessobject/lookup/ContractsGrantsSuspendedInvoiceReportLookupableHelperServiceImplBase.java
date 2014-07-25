/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.SuspensionCategory;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kew.api.document.DocumentStatus;

/**
 * Abstract base class of the suspended invoice reports
 */
public abstract class ContractsGrantsSuspendedInvoiceReportLookupableHelperServiceImplBase extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected FinancialSystemDocumentService financialSystemDocumentService;

    /**
     * The suspension category code input on the form may use wild cards, so we'll do a lookup on matching suspension categories
     * @param suspensionCategoryCode the code String to lookup - it may control wildcards
     * @return a Set of the matching suspension category code Strings
     */
    protected Set<String> retrieveMatchingSuspensionCategories(String suspensionCategoryCode) {
        Set<String> matchingSuspensionCodes = new HashSet<String>();

        if (!StringUtils.isBlank(suspensionCategoryCode)) {
            Map<String, String> fields = new HashMap<String, String>();
            fields.put(ArPropertyConstants.SuspensionCategory.SUSPENSION_CATEGORY_CODE, suspensionCategoryCode);
            final Collection<SuspensionCategory> suspensionCategories = getLookupService().findCollectionBySearchHelper(SuspensionCategory.class, fields, true);
            for (SuspensionCategory suspensionCategory : suspensionCategories) {
                matchingSuspensionCodes.add(suspensionCategory.getSuspensionCategoryCode());
            }
        }
        return matchingSuspensionCodes;
    }

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
