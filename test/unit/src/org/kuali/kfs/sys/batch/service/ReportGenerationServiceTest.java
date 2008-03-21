/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.service;

import static org.kuali.test.fixtures.AccountFixture.ACTIVE_ACCOUNT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import net.sf.jasperreports.engine.JRParameter;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSConstants.ReportGeneration;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.util.ExtractProcessReportDataHolder;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.AccountFixture;
import org.kuali.test.fixtures.SubAccountFixture;
import org.springframework.core.io.ClassPathResource;

@ConfigureContext
public class ReportGenerationServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportGenerationServiceTest.class);

    private ReportGenerationService reportGenerationService;
    private String reportDirectoty;
    
    private String reportTemplateClassPath = "org/kuali/kfs/report/";
    private String resourceBundleBaseName = reportTemplateClassPath + "ReportGenerationService";

    @Override
    public void setUp() throws Exception {
        super.setUp();

        reportGenerationService = SpringContext.getBean(ReportGenerationService.class);
        reportDirectoty = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }
    
    // the report data are hold by data source, which can be an instance of java.util.Collection, an object array or JRDataSource
    public void testGenerateReportToPdfFile_WithDataSource() throws Exception {
        String fullReportFileName = reportDirectoty + "/testGenerateReportToPdfFileWithDataSource";
        String template = reportTemplateClassPath + "MasterReportWithDataSource";

        Collection<Account> accounts = new ArrayList<Account>();
        for(AccountFixture account : AccountFixture.values()) {  
            accounts.add(account.createAccount());
        }
        
        Map<String, String> subReports = new HashMap<String, String>();
        subReports.put("accountReport", "AccountReport");

        Map<String, Object> reportData = new HashMap<String, Object>();
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, reportTemplateClassPath);
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle(resourceBundleBaseName));
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        try {
            reportGenerationService.generateReportToPdfFile(reportData, accounts, template, fullReportFileName);
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }
    
    // the report data are managed by a parameter map so the multiple data source can be passed to report template.
    public void testGenerateReportToPdfFile_WithParameterMap() throws Exception {
        String fullReportFileName = reportDirectoty + "/testGenerateReportToPdfFileWithParameterMap";
        String template = reportTemplateClassPath + "MasterReportWithParamterMap";
        
        Collection<Account> accounts = new ArrayList<Account>();
        for(AccountFixture account : AccountFixture.values()) {  
            accounts.add(account.createAccount());
        }
                
        Collection<SubAccount> subAccounts = new ArrayList<SubAccount>();
        for(SubAccountFixture subAccount : SubAccountFixture.values()) {  
            subAccounts.add(subAccount.createSubAccount());
        }
        
        Map<String, String> subReports = new HashMap<String, String>();
        subReports.put("accountReport", "AccountReport");
        subReports.put("subAccountReport", "SubAccountReport");
        
        Map<String, Object> reportData = new HashMap<String, Object>(); 
        reportData.put(JRParameter.REPORT_RESOURCE_BUNDLE, ResourceBundle.getBundle(resourceBundleBaseName));        
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_DIR, reportTemplateClassPath);
        
        reportData.put("accounts", accounts);
        reportData.put("subAccounts", subAccounts);
        
        reportData.put(ReportGeneration.PARAMETER_NAME_SUBREPORT_TEMPLATE_NAME, subReports);

        try {
            reportGenerationService.generateReportToPdfFile(reportData, template, fullReportFileName);
        }
        catch (Exception e) {
            fail("fail to generate PDF file." + e);
        }
    }
}
