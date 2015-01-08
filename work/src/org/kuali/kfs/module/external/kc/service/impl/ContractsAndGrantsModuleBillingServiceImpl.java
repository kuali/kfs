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
package org.kuali.kfs.module.external.kc.service.impl;

import java.net.MalformedURLException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.Award;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateDto;
import org.kuali.kfs.module.external.kc.dto.AwardBillingUpdateStatusDto;
import org.kuali.kfs.module.external.kc.dto.AwardFieldValuesDto;
import org.kuali.kfs.module.external.kc.service.ExternalizableLookupableBusinessObjectService;
import org.kuali.kfs.module.external.kc.webService.AwardWebSoapService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kra.external.award.AwardWebService;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of Contracts & Grants module billing service which will allow AR to utilize KC functionality to perform CGB actions.
 */
public class ContractsAndGrantsModuleBillingServiceImpl implements ContractsAndGrantsModuleBillingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsAndGrantsModuleBillingServiceImpl.class);

    protected ExternalizableLookupableBusinessObjectService awardService;

    @Override
    public List<? extends ContractsAndGrantsAward> lookupAwards(Map<String, String> fieldValues, boolean unbounded) {
        return (List<Award>)getAwardService().getSearchResults(fieldValues);
    }

    @Override
    public ContractsAndGrantsBillingAward updateAwardIfNecessary(Long proposalNumber, ContractsAndGrantsBillingAward currentAward ) {
        ContractsAndGrantsBillingAward award = currentAward;

        if (ObjectUtils.isNull(proposalNumber)) {
            award = null;
        } else {
            if (ObjectUtils.isNull(currentAward) || !currentAward.getProposalNumber().equals(proposalNumber))  {
                Map<String, Long> primaryKeys = new HashMap<>();
                primaryKeys.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
                award = (Award)awardService.findByPrimaryKey(primaryKeys);
            }
        }
        return award;
    }

    public ExternalizableLookupableBusinessObjectService getAwardService() {
        return awardService;
    }

    public void setAwardService(ExternalizableLookupableBusinessObjectService awardService) {
        this.awardService = awardService;
    }

    @Override
    public void setLastBilledDateToAwardAccount(Map<String, Object> criteria, boolean invoiceReversal, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            updateDto.setRestorePreviousBillDate(true);
        } else {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
        }

        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));
    }

    protected AwardFieldValuesDto buildSearchDto(Map<String, Object> criteria) {
        AwardFieldValuesDto dto = new AwardFieldValuesDto();
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
        AwardFieldValuesDto searchDto = new AwardFieldValuesDto();
        searchDto.setAwardId(proposalNumber);
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(searchDto, updateDto));
    }

    @Override
    public void setLOCCreationTypeToAward(Long proposalNumber, String locCreationType) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoLocCreationTypeUpdate(true);
        updateDto.setLocCreationType(locCreationType);
        AwardFieldValuesDto searchDto = new AwardFieldValuesDto();
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
    public void setAmountToDrawToAwardAccount(Map<String, Object> criteria, KualiDecimal amountToDraw) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoAmountToDrawUpdate(true);
        updateDto.setAmountToDraw(amountToDraw.bigDecimalValue());
        handleBillingStatusResult(getAwardWebService().updateAwardBillingStatus(buildSearchDto(criteria), updateDto));

    }

    public AwardWebService getAwardWebService() {
        return getWebService();
    }


    @Override
    public void setFinalBilledAndLastBilledDateToAwardAccount(Map<String, Object> mapKey, boolean finalBilled, boolean invoiceReversal, Date lastBilledDate) {
        AwardBillingUpdateDto updateDto = new AwardBillingUpdateDto();
        updateDto.setDoFinalBilledUpdate(true);
        updateDto.setFinalBilledIndicator(finalBilled);

        if (invoiceReversal) {
            // If the invoice is corrected, transpose previous billed date to current and set previous last billed date to null.
            updateDto.setRestorePreviousBillDate(true);
        } else {
            updateDto.setDoLastBillDateUpdate(true);
            updateDto.setLastBillDate(lastBilledDate);
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
