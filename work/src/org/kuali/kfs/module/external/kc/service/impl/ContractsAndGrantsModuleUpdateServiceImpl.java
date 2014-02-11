/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleUpdateService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateDto;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateStatusDto;
import org.kuali.kfs.module.external.kc.dto.AwardSearchCriteriaDto;
import org.kuali.kfs.module.external.kc.webService.AwardWebSoapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kra.external.award.AwardWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class ContractsAndGrantsModuleUpdateServiceImpl implements ContractsAndGrantsModuleUpdateService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleUpdateServiceImpl.class);

    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setInvoiceDocumentStatus(invoiceDocumentStatus);
        updateDto.setDoInvoiceDocStatusUpdate(true);
        if (invoiceStatus.equalsIgnoreCase("FINAL")) {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
        }

        // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
        else if (invoiceStatus.equalsIgnoreCase("CORRECTED")) {
            updateDto.setRestorePreviousBillDate(true);
        }
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    protected AwardSearchCriteriaDto buildSearchDto(Map<String, Object> criteria) {
        AwardSearchCriteriaDto dto = new AwardSearchCriteriaDto();
        dto.setAccountNumber((String) criteria.get(KFSPropertyConstants.ACCOUNT_NUMBER));
        dto.setChartOfAccounts((String) criteria.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        dto.setAwardId((Long) criteria.get(KFSPropertyConstants.PROPOSAL_NUMBER));
        return dto;

    }

    @Override
    public void setLastBilledDateToAward(Long proposalNumber, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoLastBillDateUpdate(true);
        updateDto.setLastBillDate(lastBilledDate);
        AwardSearchCriteriaDto searchDto = new AwardSearchCriteriaDto();
        searchDto.setAwardId(proposalNumber);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(searchDto, updateDto));
    }

    @Override
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoLocCreationTypeUpdate(true);
        updateDto.setLocCreationType(locCreationType);
        AwardSearchCriteriaDto searchDto = new AwardSearchCriteriaDto();
        searchDto.setAwardId(proposalNumber);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(searchDto, updateDto));

    }

    @Override
    public void setLOCReviewIndicatorToAwardAccount(Map<String, Object> criteria, boolean locReviewIndicator) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoLocReviewUpdate(true);
        updateDto.setLocReviewIndicator(locReviewIndicator);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    @Override
    public void setFinalBilledToAwardAccount(Map<String, Object> criteria, boolean finalBilled) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoFinalBilledUpdate(true);
        updateDto.setFinalBilledIndicator(finalBilled);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    @Override
    public void setAwardAccountInvoiceDocumentStatus(Map<String, Object> criteria, String invoiceDocumentStatus) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoInvoiceDocStatusUpdate(true);
        updateDto.setInvoiceDocumentStatus(invoiceDocumentStatus);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));

    }

    @Override
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoAmountToDrawUpdate(true);
        updateDto.setAmountToDraw(amountToDraw);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));

    }

    public AwardWebService getAwardWebService() {
        return getWebService();
    }


    @Override
    public void setBillsisItBilled(List<Map<String, String>> fieldValuesList, String value) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, String invoiceStatus, Date lastBilledDate, String invoiceDocumentStatus) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoInvoiceDocStatusUpdate(true);
        updateDto.setInvoiceDocumentStatus(invoiceDocumentStatus);
        updateDto.setDoFinalBilledUpdate(true);
        updateDto.setFinalBilledIndicator(finalBilled);
        if (invoiceStatus.equalsIgnoreCase("FINAL")) {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
        }

        // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
        else if (invoiceStatus.equalsIgnoreCase("CORRECTED")) {
            updateDto.setRestorePreviousBillDate(true);
        }
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(mapKey), updateDto));
    }

    protected void handleBillingStatusResult(AwardBillingUpdateStatusDto dto) {
        if (!dto.isSuccess()) {
            throw new RuntimeException(dto.getErrorMessages().get(0));
        }
    }

    protected AwardWebService getWebService() {
        // first attempt to get the service from the KSB - works when KFS & KC share a Rice instance
        AwardWebService awardWebService = (AwardWebService) GlobalResourceLoader.getService(KcConstants.Award.SERVICE);

        // if we couldn't get the service from the KSB, get as web service - for when KFS & KC have separate Rice instances
        if (awardWebService == null) {
            LOG.warn("Couldn't get AwardWebService from KSB, setting it up as SOAP web service - expected behavior for bundled Rice, but not when KFS & KC share a standalone Rice instance.");
            AwardWebSoapService ss =  null;
            try {
                ss = new AwardWebSoapService();
            }
            catch (MalformedURLException ex) {
                LOG.error("Could not intialize AwardWebSoapService: " + ex.getMessage());
                throw new RuntimeException("Could not intialize AwardWebSoapService: " + ex.getMessage());
            }
            awardWebService = ss.getAwardWebServicePort();
        }

        return awardWebService;
    }

}
