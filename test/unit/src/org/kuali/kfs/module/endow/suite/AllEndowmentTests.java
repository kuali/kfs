/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.suite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.kfs.module.endow.batch.service.impl.AccrualProcessingServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.AvailableCashUpdateServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.CreateAccrualTransactionsServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.CreateAutomatedCashInvestmentTransactionsServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.CreateCashSweepTransactionsServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.CurrentTaxLotBalanceUpdateServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.GeneralLedgerInterfaceBatchProcessServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.HoldingHistoryMarketValuesUpdateServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.PooledFundControlTransactionsServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.ProcessFeeTransactionsServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.RollFrequencyDatesServiceImplTest;
import org.kuali.kfs.module.endow.batch.service.impl.RollProcessDateServiceImplTest;
import org.kuali.kfs.module.endow.businessobject.KEMIDTest;
import org.kuali.kfs.module.endow.document.service.HoldingHistoryServiceTest;
import org.kuali.kfs.module.endow.document.validation.impl.AssetDecreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.AssetIncreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.CashDecreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.CashIncreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.CashTransferDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.CorporateReorganizationDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.CorpusAdjustmentDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.EndowmentToGLTransferOfFundsDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.GLToEndowmentTransferOfFundsDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.HoldingAdjustmentDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.HoldingHistoryValueAdjustmentDocumentRuleTest;
import org.kuali.kfs.module.endow.document.validation.impl.HoldingTaxLotRebalanceRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.LiabilityDecreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.LiabilityIncreaseDocumentRulesTest;
import org.kuali.kfs.module.endow.document.validation.impl.SecurityRuleTest;
import org.kuali.kfs.module.endow.document.validation.impl.SecurityTransferDocumentRulesTest;

/**
 * Runs all endowment tests.
 */
public class AllEndowmentTests {

    /**
     * Returns a suite of all the tests in ENDOW that have been added here.
     * 
     * @return a Test suite with most all ENDOW tests
     */
    public static Test suite() {
        TestSuite suite = new TestSuite();

        // Daniela's unit tests....
        suite.addTestSuite(AssetIncreaseDocumentRulesTest.class);
        suite.addTestSuite(AssetDecreaseDocumentRulesTest.class);
        suite.addTestSuite(SecurityTransferDocumentRulesTest.class);
        suite.addTestSuite(EndowmentToGLTransferOfFundsDocumentRulesTest.class);
        suite.addTestSuite(GLToEndowmentTransferOfFundsDocumentRulesTest.class);
        suite.addTestSuite(EndowmentToGLTransferOfFundsDocumentRulesTest.class);
        suite.addTestSuite(AccrualProcessingServiceImplTest.class);

        // Muddu's unit tests....
        suite.addTestSuite(HoldingAdjustmentDocumentRulesTest.class);
        suite.addTestSuite(HoldingHistoryValueAdjustmentDocumentRuleTest.class);
        suite.addTestSuite(LiabilityDecreaseDocumentRulesTest.class);
        suite.addTestSuite(LiabilityIncreaseDocumentRulesTest.class);
        suite.addTestSuite(AvailableCashUpdateServiceImplTest.class);
        suite.addTestSuite(CurrentTaxLotBalanceUpdateServiceImplTest.class);
        suite.addTestSuite(HoldingHistoryMarketValuesUpdateServiceImplTest.class);
        suite.addTestSuite(HoldingHistoryServiceTest.class);
        suite.addTestSuite(HoldingHistoryValueAdjustmentDocumentRuleTest.class);
        suite.addTestSuite(CorpusAdjustmentDocumentRulesTest.class);
        suite.addTestSuite(HoldingAdjustmentDocumentRulesTest.class);
        suite.addTestSuite(ProcessFeeTransactionsServiceImplTest.class);
        suite.addTestSuite(CreateAccrualTransactionsServiceImplTest.class);
        suite.addTestSuite(CorporateReorganizationDocumentRulesTest.class);
        suite.addTestSuite(GeneralLedgerInterfaceBatchProcessServiceImplTest.class);
        
        // David's unit tests
        suite.addTestSuite(CashIncreaseDocumentRulesTest.class);
        suite.addTestSuite(CashDecreaseDocumentRulesTest.class);
        suite.addTestSuite(CashTransferDocumentRulesTest.class);
        suite.addTestSuite(RollProcessDateServiceImplTest.class);
        suite.addTestSuite(RollFrequencyDatesServiceImplTest.class);
        suite.addTestSuite(PooledFundControlTransactionsServiceImplTest.class);
        suite.addTestSuite(CreateAutomatedCashInvestmentTransactionsServiceImplTest.class);
        suite.addTestSuite(CreateCashSweepTransactionsServiceImplTest.class);
        suite.addTestSuite(KEMIDTest.class); 
        
        //Suma's unit tests....
        suite.addTestSuite(SecurityRuleTest.class);
        
        // Samuel's unit tests....
        suite.addTestSuite(HoldingTaxLotRebalanceRulesTest.class);
        
        return suite;
    }

    /**
     * Runs all the tests in the all test test suite
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        junit.textui.TestRunner.run(suite());
    }
}
