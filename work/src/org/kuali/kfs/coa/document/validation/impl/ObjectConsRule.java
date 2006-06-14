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

import java.util.HashMap;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCons;

public class ObjectConsRule extends MaintenanceDocumentRuleBase {
    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkObjLevelCode();
        return true;
    }

    /**
     * 
     * This method should be overridden to provide custom rules for processing document routing
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean success = true;
        success &= checkObjLevelCode();
        return success;
    }

    /**
     * 
     * This method checks to see if the Object Consolidation code matches a pre-existing Object Level code that is already entered.
     * If it does it returns false with an error
     * 
     * @param document
     * @return false if Object Level Code already exists
     */
    private boolean checkObjLevelCode() {
        boolean success = true;
        ObjectCons objConsolidation = (ObjectCons) super.getNewBo();
        String chartOfAccountsCode = objConsolidation.getChartOfAccountsCode();
        String financialObjectLevelCode = objConsolidation.getFinConsolidationObjectCode();
        Map primaryKeys = new HashMap();
        primaryKeys.put("chartOfAccountsCode", chartOfAccountsCode);
        primaryKeys.put("financialObjectLevelCode", financialObjectLevelCode);
        ObjLevel objLevel = (ObjLevel) getBoService().findByPrimaryKey(ObjLevel.class, primaryKeys);
        if (objLevel != null) {
            success = false;
            putFieldError("finConsolidationObjectCode", KeyConstants.ERROR_DOCUMENT_OBJCONSMAINT_ALREADY_EXISTS_AS_OBJLEVEL);
        }
        return success;
    }
}
