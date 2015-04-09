/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.service.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentFileLoad;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.ResearchParticipantPaymentValidationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

public class ResearchParticipantPaymentValidationServiceImpl implements ResearchParticipantPaymentValidationService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ResearchParticipantPaymentValidationServiceImpl.class);

    private BusinessObjectService businessObjectService;

    private ParameterService parameterService;


    @Override
    public boolean validatePaymentAccount(PaymentFileLoad paymentFile, MessageMap errorMap) {
        PaymentAccountDetail accountDetail = this.getPaymentAccountDetail(paymentFile);

        if(ObjectUtils.isNull(accountDetail)){
            return true;
        }

        boolean isValid = this.validateProjectCode(accountDetail, errorMap);

        return isValid;
    }

    @Override
    public PaymentAccountDetail getPaymentAccountDetail(PaymentFileLoad paymentFile) {

        List<PaymentGroup> paymentGroups = paymentFile.getPaymentGroups();
        if(ObjectUtils.isNull(paymentGroups) || paymentGroups.isEmpty()){
            return null;
        }

        PaymentGroup firstPaymentGroup = paymentGroups.get(0);
        if(ObjectUtils.isNull(firstPaymentGroup)){
            return null;
        }

        List<PaymentDetail> paymentDetails = firstPaymentGroup.getPaymentDetails();
        if(ObjectUtils.isNull(paymentDetails) || paymentDetails.isEmpty()){
            return null;
        }

        PaymentDetail firstPaymentDetail = paymentDetails.get(0);
        if(ObjectUtils.isNull(firstPaymentDetail)){
            return null;
        }

        List<PaymentAccountDetail> paymentAccountDetails = firstPaymentDetail.getAccountDetail();
        if(ObjectUtils.isNull(paymentAccountDetails) || paymentAccountDetails.isEmpty()){
            return null;
        }

        return paymentAccountDetails.get(0);
    }

    /**
     * validate the given project code, which must exist in the database and active
     */
    protected boolean validateProjectCode(PaymentAccountDetail accountDetail, MessageMap errorMap) {
        String projectCode = accountDetail.getProjectCode();
        if (StringUtils.isBlank(projectCode) || StringUtils.equals(projectCode, KFSConstants.getDashProjectCode())) {
            return true;
        }

        ProjectCode project = this.getBusinessObjectService().findBySinglePrimaryKey(ProjectCode.class, projectCode);
        if (ObjectUtils.isNull(project)) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_RESEARCH_PAYMENT_LOAD_INVALID_PROJECT_CODE, projectCode);

            return false;
        }
        else if (!project.isActive()) {
            errorMap.putError(KFSConstants.GLOBAL_ERRORS, PdpKeyConstants.ERROR_RESEARCH_PAYMENT_LOAD_INACTIVE_PROJECT_CODE, projectCode);

            return false;
        }

        return true;
    }

    @Override
    public boolean isResearchParticipantPayment(CustomerProfile customer) {
        boolean result = false;
        if (parameterService.parameterExists(PaymentDetail.class, PdpConstants.RESEARCH_PARTICIPANT_CUSTOMER_PROFILE)) {
            Collection<String> researchParticipantCustomers = parameterService.getParameterValuesAsString(PaymentDetail.class, PdpConstants.RESEARCH_PARTICIPANT_CUSTOMER_PROFILE);
            for (String researchParticipantCustomer : researchParticipantCustomers) {
                String[] customerArray = researchParticipantCustomer.split(KFSConstants.DASH);
                if (customer.getChartCode().equals(customerArray[0]) && customer.getUnitCode().equals(customerArray[1]) && customer.getSubUnitCode().equals(customerArray[2])) {
                    return true;
                }
            }
        }
        return result;
    }

    /**
     * Gets the businessObjectService attribute.
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }


}
