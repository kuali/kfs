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
package org.kuali.module.financial.rules;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.SubFundGroup;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class ServiceBillingDocumentRule extends TransactionalDocumentRuleBase {
    protected static final Set _invalidCapitalCodeObjects = new TreeSet();
    protected static final Set _invalidObjectCodeSubTypes = new TreeSet();

    static {
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
        _invalidCapitalCodeObjects.add(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
    }
     
    static {
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.RESERVES);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.STATE_APP);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
        _invalidObjectCodeSubTypes.add(OBJECT_SUB_TYPE_CODE.INVEST);
    }
    
    public boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        
        if(isValid) {
            ErrorMap errorMap = GlobalVariables.getErrorMap();
            TransactionalDocument tranDoc = (TransactionalDocument) document;
            List sLines = tranDoc.getSourceAccountingLines();
            List tLines = tranDoc.getTargetAccountingLines();
    
            // First, validate the accounting lines
            // then. check for expense/revenue balance
            // finally, do object code restrictions checking
            KualiDecimal sExpense = KualiDecimal.ZERO;
            KualiDecimal sRevenue = KualiDecimal.ZERO;
            KualiDecimal tExpense = KualiDecimal.ZERO;
            KualiDecimal tRevenue = KualiDecimal.ZERO;
    
            for (Iterator i = sLines.iterator(); i.hasNext();) {
                SourceAccountingLine sal = (SourceAccountingLine) i.next();
                if (isExpenseOrAsset(sal)) {
                    sExpense = sExpense.add( sal.getAmount() );
                }
                else {
                    sRevenue = sRevenue.add( sal.getAmount() );
                }
                checkAccountingLine(sal);
            }
    
            for (Iterator i = tLines.iterator(); i.hasNext();) {
                TargetAccountingLine tal = (TargetAccountingLine) i.next();
                if (isExpenseOrAsset(tal)) {
                    tExpense = tExpense.add( tal.getAmount() );
                }
                else {
                    tRevenue = tRevenue.add( tal.getAmount() );
                }
                checkAccountingLine(tal);
            }
    
            KualiDecimal sDiff = sRevenue.subtract( sExpense );
            KualiDecimal tDiff = tRevenue.subtract( tExpense );
            if (!sDiff.equals( tDiff )) {
                errorMap.put(Constants.DOCUMENT_ERRORS,
                        "Document does not balance. From revenue minus expense should equal To revenue minus expense!", "Amount");
                return false;
            }
    
            isValid = errorMap.isEmpty();
        }
        return isValid;
    }

    public boolean processCustomAddAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomAddAccountingLineBusinessRules( document, accountingLine ) && checkAccountingLine(accountingLine);
    }
    public boolean processCustomReviewAccountingLineBusinessRules(TransactionalDocument document, AccountingLine accountingLine) {
        return super.processCustomReviewAccountingLineBusinessRules( document, accountingLine ) && checkAccountingLine(accountingLine);
    }

    /**
     * 
     */
    protected boolean isDebit(AccountingLine accountingLine) throws IllegalStateException {
        if (isSourceAccountingLine(accountingLine)) {
            return !super.isDebit(accountingLine);
        }
        else {
            return super.isDebit(accountingLine);
        }
    }

    /**
     * This performs the automatic checks for business rule violations on adding account lines
     * 
     * @param accountingLineCopy
     * @return
     */
    private boolean checkAccountingLine(AccountingLine accountingLine) {
        // get a new instance so we don't affect the original accounting line's values
        AccountingLine accountingLineCopy = (AccountingLine) ObjectUtils.deepCopy(accountingLine);

        // now make sure all the necessary business objects are fully populated
        SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(accountingLineCopy);

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        boolean result = true;

        String objectCode = accountingLineCopy.getObjectCode().getFinancialObjectCode();
        String objectSubType = accountingLineCopy.getObjectCode().getFinancialObjectSubType().getCode();
        String subFundGroup = accountingLineCopy.getAccount().getSubFundGroup().getSubFundGroupCode();
        // TODO - this next piece needs to be fixed when the object tree gets fixed
        BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
        Map subFundGroupId = new HashMap();
        subFundGroupId.put("code", subFundGroup);
        int pendPurchaseCount = 0;
        // TODO - This is an Indiana only requirement based on the specification doc

        if (OBJECT_SUB_TYPE_CODE.STUDENT_FEES.equals(objectSubType) && !SUB_FUND_GROUP_CODE.CONTINUE_EDUC.equals(subFundGroup)) {
            // objects
            String errorObjects[] = { objectCode, objectSubType };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_SUB_TYPE,
                    errorObjects);
            result = false;
        }

        // Check Capital object codes
        if (_invalidCapitalCodeObjects.contains(objectSubType)
                && !SUB_FUND_GROUP_CODE.CODE_EXTAGY.equals(subFundGroup) && (pendPurchaseCount <= 0.0)) {
            // objects
            String errorObjects[] = { objectSubType, subFundGroup };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                    errorObjects);
            result = false;
        }

        if (_invalidObjectCodeSubTypes.contains(objectSubType)) {
            // objects
            String errorObjects[] = { objectSubType };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_SUB_TYPE, errorObjects);
            result = false;
        }

        // object level check
        String objectLevel = accountingLineCopy.getObjectCode().getFinancialObjectLevel().getFinancialObjectLevelCode();
        if (OBJECT_LEVEL_CODE.CONTRACT_GRANTS.equals(objectLevel)) {
            // objects
            String errorObjects[] = { objectCode, objectLevel };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_LEVEL,
                    errorObjects);
            result = false;
        }

        // object type check
        String objectType = accountingLineCopy.getObjectCode().getFinancialObjectType().getCode();
        if (OBJECT_TYPE_CODE.INCOME_NOT_CASH.equals(objectType) || OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE.equals(objectType)) {
            // objects
            String errorObjects[] = { objectCode, objectType };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_CODE_WITH_OBJ_TYPE,
                    errorObjects);
            result = false;
        }

        // object sub-type check
        if (OBJECT_SUB_TYPE_CODE.ASSESSMENT.equals(objectSubType) || OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS.equals(objectSubType)) {
            // objects
            String errorObjects[] = { objectSubType };
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_INCORRECT_OBJ_SUB_TYPE, errorObjects);
            result = false;
        }

        // fund group check
        SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
        String fundGroup = sfg.getFundGroup().getCode();
        if (FUND_GROUP_CODE.LOAN_FUND.equals(fundGroup)) {
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, "Invalid Fund Group for this eDoc");
            result = false;
        }

        // sub-fund group
        if (SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT.equals(subFundGroup)) {
            errorMap.put(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_CUSTOM, "Invalid Sub Fund Group for this eDoc");
            result = false;
        }
        return result;
    }
}
