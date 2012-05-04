/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.integration.ld.LaborLedgerEntry;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationCreateService;
import org.kuali.kfs.module.ec.batch.service.EffortCertificationExtractService;
import org.kuali.kfs.module.ec.testdata.EffortTestDataPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.Log4jConfigurer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.SpringContextForBatchRunner;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;


/**
 * This batch runner is just for testing purpose and used by effort team members.
 */
public class EffortBatchRunner {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortBatchRunner.class);

    private final Properties properties, message;
    private final String balanceFieldNames, entryFieldNames;
    private final String deliminator;

    private BusinessObjectService businessObjectService;
    private LaborModuleService laborModuleService;

    private Class<? extends LaborLedgerBalance> ledgerBalanceClass;
    private Class<? extends LaborLedgerEntry> ledgerEntryClass;

    public EffortBatchRunner() {
        String messageFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = EffortTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/effortCertificationExtractServiceProformance.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        message = TestDataPreparator.loadPropertiesFromClassPath(messageFileName);

        deliminator = properties.getProperty("deliminator");

        balanceFieldNames = properties.getProperty("balanceFieldNames");
        entryFieldNames = properties.getProperty("entryFieldNames");

        Log4jConfigurer.configureLogging(false);

        SpringContextForBatchRunner.initializeKfs();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        laborModuleService = SpringContext.getBean(LaborModuleService.class);

        KualiModuleService kualiModuleService = SpringContext.getBean(KualiModuleService.class);
        ledgerBalanceClass = LedgerBalance.class;
        ledgerEntryClass = kualiModuleService.getResponsibleModuleService(LaborLedgerEntry.class).createNewObjectFromExternalizableClass(LaborLedgerEntry.class).getClass();
    }

    public void loadData() {
        this.doCleanup();

        int numberOfEmplid = Integer.valueOf(StringUtils.trim(properties.getProperty("emplid.numOfEmplid")));
        for (int i = 1; i <= numberOfEmplid; i++) {
            String emplid = StringUtils.trim(properties.getProperty("emplid" + i));
            this.loadDataForEmployee(emplid, i);
        }
    }

    private void doCleanup() {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "2007");

        businessObjectService.deleteMatching(ledgerBalanceClass, fieldValues);
        businessObjectService.deleteMatching(ledgerEntryClass, fieldValues);

    }

    private void loadDataForEmployee(String emplid, Integer sequenceNumber) {
        int numberOfEntries = Integer.valueOf(StringUtils.trim(properties.getProperty("ledgerEntry.numOfEntries")));
        List<LaborLedgerEntry> ledgerEntries = TestDataPreparator.buildTestDataList(ledgerEntryClass, properties, "ledgerEntry.seed", entryFieldNames, deliminator, numberOfEntries);
        for (LaborLedgerEntry entry : ledgerEntries) {
            entry.setEmplid(emplid);
            entry.setTransactionLedgerEntrySequenceNumber(sequenceNumber);
        }
        businessObjectService.save(ledgerEntries);

        int numberOfBalances = Integer.valueOf(StringUtils.trim(properties.getProperty("ledgerBalance.numOfBalances")));
        List<LaborLedgerBalance> ledgerBalances = TestDataPreparator.buildTestDataList(ledgerBalanceClass, properties, "ledgerBalance.seed", balanceFieldNames, deliminator, numberOfBalances);
        for (LaborLedgerBalance balance : ledgerBalances) {
            balance.setEmplid(emplid);
        }
        businessObjectService.save(ledgerBalances);
    }


    public static void main(String[] args) {
        EffortBatchRunner batchRunner = new EffortBatchRunner();
        GlobalVariables.setMessageMap(new MessageMap());
        GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));

        Integer fiscalYear = 2007;
        String reportPeriod = "A01";
        if(args.length < 1) {
            throw new IllegalArgumentException("Wrong argument -- The argument only can be -load, -extract or -create");
        }
        else if(args.length >= 3) {
            fiscalYear = StringUtils.isNumeric(args[1]) ? Integer.parseInt(args[1]) : fiscalYear;
            reportPeriod = StringUtils.isNotBlank(args[2]) ? StringUtils.trim(args[2]).toUpperCase() : reportPeriod;
        }

        try {
            if (StringUtils.equalsIgnoreCase("-load", args[0])) {
                System.out.println("Loading data into labor Entry and balance tables ...");
                batchRunner.loadData();
            }
            else if (StringUtils.equalsIgnoreCase("-extract", args[0])) {
                System.out.println("Extracting Effort Certifications ...");
                EffortCertificationExtractService effortCertificationExtractService = SpringContext.getBean(EffortCertificationExtractService.class);
                effortCertificationExtractService.extract(fiscalYear, reportPeriod);
            }
            else if (StringUtils.equalsIgnoreCase("-create", args[0])) {
                System.out.println("Creating Effort Certifications ...");
                EffortCertificationCreateService effortCertificationCreateService = SpringContext.getBean(EffortCertificationCreateService.class);
                effortCertificationCreateService.create(fiscalYear, reportPeriod);
            }
            else {
                //throw new IllegalArgumentException("Wrong argument -- The argument only can be -load, -extract or -create");
                //batchRunner.loadData();

                EffortCertificationExtractService effortCertificationExtractService = SpringContext.getBean(EffortCertificationExtractService.class);
                effortCertificationExtractService.extract(fiscalYear, reportPeriod);

                EffortCertificationCreateService effortCertificationCreateService = SpringContext.getBean(EffortCertificationCreateService.class);
                effortCertificationCreateService.create(fiscalYear, reportPeriod);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            System.exit(0);
        }
    }
}
