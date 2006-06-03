/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl.orgreversion;

import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.gl.service.OrganizationReversionCategoryLogic;

public class GenericOrganizationReversionCategory implements OrganizationReversionCategoryLogic {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenericOrganizationReversionCategory.class);

    private KualiConfigurationService kualiConfigurationService;

    public boolean containsObjectCode(String categoryCode,ObjectCode oc) {
        LOG.debug("containsObjectCode() started");

        String cons = oc.getFinancialObjectLevel().getFinancialConsolidationObjectCode();
        String level = oc.getFinancialObjectLevelCode();
        String objTyp = oc.getFinancialObjectTypeCode();
        String objSubTyp = oc.getFinancialObjectSubType().getCode();

        KualiParameterRule consolidationRules = kualiConfigurationService.getApplicationParameterRule("OrgReversion", categoryCode + "_Consolidation");
        KualiParameterRule levelRules = kualiConfigurationService.getApplicationParameterRule("OrgReversion", categoryCode + "_Level");
        KualiParameterRule objectTypeRules = kualiConfigurationService.getApplicationParameterRule("OrgReversion", categoryCode + "_ObjectType");
        KualiParameterRule objectSubTypeRules = kualiConfigurationService.getApplicationParameterRule("OrgReversion", categoryCode + "_ObjectSubType");

        return consolidationRules.succeedsRule(cons) && levelRules.succeedsRule(level) && objectTypeRules.succeedsRule(objTyp) && objectSubTypeRules.succeedsRule(objSubTyp);
    }

    public String getName(String categoryCode) {
        return kualiConfigurationService.getApplicationParameterValue("OrgReversion", categoryCode + "_Name");
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}
