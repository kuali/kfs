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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.coa.service.ObjectLevelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.rules.PromptBeforeValidationBase;
import org.kuali.rice.krad.document.Document;

/**
 * PreRules checks for the {@link ObjectCode} that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class ObjectCodePreRules extends PromptBeforeValidationBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodePreRules.class);

    protected static ChartService chartService;
    protected static ObjectLevelService objectLevelService;
    protected Map reportsTo;

    /**
     * 
     * Constructs a ObjectCodePreRules
     * Pseudo-injects services and populates the reportsTo hierarchy
     */
    public ObjectCodePreRules() {
        if (objectLevelService == null) {
            objectLevelService = SpringContext.getBean(ObjectLevelService.class);
            chartService = SpringContext.getBean(ChartService.class);
        }

        reportsTo = chartService.getReportsToHierarchy();
    }

    /**
     * This method forces the reports to chart on the object code to be the correct one based on the 
     * reports to hierarchy
     * <p>
     * Additionally if the object level is null or inactive it confirms with the user that this
     * is actually the object level code they wish to use
     * @see org.kuali.rice.kns.rules.PromptBeforeValidationBase#doRules(org.kuali.rice.krad.document.Document)
     */
    @Override
    public boolean doPrompts(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        LOG.debug("doRules");

        if (LOG.isDebugEnabled()) {
            LOG.debug("new maintainable is: " + maintenanceDocument.getNewMaintainableObject().getClass());
        }
        ObjectCode newObjectCode = (ObjectCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        String chart = newObjectCode.getChartOfAccountsCode();
        String reportsToChart = (String) reportsTo.get(chart);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Chart: " + chart);
            LOG.debug("reportsTo: " + reportsToChart);
            LOG.debug("User supplied reportsToChart: " + newObjectCode.getReportsToChartOfAccountsCode());
        }

        // force reportsTo to the right value regardless of user input
        newObjectCode.setReportsToChartOfAccountsCode(reportsToChart);

        // If Object Level is inactive, ask user confirmation question
        ObjectLevel financialObjectLevel = objectLevelService.getByPrimaryId(chart, newObjectCode.getFinancialObjectLevelCode());
        if (!(financialObjectLevel == null)) {
            if (!financialObjectLevel.isActive()) {
                String objectLevelChartOfAccountCode = financialObjectLevel.getChartOfAccountsCode();
                String objectLevelFinancialObjectLevelCode = financialObjectLevel.getFinancialObjectLevelCode();
                String objectLevelFinancialObjectLevelName = financialObjectLevel.getFinancialObjectLevelName();
                String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSKeyConstants.ObjectCode.QUESTION_INACTIVE_OBJECT_LEVEL_CONFIRMATION);
                questionText = StringUtils.replace(questionText, "{0}", objectLevelChartOfAccountCode);
                questionText = StringUtils.replace(questionText, "{1}", objectLevelFinancialObjectLevelCode);
                questionText = StringUtils.replace(questionText, "{2}", objectLevelFinancialObjectLevelName);
                boolean useInactiveObjectLevel = super.askOrAnalyzeYesNoQuestion(KFSConstants.ObjectCodeConstants.INACTIVE_OBJECT_LEVEL_QUESTION_ID, questionText);
                if (!useInactiveObjectLevel) {
                    event.setActionForwardName(KFSConstants.MAPPING_BASIC);
                    return false;
                }
            }
        }

        return true;

    }
}
