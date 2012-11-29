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

import org.kuali.kfs.fp.businessobject.TravelCompanyCode;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TravelAuthorizationFields;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.authorization.TravelDocumentPresentationController;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.util.SourceAccountingLineComparator;
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
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class TEMAccountingLineAllowedObjectCodeValidation extends GenericValidation {

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
        List errors = GlobalVariables.getMessageMap().getErrorPath();
        GlobalVariables.getMessageMap().clearErrorPath();
        TravelDocument travelDocument = (TravelDocument) event.getDocument();

        TravelDocumentPresentationController documentPresentationController = (TravelDocumentPresentationController) getDocumentHelperService().getDocumentPresentationController(travelDocument);
        boolean canUpdate = documentPresentationController.enableForDocumentManager(GlobalVariables.getUserSession().getPerson());

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
                for (AccountingDistribution dist : list) {
                    distributionList.add(new AccountingLineDistributionKey(dist.getObjectCode(), dist.getCardType()));
                }

                if (!distributionList.contains(new AccountingLineDistributionKey(line.getFinancialObjectCode(), line.getCardType()))) {
                    GlobalVariables.getMessageMap().putError(TravelAuthorizationFields.FIN_OBJ_CD, TemKeyConstants.ERROR_TEM_ACCOUNTING_LINES_OBJECT_CODE_CARD_TYPE, line.getFinancialObjectCode(), line.getCardType());
                    valid &= false;
                }
            }
        }


        if (line.getAmount().isLessEqual(KualiDecimal.ZERO)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_CUSTOM, "Amount must be greater than zero.");
            valid &= false;
        }

        if (valid){
          //Fly America validation
            TravelDocument document = (TravelDocument) event.getDocument();
            List<TEMExpense> allExpenses = new ArrayList<TEMExpense>();
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
                    for (TEMExpense expense : allExpenses){
                        if (expense.getTravelCompanyCodeCode().equals(TemConstants.ExpenseTypes.AIRFARE)){
                            Map<String,Object> fieldValues = new HashMap<String, Object>();
                            fieldValues.put(KRADPropertyConstants.CODE,TemConstants.ExpenseTypes.AIRFARE);
                            fieldValues.put(KRADPropertyConstants.NAME,expense.getTravelCompanyCodeName());
                            TravelCompanyCode travelCompany = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(TravelCompanyCode.class, fieldValues);
                            if (travelCompany != null && travelCompany.isForeignCompany()){
                                String financialObjectCode = expense.getTravelExpenseTypeCode() != null ? expense.getTravelExpenseTypeCode().getFinancialObjectCode() : null;
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
                        SourceAccountingLineComparator comparator = new SourceAccountingLineComparator();

                        int newIndex = 0;
                        for (TemSourceAccountingLine sourceLine : (List<TemSourceAccountingLine>)document.getSourceAccountingLines()){
                            if (comparator.compare(line,sourceLine) < 0){
                                newIndex = sourceLine.getSequenceNumber().intValue() - 1;
                                break;
                            }
                            else{
                                newIndex++;
                            }
                        }
                        errorPath = "document." + TemPropertyConstants.SOURCE_ACCOUNTING_LINE + "[" + newIndex + "]";
                        GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                    }

                    GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, TemKeyConstants.ERROR_ACCOUNTING_LINE_CG);
                }
            }
        }


        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().getErrorPath().addAll(errors);

        return valid;
    }

    private DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

}
