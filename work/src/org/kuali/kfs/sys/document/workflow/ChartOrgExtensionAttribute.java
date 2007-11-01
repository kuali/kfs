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
package org.kuali.workflow.attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.workflow.KualiWorkflowUtils;

import edu.iu.uis.eden.lookupable.Row;
import edu.iu.uis.eden.validation.ValidationContext;
import edu.iu.uis.eden.validation.ValidationResults;

/**
 * An attribute which provides Chart & Org extensions to an entity. In this case, it is used for the Chart Org Workgroup type.
 * 
 * @author ewestfal
 */
public class ChartOrgExtensionAttribute implements ExtensionAttribute {

    private static final String CHART = KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME;
    private static final String ORG = KFSConstants.ORGANIZATION_CODE_PROPERTY_NAME;

    private static final String EXTENSIONS_PARAM = "extensions";
    private static final String OPERATION_PARAM = "operation";
    private static final String SEARCH_OP = "search";
    private static final String ENTRY_OP = "entry";

    private List<Row> rows;

    public ChartOrgExtensionAttribute() {
        rows = new ArrayList<Row>();
        rows.add(KualiWorkflowUtils.buildTextRowWithLookup(Chart.class, CHART, CHART));
        Map fieldConversionMap = new HashMap();
        fieldConversionMap.put(CHART, CHART);
        rows.add(KualiWorkflowUtils.buildTextRowWithLookup(Org.class, ORG, ORG, fieldConversionMap));
    }

    public List<Row> getRows() {
        return rows;
    }

    /**
     * Validate the chart code and org code that are specified.
     * 
     * @see org.kuali.workflow.attribute.ExtensionAttribute#validate(edu.iu.uis.eden.validation.ValidationContext)
     */
    public ValidationResults validate(ValidationContext validationContext) {
        Map<String, String> extensions = (Map<String, String>) validationContext.getParameters().get(EXTENSIONS_PARAM);
        String operation = (String) validationContext.getParameters().get(OPERATION_PARAM);
        if (extensions == null) {
            // not sure if this is the correct exception or not but it feels better than throwing a runtime
            throw new ValidationException("Could not locate workgroup extension data in order to perform validation.");
        }
        List errors = new ArrayList();
        String chart = LookupUtils.forceUppercase(Org.class, CHART, extensions.get(CHART));
        String org = LookupUtils.forceUppercase(Org.class, ORG, extensions.get(ORG));

        ValidationResults results = new ValidationResults();
        if (StringUtils.isBlank(chart) || StringUtils.isBlank(org)) {
            if (ENTRY_OP.equals(operation)) {
                results.addValidationResult("Chart/org is required.");
            }
        }
        else {
            Org organization = SpringContext.getBean(OrganizationService.class).getByPrimaryIdWithCaching(chart, org);
            if (organization == null) {
                results.addValidationResult("Chart/org is invalid.");
            }
        }
        return results;
    }

}
