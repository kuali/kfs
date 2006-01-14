/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.rules;


import java.util.Collections;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the <code>{@link IndirectCostAdjustmentDocument}</code>'s rules 
 * and PE generation.  This is not currently implemented properly.  When we 
 * get to building this document, we would need to extend TransactionalDocumentRuleTestBase. 
 * For now it contains commented out old fixtures code that will need to be fitted to 
 * the new xml based fixtures framework.  
 * 
 * @author Kuali Transaction Processing Team (kualidev@oncourse.iu.edu)
 */
public class IndirectCostAdjustmentDocumentRuleTest extends KualiTestBaseWithSpring {
    //////////////////////////////////////////////////////////////////////////
    // Test methods start here                                              //
    //////////////////////////////////////////////////////////////////////////

    public final void testSave_nullDocument() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringServiceLocator.getDocumentService().save(null, null, Collections.EMPTY_LIST);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /*
     * This was taken from the old fixtures class and contains valuable data that 
     * we should use when we get around to building this document.  This should be put 
     * into the new xml fixtures framework though.
     *  
     public void fixturesDefault() {
     addFixture( KualiRuleTestCase.ACCOUNT, "1912610" );
     addFixture( KualiRuleTestCase.BALANCE_TYPE,
     TransactionalDocumentRuleBase.BALANCE_TYPE_CODE.ACTUAL );
     addFixture( KualiRuleTestCase.CHART, "UA" );
     addFixture( KualiRuleTestCase.OBJECT_TYPE_CODE, 
     TransactionalDocumentRuleBase.OBJECT_TYPE_CODE.CASH_NOT_INCOME );
     addFixture( KualiRuleTestCase.POSTING_YEAR, new Integer( 2005 ) );
     addFixture( KualiRuleTestCase.PROJECT, "BOB" );
     addFixture( KualiRuleTestCase.SUBACCOUNT, "BEER" );
     }

     public void fixturesPartiallyLoadedDoc() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();

     addFixture( KualiRuleTestCase.CHART, "UA" );
     addFixture( KualiRuleTestCase.ACCOUNT, "1912610" );
     addFixture( KualiRuleTestCase.SUBACCOUNT, "BEER" );
     addFixture( KualiRuleTestCase.PROJECT, "BOB" );

     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, "This is a test document, "
     + "testing valid accounting line business rules." );

     addFixture( KualiRuleTestCase.OBJECT_CODE, "9912" );
     sourceLines.add( fixtureSourceAccountingLine( null, "1000" ) );
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     addFixture( KualiRuleTestCase.OBJECT_CODE, "9903" );
     targetLines.add( fixtureTargetAccountingLine( null, "1000" ) );
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }

     public void fixturesApplyAddAccountingLineBusinessRulesInvalidSubObjectCode() 
     throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();

     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing valid "
     + "accounting line business rules." );

     sourceLines.add( fixtureSourceAccountingLine( "9897", "1000" ) );
     sourceLines.add( fixtureSourceAccountingLine( "9889", "1000" ) );       
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     targetLines.add( fixtureTargetAccountingLine( "9891", "1000" ) );
     targetLines.add( fixtureTargetAccountingLine( "9760", "1000" ) );       
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }    

     public void fixturesApplyAddAccountingLineBusinessRulesValidSubObjectCode() 
     throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();

     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "testing "
     + "IndirectCostAdjustmentDocumentServiceTest.createValidRuleDIDocument" );
     addFixture( KualiRuleTestCase.EXPLANATION, "This is a test document, "
     + "testing valid accounting line business rules." );
     
     sourceLines.add( fixtureSourceAccountingLine( "1696", "2000" ) );       
     sourceLines.add( fixtureSourceAccountingLine( "1696", "1000" ) );
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     targetLines.add( fixtureTargetAccountingLine( "1696", "2000" ) );
     targetLines.add( fixtureTargetAccountingLine( "1696", "1000" ) );
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public final void fixturesApplyRouteDocumentBusinessRulesNotInBalance()
     throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();

     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, "This is a test document, "
     + "testing valid accounting line business rules." );

     sourceLines.add( fixtureSourceAccountingLine( "9912", "2000" ) );
     sourceLines.add( fixtureSourceAccountingLine( "1698", "1000" ) );
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     targetLines.add( fixtureTargetAccountingLine( "9912", "1000" ) );
     targetLines.add( fixtureTargetAccountingLine( "1698", "2000" ) );
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }

     public final void fixturesApplyRouteDocumentBusinessRulesInvalidSubObjectCode() 
     throws Exception {
     fixturesApplyAddAccountingLineBusinessRulesInvalidSubObjectCode();
     }

     public final void fixturesApplyRouteDocumentBusinessRules_validDocument() 
     throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();

     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, "This is a test document, "
     + "testing valid accounting line business rules." );

     sourceLines.add( fixtureSourceAccountingLine( "5198", "2000" ) );
     sourceLines.add( fixtureSourceAccountingLine( "1696", "1000" ) );
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     targetLines.add( fixtureTargetAccountingLine( "5198", "2000" ) );
     targetLines.add( fixtureTargetAccountingLine( "1696", "1000" ) );
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     */
}
