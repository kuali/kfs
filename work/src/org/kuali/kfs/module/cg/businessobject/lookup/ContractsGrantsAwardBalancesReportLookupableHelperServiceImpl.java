/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.ContractsGrantsAwardBalancesReport;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.config.ConfigContext;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;

/**
 * Helper service class for Contracts Grants Award Balances Report
 */
public class ContractsGrantsAwardBalancesReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final Log LOG = LogFactory.getLog(ContractsGrantsAwardBalancesReportLookupableHelperServiceImpl.class);

    /**
     * This method performs the lookup and returns a collection of lookup items
     * 
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @SuppressWarnings({ "deprecation", "rawtypes" })
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean unbounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KNSConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KNSConstants.DOC_FORM_KEY));

        setBusinessObjectClass(Award.class);
        List<Award> searchResults = (List<Award>) super.getSearchResultsHelper(lookupFormFields, unbounded);
        setBusinessObjectClass(ContractsGrantsAwardBalancesReport.class);

        Collection<ContractsGrantsAwardBalancesReport> displayList = new ArrayList<ContractsGrantsAwardBalancesReport>();


        for (Award award : searchResults) {
            ContractsGrantsAwardBalancesReport awardBalancesReportEntry = new ContractsGrantsAwardBalancesReport();

            awardBalancesReportEntry.setProposalNumber(award.getProposalNumber());
            awardBalancesReportEntry.setAwardId(award.getAwardId());
            awardBalancesReportEntry.setAgency(award.getAgency());
            awardBalancesReportEntry.setAwardProjectTitle(award.getAwardProjectTitle());
            awardBalancesReportEntry.setAwardStatusCode(award.getAwardStatusCode());
            awardBalancesReportEntry.setAwardBeginningDate(award.getAwardBeginningDate());
            awardBalancesReportEntry.setAwardEndingDate(award.getAwardEndingDate());

            String primaryProjectDirectorName = (ObjectUtils.isNull(award.getAwardPrimaryProjectDirector())) || (ObjectUtils.isNull(award.getAwardPrimaryProjectDirector().getProjectDirector())) ? "" : award.getAwardPrimaryProjectDirector().getProjectDirector().getName();
            awardBalancesReportEntry.setAwardPrimaryProjectDirectorName(primaryProjectDirectorName);

            String primaryFundManagerName = (ObjectUtils.isNull(award.getAwardPrimaryFundManager())) || (ObjectUtils.isNull(award.getAwardPrimaryFundManager().getFundManager())) ? "" : award.getAwardPrimaryFundManager().getFundManager().getName();
            awardBalancesReportEntry.setAwardPrimaryFundManagerName(primaryFundManagerName);

            awardBalancesReportEntry.setAwardTotalAmountForReport(award.getAwardTotalAmount());

            KualiDecimal awardBilledToDate = SpringContext.getBean(AccountsReceivableModuleService.class).getAwardBilledToDateByProposalNumber(award.getProposalNumber());
            awardBalancesReportEntry.setTotalBilledToDate(awardBilledToDate);

            // calculate Total Payments To Date
            KualiDecimal totalPayments = SpringContext.getBean(AccountsReceivableModuleService.class).calculateTotalPaymentsToDateByAward(award.getProposalNumber());
            awardBalancesReportEntry.setTotalPaymentsToDate(totalPayments);
            awardBalancesReportEntry.setAmountCurrentlyDue(awardBilledToDate.subtract(totalPayments));

            displayList.add(awardBalancesReportEntry);

        }

        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }


    /**
     * This methods builds result table for the lookup results.
     * 
     * @param lookupForm
     * @param displayList
     * @param resultTable
     */
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action
        // urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);

                // add url when property is documentNumber
                if (col.getPropertyName().equals("documentNumber")) {
                    String url = ConfigContext.getCurrentContextConfig().getKEWBaseURL() + "/" + KEWConstants.DOC_HANDLER_REDIRECT_PAGE + "?" + KEWConstants.COMMAND_PARAMETER + "=" + KEWConstants.DOCSEARCH_COMMAND + "&" + KEWConstants.ROUTEHEADER_ID_PARAMETER + "=" + propValue;

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KNSConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(createTitleText(ContractsGrantsAwardBalancesReport.class), ContractsGrantsAwardBalancesReport.class, fieldList));

                    col.setColumnAnchor(a);
                }
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    /**
     * This method provides title text for the report
     * 
     * @param boClass
     * @return
     */
    protected String createTitleText(Class<? extends BusinessObject> boClass) {
        String titleText = "";

        final String titlePrefixProp = getKualiConfigurationService().getPropertyString("title.inquiry.url.value.prependtext");
        if (StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        final String objectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }

}
