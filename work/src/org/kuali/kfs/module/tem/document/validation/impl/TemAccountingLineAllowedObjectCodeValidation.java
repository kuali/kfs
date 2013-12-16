/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TemExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class TemAccountingLineAllowedObjectCodeValidation extends GenericValidation {

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();
        TemSourceAccountingLine line  = null;
        if (event instanceof UpdateAccountingLineEvent){
            line = (TemSourceAccountingLine) ((UpdateAccountingLineEvent)event).getUpdatedAccountingLine();
        }
        else{
            line = (TemSourceAccountingLine) ((AccountingLineEvent)event).getAccountingLine();
        }
        List<String> holdErrors = new ArrayList<String>();
        holdErrors.addAll(GlobalVariables.getMessageMap().getErrorPath());
        GlobalVariables.getMessageMap().clearErrorPath();
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        final boolean canUpdate = isAtTravelNode(event.getDocument().getDocumentHeader().getWorkflowDocument()) || isAdvancePaymentMethodException(event.getDocument(), line);  // Are we at the travel node?  If so, there's a chance that accounting lines changed; if they did, that
                            // was a permission granted to the travel manager so we should allow it.  Also, if we're at PaymentMethod and the line is an advance accounting line, that's allowed to

        boolean valid = true;
        String errorPath = TemPropertyConstants.NEW_SOURCE_ACCTG_LINE;
        for (TemSourceAccountingLine sourceLine : (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
            if (line.equals(sourceLine)){
                errorPath = "document." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + travelDocument.getSourceAccountingLines().indexOf(line) + "]";
                break;
            }
        }

        // Test added accounting lines for null values and if there is an access change.
        valid = SpringContext.getBean(TravelDocumentService.class).validateSourceAccountingLines(travelDocument, false);

        if ((!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TRAVEL_DOC_APP_DOC_STATUS_INIT))
                && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.IN_PROCESS))
                && (!travelDocument.getAppDocStatus().equalsIgnoreCase(TemConstants.TravelAuthorizationStatusCodeKeys.CHANGE_IN_PROCESS))) {
            if (!line.getAccount().getAccountFiscalOfficerUser().getPrincipalId().equals(currentUser.getPrincipalId())
                    && !canUpdate) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_TA_FISCAL_OFFICER_ACCOUNT, line.getAccountNumber());
                return false;
            }
        }
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);

        // skip accounting line validation for TA
        if (!(event.getDocument() instanceof TravelAuthorizationDocument)) {
            if (ObjectUtils.isNotNull(line.getObjectTypeCode())) {
                // check to make sure they're the same
                List<AccountingDistribution> list = SpringContext.getBean(AccountingDistributionService.class).buildDistributionFrom(travelDocument);
                List<AccountingLineDistributionKey> distributionList = new ArrayList<AccountingLineDistributionKey>();
                List<String> expectedObjectCodes = new ArrayList<String>();
                for (AccountingDistribution dist : list) {
                    distributionList.add(new AccountingLineDistributionKey(dist.getObjectCode(), dist.getCardType()));
                    expectedObjectCodes.add(dist.getObjectCode());
                }
                final String expectedObjectCodesString = StringUtils.join(expectedObjectCodes, ", ");

                if (!distributionList.contains(new AccountingLineDistributionKey(line.getFinancialObjectCode(), line.getCardType()))) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE, line.getFinancialObjectCode(), line.getCardType(), expectedObjectCodesString);
                    valid &= false;
                }
            }
        }

        if (line.getAmount().isLessEqual(KualiDecimal.ZERO) && !travelDocument.getBlanketTravel()) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_CUSTOM, "Amount must be greater than zero.");
            valid &= false;
        }

        if (valid){
          //Fly America validation
            TravelDocument document = (TravelDocument) event.getDocument();
            List<TemExpense> allExpenses = new ArrayList<TemExpense>();
            allExpenses.addAll(document.getImportedExpenses());
            allExpenses.addAll(document.getActualExpenses());
            if (allExpenses.size() > 0){
                boolean hasAttachment = false;
                boolean showFlyAmerica = false;
                for (Note note : document.getNotes()){
                    if (note.getAttachment() != null){
                        hasAttachment = true;
                        break;
                    }
                }
                boolean isCGEnabled = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(KFSConstants.CoreModuleNamespaces.CHART, KFSConstants.RouteLevelNames.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_FUND_GROUP_DENOTES_CG);
                if (isCGEnabled){
                    for (TemExpense expense : allExpenses){
                        if (expense.getExpenseTypeCode().equals(TemConstants.ExpenseTypes.AIRFARE)){
                            Map<String,Object> fieldValues = new HashMap<String, Object>();
                            fieldValues.put(KRADPropertyConstants.CODE,TemConstants.ExpenseTypes.AIRFARE);
                            fieldValues.put(KRADPropertyConstants.NAME,expense.getTravelCompanyCodeName());
                            TravelCompanyCode travelCompany = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TravelCompanyCode.class, fieldValues);
                            if (travelCompany != null && travelCompany.isForeignCompany()){
                                String financialObjectCode = expense.getExpenseTypeObjectCode() != null ? expense.getExpenseTypeObjectCode().getFinancialObjectCode() : null;
                                if (travelDocument instanceof TravelAuthorizationDocument && expense instanceof ActualExpense){
                                    if (document.getTripType() != null) {
                                        financialObjectCode = document.getTripType().getEncumbranceObjCode();
                                    }
                                }
                                if (financialObjectCode != null && financialObjectCode.equals(line.getFinancialObjectCode())){
                                    String cg = SpringContext.getBean(ParameterService.class).getParameterValueAsString(KFSConstants.CoreModuleNamespaces.CHART, KFSConstants.RouteLevelNames.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_CG_DENOTING_VALUE);
                                    if (line.getAccount() == null){
                                        line.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
                                    }
                                    if (line.getAccount().getSubFundGroup() == null){
                                        line.refreshReferenceObject(KFSPropertyConstants.SUB_FUND_GROUP);
                                    }
                                    if (line.getAccount().getSubFundGroup().getFundGroupCode().equals(cg)){
                                        showFlyAmerica = true;
                                    }
                                }
                            }
                        }
                    }
                }

                //Fly America error has been triggered, determine what accounting line to show it on.
                if (showFlyAmerica && !hasAttachment){
                    boolean newLine = true;
                    for (TemSourceAccountingLine sourceLine : (List<TemSourceAccountingLine>)travelDocument.getSourceAccountingLines()){
                        if (line.equals(sourceLine)){
                            newLine = false;
                        }
                    }
                    //if line is a new accounting line or a current one being saved/submitted in the document.
                    //figure out where the new accounting line will be added and set the error to that line #
                    if (newLine) {
                        GlobalVariables.getMessageMap().clearErrorPath();

                        int newIndex = document.getSourceAccountingLine(document.getSourceAccountingLines().size() - 1).getSequenceNumber() + 1;
                        errorPath = "document." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + newIndex + "]";
                        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    }

                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_ACCOUNTING_LINE_CG);
                }
            }
        }


        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().getErrorPath().addAll(holdErrors);

        return valid;
    }

    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

    /**
     * Check if workflow is at the specific node
     *
     * @param workflowDocument
     * @param nodeName
     * @return
     */
    protected boolean isAtTravelNode(WorkflowDocument workflowDocument) {
        Set<String> nodeNames = workflowDocument.getNodeNames();
        for (String nodeNamesNode : nodeNames) {
            if (TemWorkflowConstants.RouteNodeNames.AP_TRAVEL.equals(nodeNamesNode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if a) the document is at the PaymentMethod route node; and b) the accountingLine is for a travel advance
     * @param document a travel document
     * @param accountingLine the accounting line to validate
     * @return true if the advance payment/payment method is correct and accessibility should not be checked; false otherwise
     */
    protected boolean isAdvancePaymentMethodException(Document document, TemSourceAccountingLine accountingLine) {
        return StringUtils.equals(TemConstants.TRAVEL_ADVANCE_ACCOUNTING_LINE_TYPE_CODE, accountingLine.getFinancialDocumentLineTypeCode()) &&
                document.getDocumentHeader().getWorkflowDocument().getCurrentNodeNames().contains(KFSConstants.RouteLevelNames.PAYMENT_METHOD);
    }

}
