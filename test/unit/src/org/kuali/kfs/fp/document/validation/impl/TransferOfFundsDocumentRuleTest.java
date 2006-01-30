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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class tests the Transfer of Funds Document's persistence, routing, and PE generation.
 *
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class TransferOfFundsDocumentRuleTest
    extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME =
        "TransferOfFundsDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME =
        "KualiTransferOfFundsDocument";

    private static final String[] FIXTURE_COLLECTION_NAMES =
        { COLLECTION_NAME };

    private TransactionalDocumentParameter _docParam1;
    private TransactionalDocumentParameter _docParam2;
    private TransactionalDocumentParameter _documentParameter5;
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _sourceLine4;
    private AccountingLineParameter _sourceLine5;
    private AccountingLineParameter _sourceLine6;
    private AccountingLineParameter _assetSourceLine;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntry;
    private GeneralLedgerPendingEntry _expectedOffSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedOffTargetGlEntry;


    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here                                            //
    ///////////////////////////////////////////////////////////////////////////
    protected final String getDocumentTypeName() {
        return KNOWN_DOCUMENT_TYPENAME;
    }

    public final TargetAccountingLine getAssetTargetLine()
        throws Exception {
        return (TargetAccountingLine) getTargetLineParameter1().createLine();
    }

    protected final TargetAccountingLine getValidObjectSubTypeTargetLine()
        throws Exception {
        return ( TargetAccountingLine )getTargetLineParameter1().createLine();
    }

    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine()
        throws Exception {
        return ( TargetAccountingLine )getTargetLineParameter3().createLine();
    }

    protected final List getValidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add( getSourceLineParameter3().createLine() );
        retval.add( getSourceLineParameter6().createLine() );
        return retval;
    }

    protected final List getInvalidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add( getSourceLineParameter1().createLine() );
        retval.add( getSourceLineParameter2().createLine() );
        return retval;
    }

    protected final List getInvalidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add( getTargetLineParameter1().createLine() );
        retval.add( getTargetLineParameter3().createLine() );
        return retval;
    }

    protected final List getValidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add( getTargetLineParameter2().createLine() );
        retval.add( getTargetLineParameter2().createLine() );
        return retval;
    }

    protected final SourceAccountingLine getValidObjectTypeSourceLine()
        throws Exception {
        return ( SourceAccountingLine )getSourceLineParameter4().createLine();
    }

    protected final SourceAccountingLine getInvalidObjectTypeSourceLine()
        throws Exception {
        return ( SourceAccountingLine )getSourceLineParameter3().createLine();
    }

    protected final SourceAccountingLine getInvalidObjectCodeSourceLine()
        throws Exception {
        return ( SourceAccountingLine )getSourceLineParameter5().createLine();
    }

    protected final SourceAccountingLine getValidObjectCodeSourceLine()
        throws Exception {
        return ( SourceAccountingLine )getSourceLineParameter6().createLine();
    }

    public final SourceAccountingLine getAssetSourceLine() {
        return (SourceAccountingLine) getAssetSourceLineParameter().createLine();
    }

    protected final Document createDocument() throws Exception {
        return getDocumentParameter1().createDocument( getDocumentService() );
    }

    protected final TransactionalDocument createDocument5()
        throws Exception {
        return (TransactionalDocument)getDocumentParameter5()
            .createDocument( getDocumentService() );
    }

    protected final Document createDocumentValidForRouting()
        throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    protected final Document createDocumentInvalidForSave()
        throws Exception {
        return getDocumentParameterNoDescription()
            .createDocument( getDocumentService() );
    }

    protected final TransactionalDocument createDocumentWithInvalidObjectSubType()
        throws Exception {
        TransferOfFundsDocument retval =
            ( TransferOfFundsDocument )createDocument();
        retval.setSourceAccountingLines
            ( getInvalidObjectSubTypeSourceLines() );
        retval.setTargetAccountingLines
            ( getInvalidObjectSubTypeTargetLines() );
        return retval;
    }

    protected final TransactionalDocument createDocumentUnbalanced()
        throws Exception {
        TransferOfFundsDocument retval =
            ( TransferOfFundsDocument )createDocument();
        retval.setSourceAccountingLines
            ( getInvalidObjectSubTypeSourceLines() );
        retval.addTargetAccountingLine( getValidObjectSubTypeTargetLine() );
        return retval;
    }

    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription()
            .createDocument( getDocumentService() );
    }

    protected final TransactionalDocument createDocumentWithValidObjectSubType()
        throws Exception {
        TransferOfFundsDocument retval =
            ( TransferOfFundsDocument )createDocument();
        retval.setSourceAccountingLines( getValidObjectSubTypeSourceLines() );
        retval.setTargetAccountingLines( getValidObjectSubTypeTargetLines() );
        return retval;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter1() {
        return _sourceLine1;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter1( AccountingLineParameter p ) {
        _sourceLine1 = p;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter2() {
        return _sourceLine2;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter2( AccountingLineParameter p ) {
        _sourceLine2 = p;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter3() {
        return _sourceLine3;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter3( AccountingLineParameter p ) {
        _sourceLine3 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter4( AccountingLineParameter p ) {
        _sourceLine4 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter4() {
        return _sourceLine4;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter5() {
        return _sourceLine5;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter5( AccountingLineParameter p ) {
        _sourceLine5 = p;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter6() {
        return _sourceLine6;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     *
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter6( AccountingLineParameter p ) {
        _sourceLine6 = p;
    }

    /**
     * Accessor for fixture 'assetSourceLine'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAssetSourceLineParameter() {
        return _assetSourceLine;
    }

    /**
     * Accessor for fixture 'assetSourceLine'
     *
     * @param p AccountingLineParameter
     */
    public final void setAssetSourceLineParameter( AccountingLineParameter p ) {
        _assetSourceLine = p;
    }

    /**
     * Accessor for fixture 'targetLine1'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter1() {
        return _targetLine1;
    }

    /**
     * Accessor for fixture 'targetLine1'
     *
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter1( AccountingLineParameter p ) {
        _targetLine1 = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter2() {
        return _targetLine2;
    }

    /**
     * Accessor for fixture 'targetLine2'
     *
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter2( AccountingLineParameter p ) {
        _targetLine2 = p;
    }

    /**
     * Accessor for fixture 'targetLine3'
     *
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter3() {
        return _targetLine3;
    }

    /**
     * Accessor for fixture 'targetLine3'
     *
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter3( AccountingLineParameter p ) {
        _targetLine3 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameter1() {
        return _docParam1;
    }

    public final void setDocumentParameter1( TransactionalDocumentParameter p ) {
        _docParam1 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameterNoDescription() {
        return _docParam2;
    }

    public final void setDocumentParameterNoDescription( TransactionalDocumentParameter p ) {
        _docParam2 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameter5() {
        return _documentParameter5;
    }

    public final void setDocumentParameter5(TransactionalDocumentParameter p) {
        _documentParameter5 = p;
    }

    /**
     * Accessor method for Explicit Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return _expectedExpSourceGlEntry;
    }

    /**
     * Accessor method for Explicit Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedExplicitSourcePendingEntry( GeneralLedgerPendingEntry e ) {
        _expectedExpSourceGlEntry = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return _expectedExpTargetGlEntry;
    }

    /**
     * Accessor method for Explicit Target fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedExplicitTargetPendingEntry( GeneralLedgerPendingEntry e ) {
        _expectedExpTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Target fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return _expectedOffTargetGlEntry;
    }

    /**
     * Accessor method for Offset Target fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedOffsetTargetPendingEntry( GeneralLedgerPendingEntry e ) {
        LOG().info( "Setting expectedOffsetTargetGlEntry: " + e );
        _expectedOffTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @param GeneralLedgerPendingEntry pending entry fixture
     */
    public final void setExpectedOffsetSourcePendingEntry( GeneralLedgerPendingEntry e ) {
        _expectedOffSourceGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for
     * <code>{@link testProcessGeneralLedgerPendingEntries}</code> test
     * methods.
     *
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return _expectedOffSourceGlEntry;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here                                              //
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here                                               //
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // Test Methods End Here                                                 //
    ///////////////////////////////////////////////////////////////////////////
}
