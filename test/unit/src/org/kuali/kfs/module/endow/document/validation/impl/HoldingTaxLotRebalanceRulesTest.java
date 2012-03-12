/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.endow.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.module.endow.businessobject.KEMID;
import org.kuali.kfs.module.endow.businessobject.RegistrationCode;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;
import org.kuali.kfs.module.endow.fixture.ClassCodeFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentMaintenanceDocumentFixture;
import org.kuali.kfs.module.endow.fixture.EndowmentTransactionCodeFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotFixture;
import org.kuali.kfs.module.endow.fixture.HoldingTaxLotRebalanceFixture;
import org.kuali.kfs.module.endow.fixture.KemIdFixture;
import org.kuali.kfs.module.endow.fixture.RegistrationCodeFixture;
import org.kuali.kfs.module.endow.fixture.SecurityFixture;
import org.kuali.kfs.module.endow.fixture.SecurityReportingGroupFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.KualiMaintainableImpl;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class tests the rules in HoldingTaxLotRebalanceRules
 */
@ConfigureContext(session = khuntley)
public class HoldingTaxLotRebalanceRulesTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(HoldingTaxLotRebalanceRulesTest.class);

    private HoldingTaxLotRebalanceRule rule;
    private MaintenanceDocument holdingTaxLotRebalanceMaintenceDocument;

    private HoldingTaxLotRebalance normalHoldingTaxLotRebalance;
    private HoldingTaxLotRebalance zeroHoldingTaxLotRebalance;
    private HoldingTaxLotRebalance negativeHoldingTaxLotRebalance;
    
    private HoldingTaxLotRebalance zeroUnitOnlyHoldingTaxLotRebalance;
    private HoldingTaxLotRebalance zeroCostOnlyHoldingTaxLotRebalance; 
    
    private RegistrationCode registrationCode;
    private SecurityReportingGroup reportingGroup;
    private ClassCode classCode;
    private Security security;
    private KEMID kemid;
    private EndowmentTransactionCode endowmentTransactionCode;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new HoldingTaxLotRebalanceRule();
        holdingTaxLotRebalanceMaintenceDocument = createHoldingTaxLotRebalanceDocument();
        
        registrationCode = RegistrationCodeFixture.REGISTRATION_CODE_RECORD_FOR_LIABILITY.createRegistrationCode();
        reportingGroup = SecurityReportingGroupFixture.REPORTING_GROUP.createSecurityReportingGroup();
        endowmentTransactionCode = EndowmentTransactionCodeFixture.INCOME_TRANSACTION_CODE.createEndowmentTransactionCode();
        classCode = ClassCodeFixture.TEST_CLASS_CODE.createClassCodeRecord();
        security = SecurityFixture.ACTIVE_SECURITY.createSecurityRecord();
        kemid = KemIdFixture.ALLOW_TRAN_KEMID_RECORD.createKemidRecord();
                
        //instantiate tax lot rebalance records so it can be verified by the rules class..
        normalHoldingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD.createHoldingTaxLotRebalanceRecord();
        normalHoldingTaxLotRebalance.getHoldingTaxLots().add(HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD.createHoldingTaxLotRecord());
        updateHoldingTaxLotBalanceTotals(normalHoldingTaxLotRebalance);
        
        negativeHoldingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_4.createHoldingTaxLotRebalanceRecord();
        negativeHoldingTaxLotRebalance.getHoldingTaxLots().add(HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_ALL_NEGATIVE.createHoldingTaxLotRecord());
        updateHoldingTaxLotBalanceTotals(negativeHoldingTaxLotRebalance);
        
        zeroHoldingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_3.createHoldingTaxLotRebalanceRecord();
        zeroHoldingTaxLotRebalance.getHoldingTaxLots().add(HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_ALL_ZERO.createHoldingTaxLotRecord());
        updateHoldingTaxLotBalanceTotals(zeroHoldingTaxLotRebalance);
        
        zeroUnitOnlyHoldingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_ZERO_UNIT.createHoldingTaxLotRebalanceRecord();
        zeroUnitOnlyHoldingTaxLotRebalance.getHoldingTaxLots().add(HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_ZERO_UNIT.createHoldingTaxLotRecord());
        updateHoldingTaxLotBalanceTotals(zeroUnitOnlyHoldingTaxLotRebalance);
        
        zeroCostOnlyHoldingTaxLotRebalance = HoldingTaxLotRebalanceFixture.HOLDING_TAX_LOT_REBALANCE_RECORD_ZERO_COST.createHoldingTaxLotRebalanceRecord();
        zeroCostOnlyHoldingTaxLotRebalance.getHoldingTaxLots().add(HoldingTaxLotFixture.HOLDING_TAX_LOT_RECORD_ZERO_COST.createHoldingTaxLotRecord());
        updateHoldingTaxLotBalanceTotals(zeroCostOnlyHoldingTaxLotRebalance);
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        holdingTaxLotRebalanceMaintenceDocument = null;
        super.tearDown();
    }

    /**
     * helper method to update the actual total units and costs in the balance record 
     * 
     */
    private void updateHoldingTaxLotBalanceTotals(HoldingTaxLotRebalance holdingTaxLotBalance) {
        
        BigDecimal totalCost = BigDecimal.ZERO;
        BigDecimal totalUnit = BigDecimal.ZERO;
        for (HoldingTaxLot taxLot : holdingTaxLotBalance.getHoldingTaxLots()) {
            totalCost = totalCost.add(taxLot.getCost());
            totalUnit = totalUnit.add(taxLot.getUnits());
        }
        
        //update the balance
        holdingTaxLotBalance.setTotalCost(totalCost);
        holdingTaxLotBalance.setTotalUnits(totalUnit);
        holdingTaxLotBalance.setTotalLotNumber(new KualiInteger(String.valueOf(holdingTaxLotBalance.getHoldingTaxLots().size())));
    }
    
    /**
     * create a blank HoldingTaxLotRebalance document 
     * 
     * @return doc
     */
    private MaintenanceDocument createHoldingTaxLotRebalanceDocument() {
        MaintenanceDocument document = EndowmentMaintenanceDocumentFixture.ENDOWMENT_MAINTENANCE_DOCUMENT_REQUIRED_FIELDS_RECORD.createEndowmentMaintenanceDocument();
        document.getDocumentHeader().setDocumentDescription("Testing Holding Tax Lot Rebalance.");

        //set the default old and new maintainable objects to be identical
        document.setOldMaintainableObject(new KualiMaintainableImpl(normalHoldingTaxLotRebalance));
        document.setNewMaintainableObject(new KualiMaintainableImpl(normalHoldingTaxLotRebalance));
        return document;
    }
    
    /**
     * update the maintainables in the document 
     */
    protected void updateMaintainables(MaintenanceDocument document, HoldingTaxLotRebalance oldBo, HoldingTaxLotRebalance newBo) {
        document.setOldMaintainableObject(new KualiMaintainableImpl(oldBo));
        document.setNewMaintainableObject(new KualiMaintainableImpl(newBo));
    }

    /**
     * test to check validateUnitValue() method in the rule class
     */
    public void testValidateUnitValue() {
        LOG.info("testValidateUnitValue() entered.");
        
        //fail when any tax lot unit is negative
        assertFalse(rule.validateUnitValue(negativeHoldingTaxLotRebalance));
        
        assertTrue(rule.validateUnitValue(normalHoldingTaxLotRebalance));
        assertTrue(rule.validateUnitValue(zeroHoldingTaxLotRebalance));
    }
    
    /**
     * test to check validateCostValue() method in the rule class
     */
    public void testValuealidateCostValue() {
        LOG.info("testValidateCostValue() entered.");
        
        //fail when any tax lot cost is negative
        assertFalse(rule.validateCostValue(negativeHoldingTaxLotRebalance));
        
        assertTrue(rule.validateCostValue(normalHoldingTaxLotRebalance));
        assertTrue(rule.validateCostValue(zeroHoldingTaxLotRebalance));
    }
    
    /**
     * test to check validateAllZero() method in the rule class
     */
    public void testValidateAllZero() {
        LOG.info("testValidateAllZero() entered.");
        
        //test negatives and normal cases
        assertTrue(rule.validateAllZero(negativeHoldingTaxLotRebalance));
        assertTrue(rule.validateAllZero(normalHoldingTaxLotRebalance));
        
        //both unit and costs are zeros
        assertTrue(rule.validateAllZero(zeroHoldingTaxLotRebalance));
        
        //only unit is zero, cost is non-zero
        assertFalse(rule.validateAllZero(zeroUnitOnlyHoldingTaxLotRebalance));
        //only cost is zero, unit is non-zero
        assertFalse(rule.validateAllZero(zeroCostOnlyHoldingTaxLotRebalance));
    }
    
    /**
     * test to check validateTotalUnits() method in the rule class
     */
    public void testValidateTotalUnit() {
        LOG.info("testValidateTotalUnit() entered.");
        
        //fail when old/new rebalance objects have mismatch units
        assertFalse(rule.validateTotalUnits(normalHoldingTaxLotRebalance, zeroHoldingTaxLotRebalance));
        assertFalse(rule.validateTotalUnits(negativeHoldingTaxLotRebalance, normalHoldingTaxLotRebalance));
        
        assertTrue(rule.validateTotalUnits(normalHoldingTaxLotRebalance, normalHoldingTaxLotRebalance));
        assertTrue(rule.validateTotalUnits(negativeHoldingTaxLotRebalance, negativeHoldingTaxLotRebalance));
        assertTrue(rule.validateTotalUnits(zeroHoldingTaxLotRebalance, zeroHoldingTaxLotRebalance));
    }

    /**
     * test to check validateTotalCost() method in the rule class
     */
    public void testValidateTotalCost() {
        LOG.info("testValidateTotalCost() entered.");
        
        //fail when old/new rebalance objects have mismatch cost
        assertTrue(rule.validateTotalCost(normalHoldingTaxLotRebalance, zeroHoldingTaxLotRebalance));
        assertTrue(rule.validateTotalCost(negativeHoldingTaxLotRebalance, normalHoldingTaxLotRebalance));
        
        assertTrue(rule.validateTotalCost(normalHoldingTaxLotRebalance, normalHoldingTaxLotRebalance));
        assertTrue(rule.validateTotalCost(negativeHoldingTaxLotRebalance, negativeHoldingTaxLotRebalance));
        assertTrue(rule.validateTotalCost(zeroHoldingTaxLotRebalance, zeroHoldingTaxLotRebalance));
    }
    
    
    /**
     * test to validate processCustomRouteDocumentBusinessRules() method in the rule class.
     */
    public void testProcessCustomRouteDocumentBusinessRules() {
        LOG.info("testProcessCustomRouteDocumentBusinessRules() entered.");
        
        //default case, identical maintainables
        holdingTaxLotRebalanceMaintenceDocument = createHoldingTaxLotRebalanceDocument();
        assertTrue(rule.processCustomRouteDocumentBusinessRules(holdingTaxLotRebalanceMaintenceDocument));
        
        //clear any error messages before the preceding assertion...
        GlobalVariables.getMessageMap().clearErrorMessages();

        //negative unit and cost 
        updateMaintainables(holdingTaxLotRebalanceMaintenceDocument, normalHoldingTaxLotRebalance, negativeHoldingTaxLotRebalance);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(holdingTaxLotRebalanceMaintenceDocument));
        
        //mix unit and cost business object
        GlobalVariables.getMessageMap().clearErrorMessages();
        updateMaintainables(holdingTaxLotRebalanceMaintenceDocument, normalHoldingTaxLotRebalance, zeroCostOnlyHoldingTaxLotRebalance);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(holdingTaxLotRebalanceMaintenceDocument));
        GlobalVariables.getMessageMap().clearErrorMessages();
        updateMaintainables(holdingTaxLotRebalanceMaintenceDocument, normalHoldingTaxLotRebalance, zeroUnitOnlyHoldingTaxLotRebalance);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(holdingTaxLotRebalanceMaintenceDocument));
        
        //unmatch maintables on unit/cost 
        GlobalVariables.getMessageMap().clearErrorMessages();
        updateMaintainables(holdingTaxLotRebalanceMaintenceDocument, zeroCostOnlyHoldingTaxLotRebalance, normalHoldingTaxLotRebalance);
        assertFalse(rule.processCustomRouteDocumentBusinessRules(holdingTaxLotRebalanceMaintenceDocument));
    }
        
}
