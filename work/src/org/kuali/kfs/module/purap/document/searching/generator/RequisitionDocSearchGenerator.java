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
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Org;

/**
 * This class...
 */
public class RequisitionDocSearchGenerator extends PurApDocumentSearchGenerator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(RequisitionDocSearchGenerator.class);

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
        fieldNames.add("requisitionDocumentRequisitionId");
        // doc id should be in list but is standard workflow field
        fieldNames.add("requisitionDocumentRequestorName");
        fieldNames.add("requisitionDocumentDeliverToName");
        fieldNames.add("purapDocumentChartOfAccountsCode");
        fieldNames.add("purapDocumentOrganizationCode");
        fieldNames.add("requisitionDocumentOrganizationReference1Text");
        fieldNames.add("requisitionDocumentOrganizationReference2Text");
        fieldNames.add("requisitionDocumentOrganizationReference3Text");
        return fieldNames;
    }

}
