/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.lookup.keyvalues;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.module.chart.rules.SubAccountRule;

public class SubAccountTypeValuesFinder extends KeyValuesBase {

    protected KualiConfigurationService configService;


    public SubAccountTypeValuesFinder() {
        super();

        this.setConfigService(SpringServiceLocator.getKualiConfigurationService());
    }


    public List getKeyValues() {

        // todo: use getApplicationParameterValues() instead?
        KualiParameterRule parmRule = configService.getApplicationParameterRule(Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, SubAccountRule.CG_ALLOWED_SUBACCOUNT_TYPE_CODES);
        // now we need to retrieve the parm values
        String[] parmValues = { "" };
        if (StringUtils.isNotBlank(parmRule.getParameterText())) {
            parmValues = parmRule.getParameterText().split(";");
        }

        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("", ""));
        for (int i = 0; i < parmValues.length; i++) {
            String parm = parmValues[i];
            activeLabels.add(new KeyLabelPair(parm, parm));
        }
        return activeLabels;
    }

    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }

}
