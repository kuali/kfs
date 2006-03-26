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
package org.kuali.module.chart.rules;

import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ChartService;


public class ObjectCodePreRules extends PreRulesContinuationBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ObjectCodePreRules.class);
    
    private ChartService chartService;
    private Map reportsTo;
   
    public ObjectCodePreRules() {
        this.setChartService(SpringServiceLocator.getChartService());
        reportsTo=chartService.getReportsToHierarchy();
    }
    
    
    public boolean doRules(Document document) {
        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;
        
        LOG.debug("doRules");
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("new maintainable is: "+maintenanceDocument.getNewMaintainableObject().getClass());
        }
        ObjectCode newObjectCode = (ObjectCode) maintenanceDocument.getNewMaintainableObject().getBusinessObject();

        String chart=newObjectCode.getChartOfAccountsCode();
        String reportsToChart=(String) reportsTo.get(chart);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Chart: "+chart);
            LOG.debug("reportsTo: "+reportsToChart);
            LOG.debug("User supplied reportsToChart: "+newObjectCode.getReportsToChartOfAccountsCode());
        }
        
        // force reportsTo to the right value regardless of user input
        newObjectCode.setReportsToChartOfAccountsCode(reportsToChart);
        
        return true;
        
    }

    public void setChartService(ChartService chartService) {
        this.chartService = chartService;
    }
}
