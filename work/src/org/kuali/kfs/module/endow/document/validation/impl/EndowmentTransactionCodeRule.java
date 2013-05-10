/*
 * Copyright 2009 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.endow.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowKeyConstants;
import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class EndowmentTransactionCodeRule extends MaintenanceDocumentRuleBase {


    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean isValid = true;
        isValid &= super.processCustomRouteDocumentBusinessRules(document);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();

        if (isValid) {
            isValid &= checkGLLinks(document);
        }

        return isValid;
    }

    /**
     * Checks that glLinks is not empty. Each Etran Code must have at least one associated record in END_ETRAN_GL_LNK_T.
     * 
     * @param document maintenance document
     * @return true if valid, false otherwise
     */
    private boolean checkGLLinks(MaintenanceDocument document) {
        boolean isValid = true;
        EndowmentTransactionCode endowmentTransactionCode = (EndowmentTransactionCode) document.getNewMaintainableObject().getBusinessObject();
        List<GLLink> activeGLLinks = new ArrayList<GLLink>();

        int i = 0;
        for (GLLink link : endowmentTransactionCode.getGlLinks()) {

            // check that ObjectCode.ObjectTypeCode.BasicAccountingCategory == EndowmentTransactionCode.TransactionTypeCode
            link.refreshReferenceObject(EndowPropertyConstants.GL_LINK_FINANCIAL_OBJECT_CODE);

            if (!validateGLLinkObjectCode(endowmentTransactionCode, link)) {
                isValid = false;
                putFieldError(EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS + "[" + i + "]" + "." + EndowPropertyConstants.GL_LINK_OBJECT_CD, EndowKeyConstants.EndowmentTransactionConstants.ERROR_GL_LINK_OBJ_CD_ACC_CATEGORY_MUST_EQUAL_ETRAN_TYPE);
                break;
            }

            // add it to the active glLinks list
            if (link.isActive()) {
                activeGLLinks.add(link);
            }
            i++;
        }

        // check if the endowment transaction code has at least one active GL Link
        if (isValid) {
            if (activeGLLinks.size() == 0) {
                putFieldError(EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS, EndowKeyConstants.EndowmentTransactionConstants.ERROR_ENDOWMENT_TRANSACTION_MUST_HAVE_AT_LEAST_ONE_GLLINK);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean isValid = true;
        isValid &= super.processCustomAddCollectionLineBusinessRules(document, collectionName, bo);
        MessageMap errorMap = GlobalVariables.getMessageMap();
        isValid &= errorMap.hasNoErrors();
        EndowmentTransactionCode endowmentTransactionCode = (EndowmentTransactionCode) document.getNewMaintainableObject().getBusinessObject();
        GLLink glLink = (GLLink) bo;

        if (isValid) {
            bo.refreshReferenceObject(EndowPropertyConstants.GL_LINK_FINANCIAL_OBJECT_CODE);

            // check that ObjectCode.ObjectTypeCode.BasicAccountingCategory == EndowmentTransactionCode.TransactionTypeCode
            if (!validateGLLinkObjectCode(endowmentTransactionCode, glLink)) {
                isValid = false;
                putFieldError(KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS + "." + EndowPropertyConstants.GL_LINK_OBJECT_CD, EndowKeyConstants.EndowmentTransactionConstants.ERROR_GL_LINK_OBJ_CD_ACC_CATEGORY_MUST_EQUAL_ETRAN_TYPE);
            }

            // check that the selected chart and the object code chart are the same
            if (ObjectUtils.isNotNull(glLink.getFinancialObjectCode()) && !StringUtils.equalsIgnoreCase(glLink.getFinancialObjectCode().getChartOfAccountsCode(), glLink.getChartCode())) {
                isValid = false;
                putFieldError(KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS + "." + EndowPropertyConstants.GL_LINK_CHART_CD, EndowKeyConstants.EndowmentTransactionConstants.ERROR_GL_LINK_CHART_CD_MUST_EQUAL_OBJECT_CHART_CD);
            }

            // check that there is no other GL Link with the same chart already added
            if (collectionName.equals(EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS)) {
                if (glLink.getChartCode() != null) {
                    for (GLLink tmpGlLink : endowmentTransactionCode.getGlLinks()) {
                        if (glLink.getChartCode().equals(tmpGlLink.getChartCode())) {
                            isValid = false;
                            putFieldError(KFSConstants.ADD_PREFIX + "." + EndowPropertyConstants.ENDOWMENT_TRANSACTION_GL_LINKS + "." + EndowPropertyConstants.GL_LINK_CHART_CD, EndowKeyConstants.EndowmentTransactionConstants.ERROR_GL_LINK_WITH_SAME_CHART_ALREADY_EXISTS);
                        }
                    }
                }
            }
        }

        return isValid;
    }

    /**
     * Checks that the selected object code for the glLink is valid.
     * 
     * @param endowmentTransactionCode the endowment transaction
     * @param glLink the gl link
     * @return true if the object code is valid, false otherwise
     */
    private boolean validateGLLinkObjectCode(EndowmentTransactionCode endowmentTransactionCode, GLLink glLink) {
        boolean isValid = true;

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        ObjectCode objectCode = glLink.getFinancialObjectCode();

        if (ObjectUtils.isNotNull(objectCode)) {

            // check that ObjectCode.ObjectTypeCode.BasicAccountingCategory == EndowmentTransactionCode.TransactionTypeCode
            if (EndowConstants.EndowmentTransactionTypeCodes.ASSET_TYPE_CODE.equalsIgnoreCase(endowmentTransactionCode.getEndowmentTransactionTypeCode())) {
                String basicAccountingCategoryAsset = parameterService.getParameterValueAsString(EndowmentTransactionCode.class, EndowParameterKeyConstants.ASSETS_ENTRAN_TYPE);

                if (!objectCode.getFinancialObjectType().getBasicAccountingCategoryCode().equalsIgnoreCase(basicAccountingCategoryAsset)) {
                    isValid = false;
                }
            }

            if (EndowConstants.EndowmentTransactionTypeCodes.EXPENSE_TYPE_CODE.equalsIgnoreCase(endowmentTransactionCode.getEndowmentTransactionTypeCode())) {
                String basicAccountingCategoryExpense = parameterService.getParameterValueAsString(EndowmentTransactionCode.class, EndowParameterKeyConstants.EXPENSES_ENTRAN_TYPE);

                if (!objectCode.getFinancialObjectType().getBasicAccountingCategoryCode().equalsIgnoreCase(basicAccountingCategoryExpense)) {
                    isValid = false;
                }
            }

            if (EndowConstants.EndowmentTransactionTypeCodes.INCOME_TYPE_CODE.equalsIgnoreCase(endowmentTransactionCode.getEndowmentTransactionTypeCode())) {
                String basicAccountingCategoryIncome = parameterService.getParameterValueAsString(EndowmentTransactionCode.class, EndowParameterKeyConstants.INCOME_ENTRAN_TYPE);

                if (!objectCode.getFinancialObjectType().getBasicAccountingCategoryCode().equalsIgnoreCase(basicAccountingCategoryIncome)) {
                    isValid = false;
                }
            }

            if (EndowConstants.EndowmentTransactionTypeCodes.LIABILITY_TYPE_CODE.equalsIgnoreCase(endowmentTransactionCode.getEndowmentTransactionTypeCode())) {
                String basicAccountingCategoryLiabilities = parameterService.getParameterValueAsString(EndowmentTransactionCode.class, EndowParameterKeyConstants.LIABILITIES_ENTRAN_TYPE);

                if (!objectCode.getFinancialObjectType().getBasicAccountingCategoryCode().equalsIgnoreCase(basicAccountingCategoryLiabilities)) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }

}
