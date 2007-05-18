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
package org.kuali.module.labor.web.struts.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import static org.apache.commons.beanutils.PropertyUtils.getProperty;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rules.event.EmployeeIdChangedEvent;
import org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;
import org.kuali.rice.KNSServiceLocator;

/**
 * This class extends the parent KualiTransactionalDocumentActionBase class, which contains all common action methods. Since the SEP
 * follows the basic transactional document pattern, there are no specific actions that it has to implement; however, this empty
 * class is necessary for integrating into the framework.
 */
public class SalaryExpenseTransferAction extends LaborDocumentActionBase {

    private static final Map<String, String> periodCodeMapping = new HashMap<String,String>();
    static {
        periodCodeMapping.put("01","month1AccountLineAmount");
        periodCodeMapping.put("02","month2AccountLineAmount");
        periodCodeMapping.put("03","month3AccountLineAmount");
        periodCodeMapping.put("04","month4AccountLineAmount");
        periodCodeMapping.put("05","month5AccountLineAmount");
        periodCodeMapping.put("06","month6AccountLineAmount");
        periodCodeMapping.put("07","month7AccountLineAmount");
        periodCodeMapping.put("08","month8AccountLineAmount");
        periodCodeMapping.put("09","month9AccountLineAmount");
        periodCodeMapping.put("10","month10AccountLineAmount");
        periodCodeMapping.put("11","month11AccountLineAmount");
        periodCodeMapping.put("12","month12AccountLineAmount");
        periodCodeMapping.put("13","month13AccountLineAmount");
    }
        
    public SalaryExpenseTransferAction() {
    }
    
    /**
     * Takes care of storing the action form in the user session and forwarding to the balance inquiry lookup action.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward performBalanceInquiryLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        TransactionalDocument document = financialDocumentForm.getTransactionalDocument();

        // save in workflow
        KNSServiceLocator.getDocumentService().saveDocument(document);

        String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();

        // parse out the important strings from our methodToCall parameter
        String fullParameter = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);

        // parse out business object class name for lookup
        String boClassName = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_BOPARM_LEFT_DEL, KFSConstants.METHOD_TO_CALL_BOPARM_RIGHT_DEL);
        if (StringUtils.isBlank(boClassName)) {
            throw new RuntimeException("Illegal call to perform lookup, no business object class name specified.");
        }

        // build the parameters for the lookup url
        Properties parameters = new Properties();
        String conversionFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM1_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM1_RIGHT_DEL);
        if (StringUtils.isNotBlank(conversionFields)) {
            parameters.put(KFSConstants.CONVERSION_FIELDS_PARAMETER, conversionFields);
        }

        // pass values from form that should be pre-populated on lookup search
        String parameterFields = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM2_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM2_RIGHT_DEL);
        if (StringUtils.isNotBlank(parameterFields)) {
            String[] lookupParams = parameterFields.split(KFSConstants.FIELD_CONVERSIONS_SEPERATOR);

            for (int i = 0; i < lookupParams.length; i++) {
                String[] keyValue = lookupParams[i].split(KFSConstants.FIELD_CONVERSION_PAIR_SEPERATOR);

                // hard-coded passed value
                if (StringUtils.contains(keyValue[0], "'")) {
                    parameters.put(keyValue[1], StringUtils.replace(keyValue[0], "'", ""));
                }
                // passed value should come from property
                else if (StringUtils.isNotBlank(request.getParameter(keyValue[0]))) {
                    parameters.put(keyValue[1], request.getParameter(keyValue[0]));
                }
            }
        }

        // grab whether or not the "return value" link should be hidden or not
        String hideReturnLink = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM3_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM3_RIGHT_DEL);
        if (StringUtils.isNotBlank(hideReturnLink)) {
            parameters.put(KFSConstants.HIDE_LOOKUP_RETURN_LINK, hideReturnLink);
        }

        System.out.println("Setting the anchor to " + ((KualiAccountingDocumentFormBase) form).getAnchor());
        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(((KualiForm) form).getAnchor())) {
            parameters.put(Constants.LOOKUP_ANCHOR, ((KualiForm) form).getAnchor());
        }

        // determine what the action path is
        String actionPath = StringUtils.substringBetween(fullParameter, KFSConstants.METHOD_TO_CALL_PARM4_LEFT_DEL, KFSConstants.METHOD_TO_CALL_PARM4_RIGHT_DEL);
        if (StringUtils.isBlank(actionPath)) {
            throw new IllegalStateException("The \"actionPath\" attribute is an expected parameter for the <kul:balanceInquiryLookup> tag - it " + "should never be blank.");
        }

        // now add required parameters
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, "search");
        parameters.put(KFSConstants.DOC_FORM_KEY, GlobalVariables.getUserSession().addObject(form));
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, boClassName);
        parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, basePath + mapping.getPath() + ".do");

        String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + actionPath, parameters);

        return new ActionForward(lookupUrl, true);
    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiBalanceInquiryReportMenuAction#performBalanceInquiryLookup(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
//     public ActionForward performBalanceInquiryLookup(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//         KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
//         TransactionalDocument document = financialDocumentForm.getTransactionalDocument();

//         // save in workflow
//         KNSServiceLocator.getDocumentService().saveDocument(document);

//         // save(mapping, form, request, response);
//         return balanceInquiryAction.performBalanceInquiryLookup(mapping, form, request, response);
//     }


    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalaryExpenseTransferForm salaryExpenseTransferForm = (SalaryExpenseTransferForm) form;

        // Needed to be executed for each accounting line that may have been added.
        boolean rulePassed = runRule(new EmployeeIdChangedEvent(salaryExpenseTransferForm.getDocument()));
        Map<String, String> requestParams = (Map<String, String>) request.getParameterMap();
        
        Collection<PersistableBusinessObject> rawValues = null;
        if (StringUtils.equals(Constants.MULTIPLE_VALUE, salaryExpenseTransferForm.getRefreshCaller())) {
            String lookupResultsSequenceNumber = salaryExpenseTransferForm.getLookupResultsSequenceNumber();
            if (StringUtils.isNotBlank(lookupResultsSequenceNumber)) {
                // actually returning from a multiple value lookup
                String lookupResultsBOClassName = salaryExpenseTransferForm.getLookupResultsBOClassName();
                Class lookupResultsBOClass = Class.forName(lookupResultsBOClassName);
                
                rawValues = KNSServiceLocator.getLookupResultsService().retrieveSelectedResultBOs(lookupResultsSequenceNumber, lookupResultsBOClass, GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
            }
        }
        
        if (rawValues != null) {
            for (PersistableBusinessObject bo : rawValues) {
                salaryExpenseTransferForm.setUniversityFiscalYear(((LedgerBalance) bo).getUniversityFiscalYear());
                
                for (String periodCode : periodCodeMapping.keySet()) {
                    ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) salaryExpenseTransferForm.getFinancialDocument().getSourceAccountingLineClass().newInstance();
                    try {
                        buildAccountingLineFromLedgerBalance((LedgerBalance) bo, line, 
                                                             (KualiDecimal) getProperty(bo, periodCodeMapping.get(periodCode)), periodCode);
                    } 
                    catch (Exception e) {
                        // IllegalAccessException thrown by getProperty() call
                        // No way to recover gracefully, so throw it back as a RuntimeException
                        throw new RuntimeException(e);
                    }
                    SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
                    insertAccountingLine(true, salaryExpenseTransferForm, line);
                }
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward copyAllAccountingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;
        for (Object line : financialDocumentForm.getFinancialDocument().getSourceAccountingLines()) {
            ExpenseTransferAccountingLine to = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
            copyAccountingLine((ExpenseTransferAccountingLine) line, to);

            boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), to));
            
            // if the rule evaluation passed, let's add it
            if (rulePassed) {
                // add accountingLine
                SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
                insertAccountingLine(false, financialDocumentForm, to);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)
     */
    public ActionForward copyAccountingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiAccountingDocumentFormBase financialDocumentForm = (KualiAccountingDocumentFormBase) form;

        AccountingDocument financialDocument = (AccountingDocument) financialDocumentForm.getDocument();

        int index = getSelectedLine(request);

        ExpenseTransferAccountingLine line = (ExpenseTransferAccountingLine) financialDocumentForm.getFinancialDocument().getTargetAccountingLineClass().newInstance();
        copyAccountingLine((ExpenseTransferAccountingLine) financialDocument.getSourceAccountingLine(index), line);

        boolean rulePassed = runRule(new AddAccountingLineEvent(KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME, financialDocumentForm.getDocument(), line));
        
        // if the rule evaluation passed, let's add it
        if (rulePassed) {
            // add accountingLine
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(line);
            insertAccountingLine(false, financialDocumentForm, line);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Copies content from one accounting line to the other. Ignores Source or Target information.
     *
     * @param source line to copy from
     * @param target new line to copy data to
     */
    private void copyAccountingLine(ExpenseTransferAccountingLine source, ExpenseTransferAccountingLine target) {
        target.setChartOfAccountsCode(source.getChartOfAccountsCode());
        target.setAccountNumber(source.getAccountNumber());
        target.setSubAccountNumber(source.getSubAccountNumber());
        target.setPostingYear(source.getPostingYear());
        target.setPayrollEndDateFiscalYear(source.getPayrollEndDateFiscalYear());
        target.setFinancialObjectCode(source.getFinancialObjectCode());
        target.setFinancialSubObjectCode(source.getFinancialSubObjectCode());
        target.setBalanceTypeCode(source.getBalanceTypeCode());
        target.setPositionNumber(source.getPositionNumber());
        target.setAmount(source.getAmount());
        target.setEmplid(source.getEmplid());
        target.setPayrollEndDateFiscalPeriodCode(source.getPayrollEndDateFiscalPeriodCode());
    }
    
    /**
     * Translates <code>{@link LedgerBalance}</code> data into an <code>{@link ExpenseTransferAccountingLine}</code>
     *
     * @param bo <code>{@link LedgerBalance}</code> instance
     * @param line <code>{@link ExpenseTransferAccountingLine}</code> to copy data to
     */
    private void buildAccountingLineFromLedgerBalance(LedgerBalance bo, ExpenseTransferAccountingLine line, KualiDecimal amount, String periodCode) {
        line.setChartOfAccountsCode(bo.getChartOfAccountsCode());
        line.setAccountNumber(bo.getAccountNumber());
        if (!bo.getSubAccountNumber().startsWith("-")) {
            line.setSubAccountNumber(bo.getSubAccountNumber());
        }
        line.setPostingYear(bo.getUniversityFiscalYear());
        line.setPayrollEndDateFiscalYear(bo.getUniversityFiscalYear());
        line.setFinancialObjectCode(bo.getFinancialObjectCode());
        if (!bo.getFinancialSubObjectCode().startsWith("-")) {
            line.setFinancialSubObjectCode(bo.getFinancialSubObjectCode());
        }
        line.setBalanceTypeCode(bo.getFinancialBalanceTypeCode());
        line.setPositionNumber(bo.getPositionNumber());
        line.setAmount(amount);
        line.setEmplid(bo.getEmplid());
        line.setPayrollEndDateFiscalPeriodCode(periodCode);
    }
        
    /**
     * Executes for the given event. This is more of a convenience method.
     *
     * @param event to run the rules for
     */
    private boolean runRule(KualiDocumentEventBase event) {
        // check any business rules

        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(event);
        return rulePassed;
    }
}
