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
package org.kuali.workflow.module.purap.docsearch;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Org;
import org.kuali.workflow.module.purap.docsearch.attribute.KualiPurchaseOrderIncompleteStatusesAttribute;

import edu.iu.uis.eden.docsearch.QueryComponent;
import edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent;

/**
 * This class...
 */
public class PurchaseOrderDocSearchGenerator extends PurApDocumentSearchGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderDocSearchGenerator.class);

    private boolean usingIncompleteStatusCriteria = false;

    /**
     * @see org.kuali.workflow.module.purap.docsearch.KualiPurApDocumentSearchGenerator#getErrorMessageForNonSpecialUserInvalidCriteria()
     */
    @Override
    public String getErrorMessageForNonSpecialUserInvalidCriteria() {
        String chartCodeLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Org.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String orgCodeLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(Org.class, KFSPropertyConstants.ORGANIZATION_CODE);
        return "User must enter a valid " + chartCodeLabel + " and a valid " + orgCodeLabel;
    }

    /**
     * @see org.kuali.workflow.module.purap.docsearch.KualiPurApDocumentSearchGenerator#getGeneralSearchUserRequiredFormFieldNames()
     */
    @Override
    public List<String> getGeneralSearchUserRequiredFormFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        return fieldNames;
    }

    /**
     * @see org.kuali.workflow.module.purap.docsearch.KualiPurApDocumentSearchGenerator#getSearchAttributeFormFieldNamesToIgnore()
     */
    @Override
    public List<String> getSearchAttributeFormFieldNamesToIgnore() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("displayType");
        return fieldNames;
    }

    /**
     * @see org.kuali.workflow.module.purap.docsearch.KualiPurApDocumentSearchGenerator#getSpecificSearchCriteriaFormFieldNames()
     */
    @Override
    public List<String> getSpecificSearchCriteriaFormFieldNames() {
        List<String> fieldNames = new ArrayList<String>();
        fieldNames.add("purchaseOrderDocumentPurchaseOrderId");
        // doc id should be in list but is standard workflow field
        fieldNames.add("purchaseOrderDocumentRequestorName");
        fieldNames.add("documentHeaderDescription");
        fieldNames.add("purapDocumentChartOfAccountsCode");
        fieldNames.add("purapDocumentOrganizationCode");
        fieldNames.add("purchaseOrderRequisitionId");
        return fieldNames;
    }

    /**
     * @see edu.iu.uis.eden.docsearch.StandardDocumentSearchGenerator#generateSearchableAttributeSql(edu.iu.uis.eden.docsearch.SearchAttributeCriteriaComponent,
     *      java.lang.String, int)
     */
    @Override
    protected QueryComponent generateSearchableAttributeSql(SearchAttributeCriteriaComponent criteriaComponent, String whereSqlStarter, int tableIndex) {
        if (KualiPurchaseOrderIncompleteStatusesAttribute.FIELD_DEF_NAME.equals(criteriaComponent.getFormKey())) {
            if (isIncompleteStatusBeingUsed(criteriaComponent)) {
                usingIncompleteStatusCriteria = true;
            }
        }
        else if ("purchaseOrderDocumentStatusCode".equals(criteriaComponent.getFormKey())) {
            // if we've previous found the criteria object for incomplete or if we find it now... exclude the statuses in criteria
            if (usingIncompleteStatusCriteria || isIncompleteStatusBeingUsed(getSearchableAttributeByFieldName(KualiPurchaseOrderIncompleteStatusesAttribute.FIELD_DEF_NAME))) {
                // found the incomplete status checkbox value so ignore the status code search attribute entry
                return new QueryComponent();
            }
        }
        return super.generateSearchableAttributeSql(criteriaComponent, whereSqlStarter, tableIndex);
    }

    private boolean isIncompleteStatusBeingUsed(SearchAttributeCriteriaComponent incompleteStatusComponent) {
        return ((ObjectUtils.isNotNull(incompleteStatusComponent)) && (KualiPurchaseOrderIncompleteStatusesAttribute.VALUE_FOR_YES.equals(incompleteStatusComponent)));
    }

}
