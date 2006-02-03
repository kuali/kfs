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

import java.sql.Timestamp;

import junit.framework.AssertionFailedError;

import org.kuali.Constants;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.test.KualiTestBaseWithSpring;
import org.kuali.test.fixtures.FixtureEntryException;

/**
 * Class for unit testing the functionality of 
 * <code>{@link TransactionalDocumentUtil}</code>
 *
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class TransactionalDocumentRuleUtilTest
    extends KualiTestBaseWithSpring  {

    private static final String COLLECTION_NAME =
        "TransactionalDocumentRuleUtilTest.collection1";

    private static final String[] FIXTURE_COLLECTION_NAMES =
        { COLLECTION_NAME };

    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }
    private static long ONE_DAY_MILLIS = 86400000;
    
    private String _balanceTypeActual;
    private String _btcAttrName;

    private String _annualBalancePeriodCode;
    private String _apcAttrName;
    private Integer _currentFiscalYear;
    

    public void runTest() throws Throwable {
        try {
            super.runTest();
        }
        catch( AssertionFailedError afe ) {
            throw new FixtureEntryException( this, afe );
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here                                            //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Accessor method to </code>errorPropertyName</code>
     *
     * @return String
     */
    protected String getErrorPropertyName() {
        return Constants.GLOBAL_ERRORS;
    }
    
    /**
     * Fixture accessor method to get <code>{@link String}</code> serialization
     * instance of an active balance type.
     *
     * @return String
     */
    protected String getActiveBalanceType() {
        return getActualBalanceTypeCode();
    }

    /**
     * Fixture accessor method to get <code>{@link String}</code> serialization
     * instance of an inactive balance type.
     *
     * @return String
     */
    protected String getInactiveBalanceType() {
        return getActualBalanceTypeCode();
    }
    
    /**
     * Fixture accessor method for the ACTUAL balance type. This is defined
     * in the fixture XML.
     *
     * @return String
     */
    public String getActualBalanceTypeCode() {
        return _balanceTypeActual;
    }

    /**
     * Fixture accessor method for the ACTUAL balance type. This is defined
     * in the fixture XML.
     *
     * @param bt <code>{@link String} instance serialization of an ACTUAL
     * <code>{@link BalanceTyp}</code>
     */
    public void setActualBalanceTypeCode( String bt ) {
        _balanceTypeActual = bt;
    }
    
    /**
     * Fixture accessor method for the Annual Balance 
     * <code>{@link AccountingPeriod}</code>
     *
     * @return AccountingPeriod
     */
    public AccountingPeriod getAnnualBalanceAccountingPeriod() {
        return getAccountingPeriodService()
            .getByPeriod( getAnnualBalancePeriodCode(),
                          getCurrentFiscalYear() );
    }
    
    public Integer getCurrentFiscalYear() {
        return _currentFiscalYear;
    }
    
    public void setCurrentFiscalYear( Integer year ) {
        _currentFiscalYear = year;
    }

    /**
     * Fixture accessor method for the Annual Balance 
     * <code>{@link AccountingPeriod}</code>
     *
     * @return String
     */
    public String getAnnualBalancePeriodCode() {
        return _annualBalancePeriodCode;
    }

    /**
     * Fixture accessor method for the Annual Balance 
     * <code>{@link AccountingPeriod}</code>
     *
     * @param periodCode
     */
    public void setAnnualBalancePeriodCode( String periodCode ) {
        _annualBalancePeriodCode = periodCode;
    }
    
    /**
     * Fixture method for getting the property name of a 
     * <code>{@link BalanceTyp}</code> for displaying errors.
     *
     * @return String
     */
    public String getBalanceTypeCodeAttributeName() {
        return _btcAttrName;
    }

    /**
     * Fixture method for getting the property name of a 
     * <code>{@link BalanceTyp}</code> for displaying errors.
     *
     * @param n 
     */
    public void setBalanceTypeCodeAttributeName( String n ) {
        _btcAttrName = n;
    }

    /**
     * Fixture method for getting the property name of an 
     * <code>{@link AccountingPeriod}</code> for displaying errors.
     *
     * @return String
     */
    public String getAccountingPeriodCodeAttributeName() {
        return _apcAttrName;
    }

    /**
     * Fixture method for getting the property name of an 
     * <code>{@link AccountingPeriod}</code> for displaying errors.
     *
     * @param n 
     */
    public void setAccountingPeriodCodeAttributeName( String n ) {
        _apcAttrName = n;
    }
    
    /**
     * Fixture accessor method for an open 
     * <code>{@link AccountingPeriod}</code> instance.
     *
     * @return AccountingPeriod
     */
    protected AccountingPeriod getOpenAccountingPeriod() {
        return getAnnualBalanceAccountingPeriod();
    }

    /**
     * Fixture accessor method for a closed  
     * <code>{@link AccountingPeriod}</code> instance.
     *
     * @return AccountingPeriod
     */
    protected AccountingPeriod getClosedAccountingPeriod() {
        return null;
    }
    
    /**
     * Fixture accessor method for getting a <code>{@link Timestamp}</code> 
     * instance that is in the past.
     *
     * @return Timestamp
     */
    protected Timestamp getPastTimestamp() {
        return new Timestamp( System.currentTimeMillis() - ONE_DAY_MILLIS );
    }
    
    /**
     * Fixture accessor method for getting a <code>{@link Timestamp}</code> 
     * instance that is in the future.
     */
    protected Timestamp getFutureTimestamp() {
        return new Timestamp( System.currentTimeMillis() + ONE_DAY_MILLIS );
    }    
    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here                                              //
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here                                               //
    ///////////////////////////////////////////////////////////////////////////
    /**
     * test the <code>isValidBalanceType()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType()
     */
    public void testIsValidBalanceType_Active() {
        testIsValidBalanceType( getActiveBalanceType(), true );
    }

    /**
     * test the <code>isValidBalanceType()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType()
     */
    public void testIsValidBalanceType_Inactive() {
        testIsValidBalanceType( getInactiveBalanceType(), true );
    }

    /**
     * test the <code>isValidBalanceType()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType()
     */
    public void testIsValidBalanceType_Null() {
        testIsValidBalanceType( null, false );
    }
    
    /**
     * test the <code>isValidBalanceType()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @param balanceType
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidBalanceType()
     */
    protected void testIsValidBalanceType( String btStr, boolean expected ) {
        BalanceTyp balanceType = null;

        if( btStr != null ) {
            balanceType = getBalanceTypService().getBalanceTypByCode( btStr );
        }
        
        assertEquals( new Boolean( TransactionalDocumentRuleUtil
                                   .isValidBalanceType( balanceType, 
                                                        getBalanceTypeCodeAttributeName() ) ),
                      new Boolean( expected ) );
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod()
     */
    public void testIsValidOpenAccountingPeriod_Open() {
        testIsValidOpenAccountingPeriod( getOpenAccountingPeriod(), true );
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod()
     */
    public void pendingTestIsValidOpenAccountingPeriod_Closed() {
        testIsValidOpenAccountingPeriod( getClosedAccountingPeriod(), false );
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod()
     */
    public void testIsValidOpenAccountingPeriod_Null() {
        testIsValidOpenAccountingPeriod( null, false );
    }

    /**
     * test the <code>isValidOpenAccountingPeriod()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @param period
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidOpenAccountingPeriod()
     */
    protected void testIsValidOpenAccountingPeriod( AccountingPeriod period,
                                                    boolean expected ) {
        assertEquals( new Boolean( TransactionalDocumentRuleUtil
                                   .isValidOpenAccountingPeriod( period, 
                                                                 getAccountingPeriodCodeAttributeName() ) ), 
                      new Boolean( expected ) );
    }

    /**
     * test the <code>isValidReversalDate()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate()
     */
    public void testIsValidReversalDate_Null() {
        testIsValidReversalDate( null, true );
    }

    /**
     * test the <code>isValidReversalDate()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate()
     */
    public void testIsValidReversalDate_Past() {
        testIsValidReversalDate( getPastTimestamp(), false );
    }

    /**
     * test the <code>isValidReversalDate()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate()
     */
    public void testIsValidReversalDate_Future() {
        testIsValidReversalDate( getFutureTimestamp(), true );
    }

    /**
     * test the <code>isValidReversalDate()</code> method of 
     * <code>{@link TransactionalDocumentRuleUtil}</code>
     *
     * @param reversalDate
     * @param expected
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleUtil#isValidReversalDate()
     */
    protected void testIsValidReversalDate( Timestamp reversalDate,
                                            boolean expected ) {
        assertEquals( new Boolean( TransactionalDocumentRuleUtil
                                   .isValidReversalDate( reversalDate, 
                                                         getErrorPropertyName() ) ),
                      new Boolean( expected ) );
        
    }
    ///////////////////////////////////////////////////////////////////////////
    // Test Methods End Here                                                 //
    ///////////////////////////////////////////////////////////////////////////
}
