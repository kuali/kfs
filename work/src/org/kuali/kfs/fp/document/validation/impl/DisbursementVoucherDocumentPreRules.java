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

import java.text.MessageFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.rules.PreRulesContinuationBase;
import org.kuali.core.rules.RulesUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.document.DisbursementVoucherDocument;

/**
 * Checks warnings and prompt conditions for dv document.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class DisbursementVoucherDocumentPreRules extends PreRulesContinuationBase implements DisbursementVoucherRuleConstants {
    private KualiConfigurationService kualiConfiguration;


    /**
     * @see org.kuali.core.rules.PreRulesContinuationBase#doRules(org.kuali.core.document.MaintenanceDocument)
     */
    public boolean doRules(Document document) {
        boolean preRulesOK = true;

        DisbursementVoucherDocument dvDocument = (DisbursementVoucherDocument) document;
        checkSpecialHandlingIndicator(dvDocument);

        preRulesOK &= checkNonEmployeeTravelTabState(dvDocument);
        
        return preRulesOK;
    }

    /**
     * If the special handling name and address 1 fields have value, this will mark the special handling indicator for the user.
     * 
     * @param dvDocument
     */
    private void checkSpecialHandlingIndicator(DisbursementVoucherDocument dvDocument) {
        if (StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitPersonName()) && StringUtils.isNotBlank(dvDocument.getDvPayeeDetail().getDisbVchrRemitLine1Addr())) {
            dvDocument.setDisbVchrSpecialHandlingCode(true);
        }
    }

    /**
     * 
     * This method...
     * @param dvDocument
     * @return Returns true if the state of all the tabs is valid, false otherwise.
     */
    private boolean checkNonEmployeeTravelTabState(DisbursementVoucherDocument dvDocument) {
        boolean tabStatesOK = true; 
        
        DisbursementVoucherNonEmployeeTravel dvNonEmplTrav = dvDocument.getDvNonEmployeeTravel();
        
        String[] travelNonEmplPaymentReasonCodes = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(DV_DOCUMENT_PARAMETERS_GROUP_NM, NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM);
        
        if(hasValues(dvNonEmplTrav) && !RulesUtils.makeSet(travelNonEmplPaymentReasonCodes).contains(dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode())) {
            String questionText = SpringServiceLocator.getKualiConfigurationService().getPropertyString(KeyConstants.QUESTION_CLEAR_NON_EMPLOYEE_TRAVEL_TAB);

            Object[] args = { dvDocument.getDvPayeeDetail().getDisbVchrPaymentReasonCode(), travelNonEmplPaymentReasonCodes[0] };
            questionText = MessageFormat.format(questionText, args);
            
            boolean clearTab = super.askOrAnalyzeYesNoQuestion(Constants.DisbursementVoucherDocumentConstants.CLEAR_NON_EMPLOYEE_TAB_QUESTION_ID, questionText);
            if (clearTab) {
                DisbursementVoucherNonEmployeeTravel blankDvNonEmplTrav = new DisbursementVoucherNonEmployeeTravel();
                blankDvNonEmplTrav.setFinancialDocumentNumber(dvNonEmplTrav.getFinancialDocumentNumber());
                blankDvNonEmplTrav.setVersionNumber(dvNonEmplTrav.getVersionNumber());
                dvDocument.setDvNonEmployeeTravel(blankDvNonEmplTrav);
            } else {
                // return to document if the user doesn't want to clear the Non Employee Travel tab
                super.event.setActionForwardName(Constants.MAPPING_BASIC);
                tabStatesOK = false;
            }
        }
        
        return tabStatesOK;
    }
    
    /**
     * 
     * This method...
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains any data in any fields.
     */
    private boolean hasValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        boolean hasValues = false;

        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrNonEmpTravelerName());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrServicePerformedDesc());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDvServicePerformedLocName());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDvServiceRegularEmprName());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromCityName());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelFromStateCode());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDvTravelFromCountryCode());
        hasValues |= ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemStartDttmStamp());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCityName());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToStateCode());
        hasValues |= StringUtils.isNotBlank(dvNonEmplTrav.getDisbVchrTravelToCountryCode());
        hasValues |= ObjectUtils.isNotNull(dvNonEmplTrav.getDvPerdiemEndDttmStamp());
        
        if(!hasValues) {
            hasValues = hasPerDiemValues(dvNonEmplTrav);
        }
        
        if(!hasValues) {
            hasValues = hasPersonalVehicleValues(dvNonEmplTrav);
        }
        
        if(!hasValues) {
            hasValues = hasNonEmployeeTravelExpenses(dvNonEmplTrav);
        }
        
        if(!hasValues) {
            hasValues = hasNonEmployeeTravelPrepaidExpenses(dvNonEmplTrav);
        }
        
        return hasValues;
    }

    /**
     * 
     * This method...
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains data in any of the fields in the per diem section
     */
    private boolean hasPerDiemValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {        
        return !StringUtils.isBlank(dvNonEmplTrav.getDisbVchrPerdiemCategoryName()) || 
               !ObjectUtils.isNull(dvNonEmplTrav.getDisbVchrPerdiemRate()) || 
               !ObjectUtils.isNull(dvNonEmplTrav.getDisbVchrPerdiemCalculatedAmt()) || 
               !ObjectUtils.isNull(dvNonEmplTrav.getDisbVchrPerdiemActualAmount()) ||
               !StringUtils.isBlank(dvNonEmplTrav.getDvPerdiemChangeReasonText());
    }

    /**
     * 
     * This method...
     * @param dvNonEmplTrav
     * @return True if non employee travel tab contains data in any of the fields in the personal vehicle section
     */
    private boolean hasPersonalVehicleValues(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {        
        return !StringUtils.isBlank(dvNonEmplTrav.getDisbVchrAutoFromCityName()) || 
               !StringUtils.isBlank(dvNonEmplTrav.getDisbVchrAutoFromStateCode()) || 
               !StringUtils.isBlank(dvNonEmplTrav.getDisbVchrAutoToCityName()) ||
               !StringUtils.isBlank(dvNonEmplTrav.getDisbVchrAutoToStateCode()) ||
               !ObjectUtils.isNull(dvNonEmplTrav.getDisbVchrMileageCalculatedAmt()) ||
               !ObjectUtils.isNull(dvNonEmplTrav.getDisbVchrPersonalCarAmount());
    }

    /**
     * 
     * This method...
     * @return
     */
    private boolean hasNonEmployeeTravelExpenses(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return dvNonEmplTrav.getDvNonEmployeeExpenses().size() > 0;
    }


    /**
     * 
     * This method...
     * @param dvNonEmplTrav
     * @return
     */
    private boolean hasNonEmployeeTravelPrepaidExpenses(DisbursementVoucherNonEmployeeTravel dvNonEmplTrav) {
        return dvNonEmplTrav.getDvPrePaidEmployeeExpenses().size() > 0;
    }
    
}