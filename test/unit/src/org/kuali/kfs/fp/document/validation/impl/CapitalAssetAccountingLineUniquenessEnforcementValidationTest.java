/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import java.util.Collection;

import org.kuali.kfs.fp.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.validation.event.AttributedRouteDocumentEvent;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext
public class CapitalAssetAccountingLineUniquenessEnforcementValidationTest extends KualiTestBase {

    CapitalAssetAccountingLineUniquenessEnforcementValidation validator;
    AttributedRouteDocumentEvent event;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validator = new CapitalAssetAccountingLineUniquenessEnforcementValidation();
        DistributionOfIncomeAndExpenseDocument doc = new DistributionOfIncomeAndExpenseDocument();
        validator.setAccountingDocumentForValidation(doc);
        //SourceAccountingLine sourceLine = AccountingLineFixture.LINE17.createSourceAccountingLine();

        event = new AttributedRouteDocumentEvent(doc);
    }

    protected SourceAccountingLine addSourceLine( AccountingLineFixture fixture ) throws Exception {
        SourceAccountingLine sourceAccountingLine = fixture.createSourceAccountingLine();
        validator.getAccountingDocumentForValidation().addSourceAccountingLine( sourceAccountingLine );
        return sourceAccountingLine;
    }

    public void testValidate_NO_LINES() {
        assertTrue( "When no accounting lines, should have returned true", validator.validate(event) );
    }

    public void testValidate_NO_CAP_LINES() throws Exception {
        validator.getAccountingDocumentForValidation().addSourceAccountingLine(AccountingLineFixture.LINE.createSourceAccountingLine() );
        assertTrue( "When no capital asset accounting lines, should have returned true", validator.validate(event) );
    }

    public void testGetCapitalAssetAccountingLines() throws Exception {
        addSourceLine(AccountingLineFixture.LINE);
        SourceAccountingLine capLine = addSourceLine(AccountingLineFixture.LINE17);
        Collection<AccountingLine> capAssetLines = validator.getCapitalAssetAccountingLines(validator.getAccountingDocumentForValidation().getSourceAccountingLines());
        assertEquals( "One of the lines should have been returned: " + capAssetLines, 1, capAssetLines.size() );
        assertSame( "Wrong line was returned" + capAssetLines, capLine, capAssetLines.iterator().next() );
    }

    public void testValidateLineUniqueness_TWO_IDENTICAL_LINES() throws Exception {
        addSourceLine(AccountingLineFixture.LINE17);
        addSourceLine(AccountingLineFixture.LINE17);
        assertFalse( "Rule should have failed - lines were not unique: " + validator.getAccountingDocumentForValidation().getSourceAccountingLines(),
                validator.validateLineUniqueness(validator.getAccountingDocumentForValidation().getSourceAccountingLines()) );
        assertTrue( "Message map should have had an appropriate error: " + dumpMessageMapErrors(), GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_NONUNIQUE_CAPITAL_ASSET_ACCOUNTING_LINE) );
    }

    public void testValidateLineUniqueness_NO_IDENTICAL_LINES() throws Exception {
        // need to alter the line so that it's a cap asset line
        addSourceLine(AccountingLineFixture.LINE).setFinancialObjectCode("7600");
        addSourceLine(AccountingLineFixture.LINE17);
        // double check - if line filtered out it would pass the test below as well
        System.err.println(validator.getAccountingDocumentForValidation().getSourceAccountingLines());
        Collection<AccountingLine> capAssetLines = validator.getCapitalAssetAccountingLines(validator.getAccountingDocumentForValidation().getSourceAccountingLines());
        assertEquals( "Both lines should have been returned: " + validator.getAccountingDocumentForValidation().getSourceAccountingLines(),
                2, capAssetLines.size() );
        assertTrue( "Rule should not have failed - lines were unique: " + validator.getAccountingDocumentForValidation().getSourceAccountingLines(),
                validator.validateLineUniqueness(validator.getAccountingDocumentForValidation().getSourceAccountingLines()) );
    }

    public void testGetLineUniquenessKey_EQUALS() throws Exception {
        SourceAccountingLine sourceLine = AccountingLineFixture.LINE17.createSourceAccountingLine();
        TargetAccountingLine targetLine = AccountingLineFixture.LINE17.createTargetAccountingLine();
        assertEquals( "Lines created from same fixture should have been equal", validator.getLineUniquenessKey(sourceLine), validator.getLineUniquenessKey(targetLine) );
    }

    public void testGetLineUniquenessKey_NOT_EQUALS() throws Exception  {
        SourceAccountingLine sourceLine = AccountingLineFixture.LINE17.createSourceAccountingLine();
        TargetAccountingLine targetLine = AccountingLineFixture.LINE.createTargetAccountingLine();
        assertFalse( "Lines created from different fixtures should not have been equal", validator.getLineUniquenessKey(sourceLine).equals(validator.getLineUniquenessKey(targetLine)) );
    }

}
