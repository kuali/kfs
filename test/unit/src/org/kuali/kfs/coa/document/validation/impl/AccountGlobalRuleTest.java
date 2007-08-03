/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.module.chart.bo.AccountGlobal;
import org.kuali.test.RequiresSpringContext;

@RequiresSpringContext
public class AccountGlobalRuleTest extends ChartRuleTestBase {

    private class Accounts {
        private class ChartCode {
            private static final String GOOD1 = "BL";
            private static final String CLOSED1 = "BL";
            private static final String EXPIRED1 = "BL";
            private static final String GOOD2 = "UA";
            private static final String BAD1 = "ZZ";
        }

        private class AccountNumber {
            private static final String GOOD1 = "1031400";
            private static final String CLOSED1 = "2231414";
            private static final String EXPIRED1 = "2231404";
            private static final String BAD1 = "9999999";
        }

        private class Org {
            private static final String GOOD1 = "ACAD";
            private static final String BAD1 = "1234";
        }

        private class State {
            private static final String GOOD1 = "IN";
            private static final String BAD1 = "ZZ";
        }

        private class Zip {
            private static final String GOOD1 = "47405-3085";
            private static final String BAD1 = "12345-6789";
        }

        private class SubFund {
            private class Code {
                private static final String CG1 = "HIEDUA";
                private static final String GF1 = "GENFND";
                private static final String GF_MPRACT = "MPRACT";
                private static final String EN1 = "ENDOW";
            }

            private class FundGroupCode {
                private static final String CG1 = "CG";
                private static final String GF1 = "GF";
                private static final String EN1 = "EN";
            }

            private static final String GOOD1 = "GENFND";
        }

        private class HigherEdFunction {
            private static final String GOOD1 = "AC";
        }

        private class User {
            private class McafeeAlan {
                private static final String UNIVERSAL_ID = "1509103107";
                private static final String USER_ID = "AEMCAFEE";
                private static final String EMP_ID = "0000000013";
                private static final String NAME = "Mcafee, Alan";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }

            private class PhamAnibal {
                private static final String UNIVERSAL_ID = "1195901455";
                private static final String USER_ID = "AAPHAM";
                private static final String EMP_ID = "0000004686";
                private static final String NAME = "Pham, Anibal";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }

            private class AhlersEsteban {
                private static final String UNIVERSAL_ID = "1959008511";
                private static final String USER_ID = "AHLERS";
                private static final String EMP_ID = "0000002820";
                private static final String NAME = "Ahlers, Esteban";
                private static final String EMP_STATUS = "A";
                private static final String EMP_TYPE = "P";
            }
        }

        private class FiscalOfficer {
            private static final String GOOD1 = "4318506633";
        }

        private class Supervisor {
            private static final String GOOD1 = "4052406505";
        }

        private class Manager {
            private static final String GOOD1 = "4318506633";
        }

        private class UserIds {
            private static final String SUPER1 = "HEAGLE";
            private static final String GOOD1 = "KCOPLEY";
            private static final String GOOD2 = "KHUNTLEY";
        }
    }

    AccountGlobal newAccountGlobals;
    MaintenanceDocument maintDoc;
    AccountGlobalRule rule;

    public void testDefaultExistenceChecks_Org_KnownGood() {

        // create new account to test
        newAccountGlobals = new AccountGlobal();
        newAccountGlobals.setChartOfAccountsCode(Accounts.ChartCode.GOOD1);
        newAccountGlobals.setOrganizationCode(Accounts.Org.GOOD1);
        setUsersThatExist();

        // run the test
        testDefaultExistenceCheck(newAccountGlobals, "organizationCode", false);
        assertGlobalErrorMapEmpty();
    }

    private void setUsersThatExist() {
        newAccountGlobals.setAccountManagerSystemIdentifier(Accounts.Manager.GOOD1);
        newAccountGlobals.setAccountFiscalOfficerSystemIdentifier(Accounts.FiscalOfficer.GOOD1);
        newAccountGlobals.setAccountsSupervisorySystemsIdentifier(Accounts.Supervisor.GOOD1);
    }

    public void testDefaultExistenceChecks_AccountState_KnownGood() {

        // create new account to test
        newAccountGlobals = new AccountGlobal();
        newAccountGlobals.setAccountStateCode(Accounts.State.GOOD1);
        setUsersThatExist();

        // run the test
        testDefaultExistenceCheck(newAccountGlobals, "accountStateCode", false);
        assertGlobalErrorMapEmpty();
    }
}
