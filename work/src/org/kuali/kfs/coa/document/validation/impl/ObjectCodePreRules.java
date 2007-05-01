/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ChartService;
import org.kuali.module.chart.service.ObjectLevelService;


public class ObjectCodePreRules extends PreRulesContinuationBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodePreRules.class);

    private ChartService chartService;
    private ObjectLevelService objectLevelService;
    private Map reportsTo;

    public ObjectCodePreRules() {
        this.setChartService(SpringServiceLocator.getChartService());
        reportsTo = chartService.getReportsToHierarchy();
        this.setObjectLevelService(SpringServiceLocator.getObjectLevelService());
    }


    public boolean doRules(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        LOG.debug("doRules");

        if (LOG.isDebugEnabled()) {
            LOG.debug("new maintainable is: " + maintenanceDocument.getNewMaintainableObject().getClass());
        }
        ObjectCode newObjectCode = (ObjectCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        String chart = newObjectCode.getChartOfAccountsCode();
        String reportsToChart = (String) reportsTo.get(chart);
        ObjLevel financialObjectLevel = objectLevelService.getByPrimaryId(chart, newObjectCode.getFinancialObjectLevelCode());
        if (LOG.isDebugEnabled()) {
            LOG.debug("Chart: " + chart);
            LOG.debug("reportsTo: " + reportsToChart);
            LOG.debug("User supplied reportsToChart: " + newObjectCode.getReportsToChartOfAccountsCode());
        }

        // force reportsTo to the right value regardless of user input
        newObjectCode.setReportsToChartOfAccountsCode(reportsToChart);

        // Default Mandatory Transfer Or Eliminations Code should be N when it left blank
        if (newObjectCode.getFinObjMandatoryTrnfrelimCd() == null) {
            newObjectCode.setFinObjMandatoryTrnfrelimCd("N");
        }
        
        // If Object Level is inactive, ask user confirmation question
         
        if (!(financialObjectLevel == null)) {
            if (!financialObjectLevel.isFinancialObjectLevelActiveIndicator()){
                String objectLevelChartOfAccountCode = financialObjectLevel.getChartOfAccountsCode();
                String objectLevelFinancialObjectLevelCode = financialObjectLevel.getFinancialObjectLevelCode();
                String objectLevelFinancialObjectLevelName = financialObjectLevel.getFinancialObjectLevelName();
                String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(
                        KFSKeyConstants.ObjectCode.QUESTION_INACTIVE_OBJECT_LEVEL_CONFIRMATION);
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

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }  
    public void setObjectLevelService(ObjectLevelService objectLevelService) {
         this.objectLevelService = objectLevelService;
    }
}
