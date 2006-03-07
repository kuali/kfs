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
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.financial.rules.KualiParameterRule;

public class OffsetDefinitionRule extends MaintenanceDocumentRuleBase {
    private OffsetDefinition oldDefinition;
    private OffsetDefinition newDefinition;
    
    public static final String DOCTYPE_AND_OBJ_CODE_VAL = "OffsetDefinition.ARDocTypes";
    public static final String DOCTYPE_AND_OBJ_CODE_ACTIVE = "OffsetDefinition.DocTypeActiveObjectCode";
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    public void setupConvenienceObjects() {
        
        //  setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldDefinition = (OffsetDefinition) super.oldBo;

        //  setup newAccount convenience objects, make sure all possible sub-objects are populated
        newDefinition = (OffsetDefinition) super.newBo;
    }
    
    /**
     * This method should be overridden to provide custom rules for processing document saving
     * 
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        checkDocTypeAndFinancialObjCode(document);
        checkDocTypeActiveFinancialObjCode(document);
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
        success &= checkDocTypeAndFinancialObjCode(document);
        success &= checkDocTypeActiveFinancialObjCode(document);
        return true;
    }
    
    private boolean checkDocTypeAndFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        KualiParameterRule parmRule = configService.getApplicationParameterRule(
                Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, 
                DOCTYPE_AND_OBJ_CODE_VAL);
        // we need to check to see if the values are in the right range and then
        // see if the ObjectCode is the right value
        if (parmRule.succeedsRule(newDefinition.getFinancialDocumentTypeCode())) {
            if((ObjectUtils.isNotNull(newDefinition.getFinancialObject())
                && StringUtils.isNotEmpty(newDefinition.getFinancialObject().getFinancialObjectSubTypeCode())
                && !newDefinition.getFinancialObject().getFinancialObjectSubTypeCode().equalsIgnoreCase("AR"))
                || StringUtils.isEmpty(newDefinition.getFinancialObjectCode())) {
                
                putFieldError("financialObjectCode", 
                        KeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INVALID_OBJ_CODE_FOR_DOCTYPE, 
                        new String[] {
                            newDefinition.getFinancialObjectCode(),
                            parmRule.getParameterText()});
                
            }
            
            success &= false;
        }
        
        return success;
    }
    
    private boolean checkDocTypeActiveFinancialObjCode(MaintenanceDocument document) {
        boolean success = true;
        KualiParameterRule parmRule = configService.getApplicationParameterRule(
                Constants.ChartApcParms.GROUP_CHART_MAINT_EDOCS, 
                DOCTYPE_AND_OBJ_CODE_ACTIVE);
        if(parmRule.succeedsRule(newDefinition.getFinancialDocumentTypeCode())) {
            if((ObjectUtils.isNotNull(newDefinition.getFinancialObject())
                && !newDefinition.getFinancialObject().isFinancialObjectActiveCode())
                || ObjectUtils.isNull(newDefinition.getFinancialObject())) {
                
                putFieldError("financialObjectCode", 
                                KeyConstants.ERROR_DOCUMENT_OFFSETDEFMAINT_INACTIVE_OBJ_CODE_FOR_DOCTYPE, 
                                new String[] {
                                    newDefinition.getFinancialObjectCode(),
                                    parmRule.getParameterText()});
                success &= false;
            }
            
        }
        return success;
    }
}
