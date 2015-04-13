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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.report.service.FederalFinancialReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Default implementation of the FederalFinancialReportService
 */
public class FederalFinancialReportServiceImpl implements FederalFinancialReportService {

    /**
     * @see org.kuali.kfs.module.ar.service.FederalFinancialReportService#validate(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String validate(String federalForm, String proposalNumber, String fiscalYear, String reportingPeriod, String agencyNumber) {
        if (StringUtils.isNotEmpty(federalForm)) {
            if (StringUtils.equals(ArConstants.FEDERAL_FORM_425, federalForm)) {
                if (ObjectUtils.isNull(proposalNumber)) {
                    return PROPOSAL_NUMBER_REQUIRED;
                }
                else if (StringUtils.isEmpty(fiscalYear) || StringUtils.isEmpty(reportingPeriod)) {
                    return FISCAL_YEAR_AND_PERIOD_REQUIRED;
                }
            }
            else if (StringUtils.equals(ArConstants.FEDERAL_FORM_425A, federalForm)) {
                if (ObjectUtils.isNull(agencyNumber)) {
                    return AGENCY_REQUIRED;
                }
                else if (StringUtils.isEmpty(fiscalYear) || StringUtils.isEmpty(reportingPeriod)) {
                    return FISCAL_YEAR_AND_PERIOD_REQUIRED;
                }
            }
            return "";
        }
        return FINANCIAL_FORM_REQUIRED;
    }

    /**
     * @see org.kuali.kfs.module.ar.service.FederalFinancialReportService#getUrlForPrintInvoice(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getUrlForPrintInvoice(String basePath, String docId, String period, String year, String agencyNumber, String formType, String methodToCall) {
        String baseUrl = basePath + "/" + ArConstants.UrlActions.FEDERAL_FINANCIAL_REPORT;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        if (StringUtils.isNotEmpty(formType)) {
            parameters.put(FEDERAL_FORM, formType);
        }
        if (StringUtils.isNotEmpty(agencyNumber)) {
            parameters.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);
        }
        if (StringUtils.isNotEmpty(period)) {
            parameters.put(REPORTING_PERIOD, period);
        }
        if (StringUtils.isNotEmpty(year)) {
            parameters.put(FISCAL_YEAR, year);
        }
        if (StringUtils.isNotEmpty(docId)) {
            parameters.put(KFSConstants.PARAMETER_DOC_ID, docId);
            parameters.put(KFSConstants.PARAMETER_COMMAND, KFSConstants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        }

        return UrlFactory.parameterizeUrl(baseUrl, parameters);
    }

}
