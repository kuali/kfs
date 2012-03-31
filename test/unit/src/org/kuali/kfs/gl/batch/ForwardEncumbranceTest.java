/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.batch;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.kuali.kfs.gl.batch.service.EncumbranceClosingOriginEntryGenerationService;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryOffsetPair;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Tests that the forward encumbrance process is generating cost share encumbrance forwarding origin entries correctly
 */
@ConfigureContext
public class ForwardEncumbranceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ForwardEncumbranceTest.class);

    /**
     * An enum with a set of encumbrances to test; here, we've only got one encumbrance, an encumbrance
     * that should force forward encumbrances to generate a cost share forwarding entry/offset pair
     */
    enum ENCUMBRANCE_FIXTURE {
        COST_SHARE_ENCUMBRANCE("BL", "4531413", "CS001", "7100", "EX", "EE");

        // to find account: select ca_prior_yr_acct_t.fin_coa_cd, ca_prior_yr_acct_t.ACCOUNT_NBR, CA_A21_SUB_ACCT_T.sub_acct_nbr
        // from (ca_prior_yr_acct_t join ca_sub_fund_grp_t on ca_prior_yr_acct_t.SUB_FUND_GRP_CD =
        // ca_sub_fund_grp_t.SUB_FUND_GRP_CD) join CA_A21_SUB_ACCT_T on CA_PRIOR_YR_ACCT_T.fin_coa_cd = CA_A21_SUB_ACCT_T.fin_coa_cd
        // and CA_PRIOR_YR_ACCT_T.account_nbr = CA_A21_SUB_ACCT_T.account_nbr where ca_sub_fund_grp_t.FUND_GRP_CD = 'CG' and
        // CA_A21_SUB_ACCT_T.sub_acct_typ_cd = 'CS'
        // this was a rough one, it needed a cost share sub account and a CG sub fund group fund group type

        private String chart;
        private String accountNumber;
        private String subAccountNumber;
        private String objectCode;
        private String balanceType;
        private String objectTypeCode;

        /**
         * Constructs a ForwardEncumbranceTest.ENCUMBRANCE_FIXTURE
         * @param chart the chart of the encumbrance
         * @param accountNumber the account of the encumbrance
         * @param subAccountNumber the sub account of the encumbrance
         * @param objectCode the object code of the encumbrance
         * @param balanceType the balance type code of the encumbrance
         * @param objectTypeCode the object type code of the encumbrance
         */
        private ENCUMBRANCE_FIXTURE(String chart, String accountNumber, String subAccountNumber, String objectCode, String balanceType, String objectTypeCode) {
            this.chart = chart;
            this.accountNumber = accountNumber;
            this.subAccountNumber = subAccountNumber;
            this.objectCode = objectCode;
            this.balanceType = balanceType;
            this.objectTypeCode = objectTypeCode;
        }

        /**
         * Converts one of the members of this enum to an actual Encumbrance
         * 
         * @return a real encumbrance!
         */
        public Encumbrance convertToEncumbrance() {
            Encumbrance e = new Encumbrance();
            Integer fy = TestUtils.getFiscalYearForTesting().intValue() - 1;
            e.setUniversityFiscalYear(fy);
            e.setChartOfAccountsCode(chart);
            e.setAccountNumber(accountNumber);
            e.setSubAccountNumber(subAccountNumber);
            e.setObjectCode(objectCode);
            e.setSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            e.setBalanceTypeCode(balanceType);
            e.setDocumentTypeCode("EXEN"); // we don't need this field
            e.setOriginCode("EP");
            e.setDocumentNumber("000001"); // we don't need this field
            GregorianCalendar lastYear = new GregorianCalendar();
            lastYear.set(Calendar.YEAR, (TestUtils.getFiscalYearForTesting().intValue() - 1));
            e.setTransactionEncumbranceDate(new java.sql.Date(lastYear.getTimeInMillis()));
            e.setTransactionEncumbranceDescription("MONKEYS-R-US IS THE NEWEST AND GREATEST STORE IN THE ENTIRE TRI-STATE AREA");
            e.setAccountLineEncumbranceAmount(new KualiDecimal(1000));
            e.setAccountLineEncumbranceClosedAmount(KualiDecimal.ZERO);
            return e;
        }

        /**
         * Returns the object type code of this enum
         * 
         * @return this enum's object type code
         */
        public String getObjectType() {
            return this.objectTypeCode;
        }
    }

    /**
     * Tests that the expected encumbrance fixtures would be selected by the forward encumbrance process
     */
    public void testEncumbranceSelection() {
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationService = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);

        assertTrue(encumbranceClosingOriginEntryGenerationService.shouldForwardEncumbrance(ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.convertToEncumbrance()));
    }

    /**
     * Tests that the expted fixtures would be selected for cost share entry/offset generation by the forward encumbrance process
     * 
     * @throws Exception thrown if something goes wrong
     */
    public void testCostShareSelection() throws Exception {
        final EncumbranceClosingOriginEntryGenerationService encumbranceClosingOriginEntryGenerationService = SpringContext.getBean(EncumbranceClosingOriginEntryGenerationService.class);

        Encumbrance encumbrance = ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.convertToEncumbrance();
        final Integer postingYear = new Integer(TestUtils.getFiscalYearForTesting().intValue() - 1);
        OriginEntryOffsetPair entryPair = encumbranceClosingOriginEntryGenerationService.createBeginningBalanceEntryOffsetPair(encumbrance, postingYear, new java.sql.Date(new GregorianCalendar().getTimeInMillis()));

        assertTrue(encumbranceClosingOriginEntryGenerationService.shouldForwardCostShareForEncumbrance(entryPair.getEntry(), entryPair.getOffset(), encumbrance, ENCUMBRANCE_FIXTURE.COST_SHARE_ENCUMBRANCE.getObjectType()));

        OriginEntryOffsetPair costShareEntryPair = encumbranceClosingOriginEntryGenerationService.createCostShareBeginningBalanceEntryOffsetPair(encumbrance, new java.sql.Date(new GregorianCalendar().getTimeInMillis()));
        assertFalse( "Should not have had a fatal error: " + costShareEntryPair, costShareEntryPair.isFatalErrorFlag() );
        LOG.info(costShareEntryPair.getEntry().getLine());
        LOG.info(costShareEntryPair.getOffset().getLine());

        assertTrue(costShareEntryPair.getOffset().getLine().indexOf("GENERATED") >= 0);
    }
}
