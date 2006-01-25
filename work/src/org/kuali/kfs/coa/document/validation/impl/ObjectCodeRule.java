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

import java.util.HashSet;
import java.util.Set;

import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectCons;
import org.kuali.module.chart.service.ObjectLevelService;

/**
 * 
 * This class implements the business rules from:
 *   http://fms.dfa.cornell.edu:8080/confluence/display/KULCOA/Object+Code
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class ObjectCodeRule extends MaintenanceDocumentRuleBase {

    DateTimeService dateTimeService;
    ObjectLevelService objectLevelService;

    

    
    final static String OBJECT_CODE_ILLEGAL_VALUES="ObjectCodeIlegalValues";
    final static String OBJECT_CODE_VALID_BUDGET_AGGREGATION_CODES="ObjectCodeValidBudgetAggregationCodes";
    final static String OBJECT_CODE_VALID_YEAR_CODE_EXCEPTIONS="ObjectCodeValidYearCodeExceptions";
    final static String OBJECT_CODE_VALID_MANDATORY_TRANSFER_ELIMINATION_CODES="ObjectCodeValidMandatoryTransferEliminationCodes";
    final static String OBJECT_CODE_VALID_FEDERAL_FUNDED_CODES="ObjectCodevalidFederalFundedCodes";
    
    
    private KualiConfigurationService configService;
    
    private static Set illegalValues;
    private static Set validYearCodeExceptions;
    private static Set validBudgetAggregationCodes;
    private static Set validMandatoryTransferEliminationCodes;
    private static Set validFederalFundedCodes;
    
    private final static int CHART_CODE=1;
    private final static int OBJECT_LEVEL=2;
    private final static int OBJECT_TYPE=3;
    private final static int SUB_TYPE=4;

    //TODO how to WARN? how to suggest Consolidation account?
    
    public ObjectCodeRule() {
        
        configService = SpringServiceLocator.getKualiConfigurationService();
   
        //TODO institutional values should really be injected or something
        illegalValues = retrieveParameterSet(OBJECT_CODE_ILLEGAL_VALUES);
        validYearCodeExceptions=retrieveParameterSet(OBJECT_CODE_VALID_YEAR_CODE_EXCEPTIONS);
        validBudgetAggregationCodes=retrieveParameterSet(OBJECT_CODE_VALID_BUDGET_AGGREGATION_CODES);
        validMandatoryTransferEliminationCodes=retrieveParameterSet(OBJECT_CODE_VALID_MANDATORY_TRANSFER_ELIMINATION_CODES);
        validFederalFundedCodes=retrieveParameterSet(OBJECT_CODE_VALID_FEDERAL_FUNDED_CODES);
        
        dateTimeService = SpringServiceLocator.getDateTimeService();
        objectLevelService = SpringServiceLocator.getObjectLevelService();
        //TODO may need Object Consolidation Service?
    }
    

    
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        //setupConvenienceObjects(document);
        //initializeRuleValues(document);
        
        //  default to success
        boolean success = true;
        success &= checkEmptyValues(document);
        
        Object maintainableObject=document.getNewMaintainableObject().getBusinessObject();
        
        success &= processObjectCodeRules((ObjectCode)maintainableObject);
        
        // Test the route rules, but do not "enforce" them during save (i.e. the result
        // from this method is ignored)
        processCustomRouteDocumentBusinessRules(document);
        
        return success;
        
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("processCustomRouteDocumentBusinessRules called");

        boolean success = true;
        
        Object maintainableObject=document.getNewMaintainableObject().getBusinessObject();
        success &= processObjectCodeRules((ObjectCode)maintainableObject);

        
        // setupConvenienceObjects(document);
        // initializeRuleValues(document);
        
        //  default to success
        
        success &= checkEmptyValues(document);
        
        return success;
    }

    private boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {
        boolean success = true;
        
        //success &= checkEmptyBOField("chartOfAccountsCode", newAccount.getChartOfAccountsCode(), "Chart of Accounts Code");
        //success &= checkEmptyBOField("accountNumber", newAccount.getAccountNumber(), "Account Number");
        
        return success;
    }

    private boolean processObjectCodeRules(ObjectCode objectCode) {
        
        boolean result=true;
        
        String objCode=objectCode.getFinancialObjectCode();
        
        if (!isLegalObjectCode(objCode)) {
            this.addIllegalValueError("objCode must not be "+illegalValues.toArray());
            return false;
        }

        Integer year=objectCode.getUniversityFiscalYear();
        String chartCode=objectCode.getChartOfAccountsCode();
        String reportsToChartCode=objectCode.getReportsToChartOfAccountsCode();
        
        if (!verifyReportsToChartCode(reportsToChartCode,year,chartCode)) {
            addIllegalValueError("reportsToChartCode have valid chart, year");
            return false;
        }

        String budgetAggregationCode=objectCode.getFinancialBudgetAggregationCd();
        
        if (!isLegalBudgetAggregationCode(budgetAggregationCode)) {
            addIllegalValueError("budgetAggregationCode must be one of "+validBudgetAggregationCodes);
            return false;
        }

        String mandatoryTransferEliminationCode=objectCode.getFinObjMandatoryTrnfrelimCd();
        
        if (!isLegalMandatoryTransferEliminationCode(mandatoryTransferEliminationCode)) {
            addIllegalValueError("mandatory transfer elim Code must be one of "+validMandatoryTransferEliminationCodes);
            return false;
        }

        //RULE: fiscal year must be valid
        if (!this.isValidYear(year)) {
            addIllegalValueError("fiscal year must be valid");
            return false;
        }

        objectCode.refresh();
        
        // Chart code (fin_coa_cd) must be valid
        if (!isValid(objectCode,CHART_CODE)) {
            addIllegalValueError("Chart must be valid");
            return false;
        }

        // Object level (obj_level_code) must be valid
        if (!isValid(objectCode,OBJECT_LEVEL)) {
            addIllegalValueError("object level must be valid");
            return false;
        }

        // Object Type must be valid
        if (!isValid(objectCode,OBJECT_TYPE)) {
            addIllegalValueError("object type must be valid");
            return false;
        }

        // Sub type must be valid
        if (!isValid(objectCode,SUB_TYPE)) {
            addIllegalValueError("object sub type must be valid");
            return false;
        }
        
        if (!this.consolidationTableDoesNotHave(chartCode,objCode)) {
            addIllegalValueError("given value already exists in consolidation table");
            return false;
        }

        if (!this.objectLevelTableDoesNotHave(chartCode,objCode)) {
            addIllegalValueError("given value already exists in consolidation table");
            return false;
        }
        
        /*
         * 
         * 
         
         The framework handles this:
         
         Pending object must not have duplicates waiting for approval
         Description (fdoc_desc) must be entered
         
         Verify the DD handles these:

         Fiscal year (univ_fisal_yr) must be entered
         Chart code (fin_coa_code) must be entered
         Object code (fin_object_code) must be entered
         (fin_obj_cd_nm) must be entered
         (fin_obj_cd_shrt_nm) must be entered
         Object level (obj_level_code) must be entered
         The Reports to Object (rpts_to_fin_obj_cd) must be entered

    It seems like this are Business Rules for other objects:
    
    An Object code must be active when it is used as valid value in the Labor Object Code table
    An Object code must be active when it is used as valid value in the LD Benefits Calculation table
    An Object code must be active when it is used as valid value in the ICR Automated Entry table
    An Object code must be active when it is used as valid value in the Chart table


These still need attention:

Warning if chart code is inactive
Warning if object level is inactive
If the Next Year Object has been entered, it must exist in the object code table alongside the fiscal year and chart code
(rpts_to_fin_coa_cd) is looked up based on chart code [fp_hcoat]


         
         */
        

        
        return result;
        
    }
    
    /**
     * 
     * This method checks whether newly added object code already exists in Object Level table
     * @param chartCode
     * @param objectCode
     * @return
     */
    public boolean objectLevelTableDoesNotHave(String chartCode,String objectCode) {
        try {
            ObjLevel objLevel=objectLevelService.getByPrimaryId(chartCode,objectCode);
            if (objLevel!=null) {
                objLevel.getFinancialObjectLevelCode(); //this might throw an Exception when proxying is in effect
                return false;
            }
        } catch (PersistenceBrokerException e) {
            //intentionally ignore the Exception 
        }
        
        return true;
    }
    
    /**
     * Check whether newly added object code already exists in Consolidation table
     */
    public boolean consolidationTableDoesNotHave(String chartCode,String objectCode) {
        try {
            ObjectCons objectCons=null; //TODO need ObjConsService?
        } catch (PersistenceBrokerException e) {
            //intentionally ignore the Exception 
        }
        return true;
    }
    

    /**
     * 
     * This is just a guess of how to do this.
     * 
     * If OJB gives us back a solid object, then it seems the mapping validates the current value, but
     * we must beware of proxy related exceptions that occur upon calling the nested object's methods.
     * 
     * @param objectCode
     * @param field
     * @return
     */
    // See Aaron's notes here: http://fms.dfa.cornell.edu:8080/confluence/display/KULEDOCS/Notes+on+Existence+Checking
    // and here: http://fms.dfa.cornell.edu:8080/confluence/display/KULEDOCS/Notes+on+Null+Checking+within+Business+Rule+Classes
    protected boolean isValid(ObjectCode objectCode, int field) {
        
        switch (field) {
            case CHART_CODE:
                try {
                    objectCode.getChartOfAccounts().getChartOfAccountsCode().equals(objectCode.getChartOfAccountsCode());
                } catch (PersistenceBrokerException e) {
                    return false;
                }
                break;
            case OBJECT_LEVEL:
                try {
                    objectCode.getFinancialObjectLevel().getFinancialObjectLevelCode().equals(objectCode.getFinancialObjectLevelCode());
                } catch (PersistenceBrokerException e) {
                    return false;
                }
                break;
            case OBJECT_TYPE:
                try {
                    objectCode.getFinancialObjectType().getCode().equals(objectCode.getFinancialObjectSubTypeCode());
                } catch (PersistenceBrokerException e) {
                    return false;
                } catch (NullPointerException e) {
                    return false;
                }
                     
                break;
            case SUB_TYPE:
                try {
                    objectCode.getFinancialObjectSubType().getCode().equals(objectCode.getFinancialObjectSubTypeCode());
                } catch (PersistenceBrokerException e) {
                    return false;
                } catch (NullPointerException e) {
                    return false;
                }
                break;
        }
        
        return true;
    }
    
    
    /**
     * 
     */
    public boolean isValidYear(Integer year) { //TODO this should come from DateTimeService?
        if (year==null) return false;
        return year.intValue()==2004 || year.intValue()==2005;
    }
    

    /**
     * 
     * This method is a null-safe wrapper around Set.contains().
     * @param set - methods returns false if the Set is null
     * @param value to seek
     * @return - true iff Set exists and contains given value
     */
    protected boolean permitted(Set set, Object value) {
        if (set!=null) {
            return set.contains(value);
        }
        return false;
    }

    protected boolean denied(Set set, Object value) {
        if (set!=null) {
            return !set.contains(value);
        }
        return true;
    }
    

    /**
     * Object code (fin_obj_code) must not have an institutionally specified illegal value
     * @return
     */
    protected boolean isLegalObjectCode(String objectCode) {
        boolean result=denied(illegalValues,objectCode);
        return result;
    }
    
    /**
     * 
     * Budget Aggregation Code (fobj_bdgt_aggr_cd) must have an institutionally specified value
     * @param budgetAggregationCode
     * @return
     */
    protected boolean isLegalBudgetAggregationCode(String budgetAggregationCode) {
        boolean result=permitted(validBudgetAggregationCodes,budgetAggregationCode);
        return result;
    }
    

    protected boolean isLegalFederalFundedCode(String federalFundedCode) {
        boolean result=permitted(validFederalFundedCodes,federalFundedCode);
        return result;
    }
    
    protected boolean isLegalMandatoryTransferEliminationCode (String mandatoryTransferEliminationCode) {
        boolean result=permitted(validMandatoryTransferEliminationCodes,mandatoryTransferEliminationCode);
        return result;
    }

    
    
    protected boolean verifyYearChart(Integer year, String chart) {
        return true; //TODO implement this
    }
    
    protected boolean verifyReportsToChartCode(String reportsToChartCode, Integer year, String chartCode) {
        // TODO: verify this ambiguously stated rule against the UNIFACE source
        // When the value of reportsToChartCode does not have an institutional exception, the Reports to Object 
        // (rpts_to_fin_obj_cd) fiscal year, and chart code must exist in the object code table
        if (!verifyYearChart(year,chartCode)) {
            // Year must be valid, unless the rpts_to_fin_coa_cd is listed as an exclusion 
            if (!permitted(validYearCodeExceptions,reportsToChartCode)) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean isDocumentValidForSave(Document document) {
        if (null == document) {
            return false;
        }

        boolean valid = true;
        //valid rules

        return valid;
    }
    
    private Set retrieveParameterSet(String parameterName) {
        
        String[] elements=configService.getApplicationParameterValues(CHART_MAINTENANCE_EDOC,parameterName);
        
        Set result=new HashSet();
        if (elements!=null) {
            for (int i=0; i<elements.length; i++) {
                result.add(elements[i]);
            }
        }
        return result;
    }

    private void addIllegalValueError(String errorMessage)  {
        //TODO COA: be sure to externalize all messages in KeyConstants, Constants, etc. 
        GlobalVariables.getErrorMap().put(
                Constants.DOCUMENT_ERRORS, 
                KeyConstants.ERROR_CUSTOM,
                new String[] {errorMessage});
    }
    
}
