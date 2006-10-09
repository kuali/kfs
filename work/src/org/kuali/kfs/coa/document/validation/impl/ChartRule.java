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

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.service.ChartService;

public class ChartRule extends MaintenanceDocumentRuleBase {
   
    /**
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        
        boolean result=true;
        
        Chart chart=(Chart)document.getNewMaintainableObject().getBusinessObject();
        ChartService chartService = SpringServiceLocator.getChartService();
        
        String chartCode=chart.getChartOfAccountsCode();
        
        String reportsToChartCode=chart.getReportsToChartOfAccountsCode();

        if (chartCode!=null && !chartCode.equals(reportsToChartCode)) { // if not equal to this newly created chart, then must exist
            Chart reportsToChart=chartService.getByPrimaryId(reportsToChartCode);
            if (reportsToChart==null) {
                result = false;
                putFieldError("reportsToChartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_CHART_REPORTS_TO_CHART_MUST_EXIST);
            }
        }
        
        UniversalUser chartManager = null;
        try {
           chartManager=SpringServiceLocator.getKualiUserService().getUniversalUser(chart.getFinCoaManagerUniversalId());
         }
         catch (UserNotFoundException e) {
             result = false;
             putFieldError("finCoaManagerUniversal.personUserIdentifier",KeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_EXIST);
         }
        
        if (chartManager!=null && !chartManager.isActiveKualiUser()) {
            result=false;
            putFieldError("finCoaManagerUniversal.personUserIdentifier",KeyConstants.ERROR_DOCUMENT_CHART_MANAGER_MUST_BE_KUALI_USER);
        }
        
        
        return result;
        
    }

}
