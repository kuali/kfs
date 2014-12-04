/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionSecurity;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.module.endow.document.LiabilityIncreaseDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;

/**
 * This class...
 */
@ConfigureContext(session = khuntley)
public class LiabilityServiceTest extends KualiTestBase {
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;

    private boolean runTests() { // change this to return false to prevent running tests
        return false;
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        sequenceAccessorService = SpringContext.getBean(SequenceAccessorService.class);
    }

    @ConfigureContext(shouldCommitTransactions = false)
    public void testOJBConfiguration() throws Exception {

        if (!runTests())
            return;

        // Insert Transaction Document
        LiabilityIncreaseDocument tx = new LiabilityIncreaseDocument();
        tx.setDocumentNumber("4160");
        tx.setTransactionSubTypeCode("C");
        tx.setTransactionSourceTypeCode("M");

        // Saving a Trans Line
        EndowmentTransactionLine tranLine = new EndowmentSourceTransactionLine();
        tranLine.setDocumentNumber("4160");
        tranLine.setTransactionLineNumber(new Integer("1"));
        tranLine.setKemid("099PLTF013");
        tranLine.setEtranCode("00100");
        tranLine.setTransactionIPIndicatorCode("I");
        tranLine.setTransactionAmount(new KualiDecimal(2.3));

        // Setting the transaction line.
        List tranList = new ArrayList();
        tranList.add(tranLine);
        tx.setSourceTransactionLines(tranList);

        // Saving a Holding line for a Trans Line
        EndowmentTransactionTaxLotLine hldg = new EndowmentTransactionTaxLotLine();
        hldg.setDocumentNumber("4160");
        hldg.setDocumentLineNumber(1);
        hldg.setDocumentLineTypeCode("F");
        hldg.setTransactionHoldingLotNumber(99);
        hldg.setLotUnits(new BigDecimal(22));

        // Setting the tax lot line.
        List taxList = new ArrayList();
        taxList.add(hldg);
        tranLine.setTaxLotLines(taxList);
        // businessObjectService.save(hldg);

        // Creating Tran Security
        EndowmentTransactionSecurity tranSec = new EndowmentSourceTransactionSecurity();
        tranSec.setDocumentNumber("4160");
        tranSec.setSecurityID("9128273E0");
        tranSec.setRegistrationCode("01P");

        List secList = new ArrayList();
        secList.add(tranSec);

        tx.setSourceTransactionSecurity(tranSec);

        // saving Part of a Liability Transaction Document
        businessObjectService.save(tx);

        // Retrirve Tx Doc
        LiabilityIncreaseDocument newTx = businessObjectService.findBySinglePrimaryKey(LiabilityIncreaseDocument.class, "4160");

        assertEquals("S0urce Lines", 1, newTx.getSourceTransactionLines().size());
        assertEquals("Target Lines", 0, newTx.getTargetTransactionLines().size());
        assertNotNull("Source Security", newTx.getSourceTransactionSecurity());

        /*
         * EndowmentSourceTransactionSecurity sec = new EndowmentSourceTransactionSecurity(); sec.setDocumentNumber("4160");
         * sec.setSecurityID("004764106"); sec.setRegistrationCode("01P"); //tx.setSourceTransactionSecurity(sec); //saving Part of
         * a Liability Transaction Document - Security //businessObjectService.save(sec);
         */


    }
};
