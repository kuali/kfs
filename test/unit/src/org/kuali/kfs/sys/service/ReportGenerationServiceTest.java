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
package org.kuali.kfs.sys.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants.ReportGeneration;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.AccountFixture;
import org.kuali.kfs.sys.fixture.SubAccountFixture;
import org.kuali.kfs.sys.report.ReportInfo;

@ConfigureContext
public class ReportGenerationServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportGenerationServiceTest.class);

    private ReportGenerationService reportGenerationService;
    private ReportInfo infoForParameterMapReport;
    private ReportInfo infoForDataSourceReport;

    @Override
    public void setUp() throws Exception {
        super.setUp();

        reportGenerationService = SpringContext.getBean(ReportGenerationService.class);

        Map<String, ReportInfo> reportInfoBeans = SpringContext.getBeansOfType(ReportInfo.class);
        infoForParameterMapReport = reportInfoBeans.get("infoForParameterMapReport");
        infoForDataSourceReport = reportInfoBeans.get("infoForDataSourceReport");
    }

    // the report data are hold by data source, which can be an instance of java.util.Collection, an object array or JRDataSource
    public void testGenerateReportToPdfFile_WithDataSource() throws Exception {
        String reportFileName = infoForDataSourceReport.getReportFileName();
        String reportDirectoty = infoForDataSourceReport.getReportsDirectory();
        String reportTemplateClassPath = infoForDataSourceReport.getReportTemplateClassPath();
        String reportTemplateName = infoForDataSourceReport.getReportTemplateName();
        ResourceBundle resourceBundle = infoForDataSourceReport.getResourceBundle();
        String subReportTemplateClassPath = infoForDataSourceReport.getSubReportTemplateClassPath();
        Map<String, String> subReports = infoForDataSourceReport.getSubReports();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        try {
            String template = reportTemplateClassPath + reportTemplateName;
            String fullReportFileName = reportDirectoty + reportFileName;
            Collection<Account> accountDataSource = this.getAccounts();
            
            reportGenerationService.generateReportToPdfFile(reportData, accountDataSource, template, fullReportFileName);
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }

    // the report data are managed by a parameter map so the multiple data source can be passed to report template.
    public void testGenerateReportToPdfFile_WithParameterMap() throws Exception {
        String reportFileName = infoForParameterMapReport.getReportFileName();
        String reportDirectoty = infoForParameterMapReport.getReportsDirectory();
        String reportTemplateClassPath = infoForParameterMapReport.getReportTemplateClassPath();
        String reportTemplateName = infoForParameterMapReport.getReportTemplateName();
        ResourceBundle resourceBundle = infoForParameterMapReport.getResourceBundle();
        String subReportTemplateClassPath = infoForParameterMapReport.getSubReportTemplateClassPath();
        Map<String, String> subReports = infoForParameterMapReport.getSubReports();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        reportData.put("accounts", this.getAccounts());
        reportData.put("subAccounts", this.getSubAccounts());

        try {
            String template = reportTemplateClassPath + reportTemplateName;
            String fullReportFileName = reportDirectoty + reportFileName;
            
            reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }
    
    public void testGenerateReportToPdfFile_100DataSet() throws Exception {
        this.generateReportWithLargeDataSet(100);
    }
    
    public void testGenerateReportToPdfFile_1000DataSet() throws Exception {
        this.generateReportWithLargeDataSet(1000);
    }

    public void testGenerateReportToPdfFile_10000DataSet() throws Exception {
        this.generateReportWithLargeDataSet(10000);
    }
    
    private void generateReportWithLargeDataSet(int size) throws Exception {
        String reportFileName = infoForParameterMapReport.getReportFileName();
        String reportDirectoty = infoForParameterMapReport.getReportsDirectory();
        String reportTemplateClassPath = infoForParameterMapReport.getReportTemplateClassPath();
        String reportTemplateName = infoForParameterMapReport.getReportTemplateName();
        ResourceBundle resourceBundle = infoForParameterMapReport.getResourceBundle();
        String subReportTemplateClassPath = infoForParameterMapReport.getSubReportTemplateClassPath();
        Map<String, String> subReports = infoForParameterMapReport.getSubReports();

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, subReportTemplateClassPath);
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        Collection<Account> accounts = this.getAccounts(size);
        reportData.put("accounts", this.getAccounts(size));
        reportData.put("subAccounts", this.getSubAccounts(size));

        try {
            String template = reportTemplateClassPath + reportTemplateName;
            String fullReportFileName = reportDirectoty + reportFileName + "LargeDataSet" + size;
            
            long start = System.currentTimeMillis();
            reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
            
            long duration = System.currentTimeMillis() - start;
            LOG.info("=======Data Size: " +  accounts.size() + " account records and " + accounts.size() + " sub account records.");
            LOG.info("=======Execution time: " +  duration + " millis");
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }

    // create a list of accounts that will be posted in report
    private Collection<Account> getAccounts(int size) {
        Collection<Account> accounts = new ArrayList<Account>();
        for(int i = 0; i< size; i++) {
            for (AccountFixture account : AccountFixture.values()) {
                accounts.add(account.createAccount());
            }
        }
        return accounts;
    }

    // create a list of sub accounts that will be posted in report 
    private Collection<SubAccount> getSubAccounts(int size) {
        Collection<SubAccount> subAccounts = new ArrayList<SubAccount>();
        for(int i = 0; i< size; i++) {
            for (SubAccountFixture subAccount : SubAccountFixture.values()) {
                subAccounts.add(subAccount.createSubAccount());
            }
        }
        return subAccounts;
    }

    // create a list of accounts that will be posted in report
    private Collection<Account> getAccounts() {
        Collection<Account> accounts = new ArrayList<Account>();
        for (AccountFixture account : AccountFixture.values()) {
            accounts.add(account.createAccount());
        }
        return accounts;
    }

    // create a list of sub accounts that will be posted in report 
    private Collection<SubAccount> getSubAccounts() {
        Collection<SubAccount> subAccounts = new ArrayList<SubAccount>();
        for (SubAccountFixture subAccount : SubAccountFixture.values()) {
            subAccounts.add(subAccount.createSubAccount());
        }
        return subAccounts;
    }
}
