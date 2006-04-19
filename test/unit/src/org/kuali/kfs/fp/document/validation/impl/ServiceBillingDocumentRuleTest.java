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
import org.kuali.test.KualiTestBaseWithFixtures;

/**
 * 
 * This class tests the business rules of the service billing document.  This is not implemented 
 * yet and needs to extend TransactionalDocumentRuleTestBase.
 * @author Kuali Financial Transaction Processing Team (kualidev@oncourse.iu.edu)
 */
public class ServiceBillingDocumentRuleTest extends KualiTestBaseWithFixtures {


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
    protected TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected TransactionalDocument createDocument5() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected TransactionalDocument createDocumentUnbalanced() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected List getValidObjectSubTypeSourceLines() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected List getInvalidObjectSubTypeSourceLines() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected List getValidObjectSubTypeTargetLines() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected List getInvalidObjectSubTypeTargetLines() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getCount0() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getCount1() {
        // TODO Auto-generated method stub
        return null;
    }

    public Integer getCount2() {
        // TODO Auto-generated method stub
        return null;
    }

    public GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        // TODO Auto-generated method stub
        return null;
    }

    public SourceAccountingLine getAssetSourceLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    public TargetAccountingLine getAssetTargetLine() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected Document createDocument() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected Document createDocumentValidForRouting() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected Document createDocumentInvalidForSave() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected Document createDocumentInvalidDescription() throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    protected String getDocumentTypeName() {
        // TODO Auto-generated method stub
        return "KualiServiceBillingDocument";
    }
    */
    
    /* 
     * Commented out until we get data in the db that matches what we need, or we can 
     * modify the objects directly (mock-testing)
     * 
     public final void testApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc()
     throws Exception {
     triggerInvalidAddAccountingLineEvents
     ( getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents
     ( getTransactionalDocument().getTargetAccountingLines() );
     
     }

     public final void testApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() 
     throws Exception {
     triggerValidAddAccountingLineEvents
     ( getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents
     ( getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() 
     throws Exception {
     triggerValidAddAccountingLineEvents
     ( getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents
     ( getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidCapObjCodes() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidObjLevelCode() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidObjType() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidObjType() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidFundGroup() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidFundGroup() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }
     
     public final void testApplyAddAccountingLineBusinessRulesValidSubFundGroup() 
     throws Exception {
     triggerValidAddAccountingLineEvents( 
     getTransactionalDocument().getSourceAccountingLines() );
     triggerInvalidAddAccountingLineEvents( 
     getTransactionalDocument().getTargetAccountingLines() );
     }*/

    /*
     * This following block of commented out code contains valuable data that should be used for this 
     * rule class when we get to this document.  This data needs to be transformed into the new 
     * xml based fixtures framework.
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
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     
     sourceLines.add( sourceLine1 );     
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     
     targetLines.add( sourceLine1 );     
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     
     }  
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     SubFundGroup sfg = new SubFundGroup();
     sfg.setSubFundGroupCode(SUB_FUND_GROUP_CODE.CONTINUE_EDUC);
     sourceLine1.getAccount().setSubFundGroup(sfg);
     
     sourceLines.add( sourceLine1 );      
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     
     targetLines.add( sourceLine1 );     
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     } 
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine2 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine5 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" );
     
     sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     
     
     sourceLines.add( sourceLine1 ); 
     sourceLines.add(sourceLine2);
     sourceLines.add(sourceLine3);
     sourceLines.add(sourceLine4);
     sourceLines.add(sourceLine5);
     sourceLines.add(sourceLine6);
     sourceLines.add(sourceLine7);
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine2 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine5 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     
     targetLines.add( targetLine1 ); 
     targetLines.add(targetLine2);
     targetLines.add(targetLine3);
     targetLines.add(targetLine4);
     targetLines.add(targetLine5);
     targetLines.add(targetLine6);
     targetLines.add(targetLine7);    
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidCapObjCodes() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine2 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine5 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine8 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine9 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine10 = fixtureSourceAccountingLine( "1696", "1000" );
     
     sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     sourceLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     sourceLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     sourceLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     
     
     sourceLines.add( sourceLine1 ); 
     sourceLines.add(sourceLine2);
     sourceLines.add(sourceLine3);
     sourceLines.add(sourceLine4);
     sourceLines.add(sourceLine5);
     sourceLines.add(sourceLine6);
     sourceLines.add(sourceLine7);
     sourceLines.add(sourceLine8);
     sourceLines.add(sourceLine9);
     sourceLines.add(sourceLine10);
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine2 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine5 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine8 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine9 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine10 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     targetLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     targetLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     targetLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     
     targetLines.add( targetLine1 ); 
     targetLines.add(targetLine2);
     targetLines.add(targetLine3);
     targetLines.add(targetLine4);
     targetLines.add(targetLine5);
     targetLines.add(targetLine6);
     targetLines.add(targetLine7);   
     targetLines.add(targetLine8);
     targetLines.add(targetLine9);
     targetLines.add(targetLine10);
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     ObjLevel level = new ObjLevel();
     level.setFinancialObjectLevelCode(OBJECT_LEVEL_CODE.CONTRACT_GRANTS); 
     sourceLine1.getObjectCode().setFinancialObjectLevel(level);
     
     
     sourceLines.add( sourceLine1 ); 
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLine1.getObjectCode().setFinancialObjectLevel(level);
     
     
     targetLines.add( targetLine1 ); 
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidObjLevelCode() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjType() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine2 = fixtureSourceAccountingLine( "1696", "1000" );
     sourceLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     sourceLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     
     
     sourceLines.add( sourceLine1 ); 
     sourceLines.add(sourceLine2);
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine2 = fixtureTargetAccountingLine( "1696", "1000" );
     targetLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     targetLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     
     
     targetLines.add( targetLine1 );
     targetLines.add(targetLine2);
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidObjType() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     AccountingLine sourceLine2 = fixtureSourceAccountingLine( "1696", "1000" );
     
     sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     
     
     sourceLines.add( sourceLine1 ); 
     sourceLines.add(sourceLine2);
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     AccountingLine targetLine2 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     
     targetLines.add( targetLine1 ); 
     targetLines.add(targetLine2);
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidFundGroup() throws Exception {
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     //This sub fund group needs to have a fund group
     //code of FUND_GROUP_CODE.LOAN_FUND
     This may not be necessary if we have an appropriate account
     BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
     String subFundGroupCode = "SOME_CODE";
     Map subFundGroupId = new HashMap();
     subFundGroupId.put("code", subFundGroupCode);
     SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     
     sourceLines.add( sourceLine1 ); 
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLines.add( targetLine1 ); 
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesValidFundGroup() throws Exception {
     
     }
     
     public void fixturesApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() throws Exception {
     //Doing this test requires grabbing
     //SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT
     List sourceLines = new ArrayList();
     List targetLines = new ArrayList();
     
     //This sub fund group needs to have a fund group
     //code of FUND_GROUP_CODE.LOAN_FUND
     This may not be necessary if we have an appropriate account
     BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService();
     String subFundGroupCode = "SOME_CODE";
     Map subFundGroupId = new HashMap();
     subFundGroupId.put("code", subFundGroupCode);
     SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     
     addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" );
     addFixture( KualiRuleTestCase.EXPLANATION, 
     "This is a test document, testing invalid object sub-type (Student Fees) "
     + "with invalid sub-fund group (DCEDU) "
     + "accounting line business rules." );
     AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     
     sourceLines.add( sourceLine1 ); 
     addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     
     AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     
     targetLines.add( targetLine1 ); 
     addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines );
     }     
     */
}
