/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.vendor.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.List;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.MaintenanceRuleTestBase;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.fixtures.VendorContractBeginEndDatesFixture;
import org.kuali.module.vendor.fixtures.VendorContractPurchaseOrderLimitAmountPresenceFixture;
import org.kuali.module.vendor.fixtures.VendorRuleAddressStateZipFixture;
import org.kuali.module.vendor.fixtures.VendorRuleAddressTypeFixture;
import org.kuali.module.vendor.fixtures.VendorRuleFaxNumberFixture;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.suite.RelatesTo;

/**
 * This class should contain all tests of methods implementing Vendor rules.  For this
 * purpose, we need to set up the parts of a MaintenanceDocument.
 */
@WithTestSpringContext(session = KHUNTLEY)
public class VendorRuleTest extends MaintenanceRuleTestBase {

    private VendorDetail oldVendor;
    private VendorDetail newVendor;
    private MaintenanceDocument maintDoc;
    private VendorRule rule;
    
    private String taxNumber = "111222333";
    private String taxType = "test";
    private String taxTypeCode = "TC";
    
    private String name = "testName";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();     
        oldVendor = new VendorDetail();
        newVendor = new VendorDetail();
        maintDoc = newMaintDoc( oldVendor, newVendor ); 
        VendorRule rule = (VendorRule)setupMaintDocRule( maintDoc, VendorRule.class );
    }
    
    @Override
    protected void tearDown() throws Exception {
        oldVendor = null;
        newVendor = null;
        maintDoc = null;
        rule = null;
        super.tearDown();
    }
    
    /*
     * TESTS OF ValidateParentVendorTaxNumber
     */
    /*
    public void testValidateParentVendorTaxNumber_UniqueVendor() throws WorkflowException {
        maintDoc = new MaintenanceDocumentBase();
        newVendor.setVendorParentIndicator(true);
        newVendor.setVendorHeaderGeneratedIdentifier( new Integer("111111111") );
        newVendor.getVendorHeader().setVendorTaxTypeCode( taxTypeCode );
        newVendor.getVendorHeader().setVendorTaxNumber( taxNumber );
        VendorMaintainableImpl vmi = new VendorMaintainableImpl();
        vmi.setBusinessObject( newVendor );
        maintDoc.setNewMaintainableObject( vmi );
        
        maintDoc.getDocumentHeader().setDocumentNumber("111111111");
        
        SpringServiceLocator.getDocumentService().prepareWorkflowDocument( maintDoc );        
        SpringServiceLocator.getDocumentService().saveDocument( maintDoc );
        try {
            assertTrue( rule.validateParentVendorTaxNumber( newVendor ) );
        } finally {
            SpringServiceLocator.getDocumentService().cancelDocument( maintDoc, "Test cleanup" );
        }
    }
    */
    
    /*
     * TESTS OF ValidateTaxTypeAndTaxNumberBlankness
     */
    public void testValidateTaxTypeAndTaxNumberBlankness_NullTaxType() {
        newVendor.getVendorHeader().setVendorTaxNumber( taxNumber );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateTaxTypeAndTaxNumberBlankness( newVendor ));
    }

    public void testValidateTaxTypeAndTaxNumberBlankness_EmptyStrTaxType() {
        newVendor.getVendorHeader().setVendorTaxNumber( taxNumber );
        newVendor.getVendorHeader().setVendorTaxTypeCode( "" );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateTaxTypeAndTaxNumberBlankness( newVendor ));
    }
    
    public void testValidateTaxTypeAndTaxNumberBlankness_WithTaxType() {
        newVendor.getVendorHeader().setVendorTaxNumber( taxNumber );
        newVendor.getVendorHeader().setVendorTaxTypeCode( taxTypeCode );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertTrue(rule.validateTaxTypeAndTaxNumberBlankness( newVendor ));
    }
    
    public void testValidateTaxTypeAndTaxNumberBlankness_NullTaxNumber() {
        newVendor.getVendorHeader().setVendorTaxTypeCode( taxTypeCode );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateTaxTypeAndTaxNumberBlankness( newVendor ));
    }
    
    public void testValidateTaxTypeAndTaxNumberBlankness_EmptyStrTaxNumber() {
        newVendor.getVendorHeader().setVendorTaxNumber( "" );
        newVendor.getVendorHeader().setVendorTaxTypeCode( taxTypeCode );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateTaxTypeAndTaxNumberBlankness( newVendor ));
    }
    
    /*
     * TESTS OF ValidateVendorNames
     */
    
    public void testValidateVendorNames_NoNames() {
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_WithVendorName() {
        newVendor.setVendorName( name );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertTrue(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_EmptyStrVendorName() {
        newVendor.setVendorName( "" );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_WithFirstAndLast() {
        newVendor.setVendorFirstName( name );
        newVendor.setVendorLastName( name );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertTrue(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_EmptyStrFirstLast() {
        newVendor.setVendorFirstName( "" );
        newVendor.setVendorLastName( name );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_BothKinds() {
        newVendor.setVendorName( name );
        newVendor.setVendorFirstName( name );
        newVendor.setVendorLastName( name );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateVendorNames( newVendor ));
    }
    
    public void testValidateVendorNames_HalfBoth() {
        newVendor.setVendorName( name );
        newVendor.setVendorFirstName( name );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        assertFalse(rule.validateVendorNames( newVendor ));
    }
    
    /**
     * This method does the setup for the tests which examine the implementation
     * of the requirement that certain vendor type codes require certain address
     * type codes.  Address type codes are in the collection of VendorAddresses.
     *
     * @param typeCode        A VendorTypeCode, which here could be any String
     * @param addrTypeCode1   A VendorAddressTypeCode, which here could be any String
     * @param addrTypeCode2   The VendorAddressTypeCode for the second VendorAddress 
     *                          of the collection
     */
    protected VendorRule validateAddressType_TestHelper( VendorRuleAddressTypeFixture vratf ) {
        newVendor = vratf.populateVendor( newVendor );
        maintDoc = newMaintDoc( oldVendor, newVendor );
        VendorRule rule = (VendorRule)setupMaintDocRule( maintDoc, VendorRule.class );
        rule.refreshSubObjects( newVendor );
        return rule;
    } 
     
    //@RelatesTo(RelatesTo.JiraIssue.KULPURAP609)
    public void testValidateAddressType_WithPOTypeAndPOAddrTypes() {
        rule = validateAddressType_TestHelper( VendorRuleAddressTypeFixture.WITH_PO_TYPE_AND_PO_ADDR_TYPES );
        assertTrue(rule.processAddressValidation( maintDoc ));
    }

    //@RelatesTo(RelatesTo.JiraIssue.KULPURAP609)
    public void testValidateAddressType_WithDVTypeAndRMAddrTypes() {
        rule = validateAddressType_TestHelper( VendorRuleAddressTypeFixture.WITH_DV_TYPE_AND_RM_ADDR_TYPES );
        assertTrue(rule.processAddressValidation( maintDoc ));
    }
    
    //@RelatesTo(RelatesTo.JiraIssue.KULPURAP609)
    public void testValidateAddressType_WithPOTypeAndRMAddrTypes() {
        rule = validateAddressType_TestHelper( VendorRuleAddressTypeFixture.WITH_PO_TYPE_AND_RM_ADDR_TYPES );
        assertFalse(rule.processAddressValidation( maintDoc ));
    }
    
    /*
    public void testValidateAddressType_WithPOTypeAndOnePOAndOneRMAddrTypes() {
        rule  = validateAddressType_TestHelper( VendorRuleAddressTypeFixture.WITH_PO_TYPE_AND_ONE_PO_AND_ONE_RM_ADDR_TYPES );
        assertTrue(rule.processAddressValidation( maintDoc ));
    }
    */
    
    
    /*
     * TESTS OF checkAddressCountryEmptyStateZip 
     */  
    public void testCheckAddressCountryEmptyStateZip_WithUSWithBothStatesWithBothZips() {
        List<VendorAddress> addrList = 
            VendorRuleAddressStateZipFixture.BOTH_US_BOTH_STATES_BOTH_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertTrue(rule.checkAddressCountryEmptyStateZip(address));
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithUSWithoutStatesWithoutZips() {
        List<VendorAddress> addrList =
            VendorRuleAddressStateZipFixture.BOTH_US_WITHOUT_STATES_WITHOUT_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertFalse(rule.checkAddressCountryEmptyStateZip( address));
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithUSWithEmptyStatesEmptyZips() {
        List<VendorAddress> addrList =
            VendorRuleAddressStateZipFixture.BOTH_US_EMPTY_STATES_EMPTY_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertFalse(rule.checkAddressCountryEmptyStateZip( address));
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithUSWithBothStatesWithOneZipOneNull() {
        List<VendorAddress> addrList =
            VendorRuleAddressStateZipFixture.BOTH_US_BOTH_STATES_ONE_ZIP_ONE_NULL.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        for( int i = 0; i < addrList.size(); i++ ) {
            VendorAddress address = addrList.get( i );
            if( i == 0 ) {
                assertTrue(rule.checkAddressCountryEmptyStateZip( address));
            } else if ( i == 1 ) {
                assertFalse(rule.checkAddressCountryEmptyStateZip( address));
            }
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithUSWithBothStatesWithOneZipOneEmpty() {
        List<VendorAddress> addrList = 
            VendorRuleAddressStateZipFixture.BOTH_US_BOTH_STATES_ONE_ZIP_ONE_EMPTY.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        for( int i = 0; i < addrList.size(); i++ ) {
            VendorAddress address = addrList.get( i );
            if( i == 0 ) {
                assertTrue(rule.checkAddressCountryEmptyStateZip( address));
            } else if ( i == 1 ) {
                assertFalse(rule.checkAddressCountryEmptyStateZip( address));
            }
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithoutUSBothStatesWithoutZips() {
        List<VendorAddress> addrList =
            VendorRuleAddressStateZipFixture.WITHOUT_US_BOTH_STATES_WITHOUT_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertTrue(rule.checkAddressCountryEmptyStateZip(address));
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithoutUSBothStatesWithEmptyZips() {
        List<VendorAddress> addrList = 
            VendorRuleAddressStateZipFixture.WITHOUT_US_BOTH_STATES_EMPTY_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertTrue(rule.checkAddressCountryEmptyStateZip(address));
        }
    }
    
    public void testCheckAddressCountryEmptyStateZip_WithoutUSBothStatesWithBothZips() {
        List<VendorAddress> addrList = 
            VendorRuleAddressStateZipFixture.WITHOUT_US_BOTH_STATES_BOTH_ZIPS.populateAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertTrue(rule.checkAddressCountryEmptyStateZip(address));
        }
    }  
    
    /*
     * TESTS OF CheckFaxNumber 
     */
    public void testCheckFaxNumber_WithDefaultFormats() {
        VendorRuleFaxNumberFixture vrfnf = VendorRuleFaxNumberFixture.TWO_DEFAULT_FORMATS;
        List<VendorAddress> addrList = vrfnf.getAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertTrue(rule.checkFaxNumber(address));
        }
    }
    
    public void testCheckFaxNumber_WithShortNumber() {
        VendorRuleFaxNumberFixture vrfnf = VendorRuleFaxNumberFixture.TWO_SHORT_FAXES;
        List<VendorAddress> addrList = vrfnf.getAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        int i = 0;
        for( VendorAddress address: addrList ) {
            i++;
            assertFalse(rule.checkFaxNumber( address));
        }
    }
    
    public void testCheckFaxNumber_WithOneDefaultOneShortFax() {
        VendorRuleFaxNumberFixture vrfnf = VendorRuleFaxNumberFixture.ONE_DEFAULT_ONE_SHORT_FAX;
        List< VendorAddress > addrList = vrfnf.getAddresses();
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        for( int i = 0; i < addrList.size(); i++ ) {
            VendorAddress address = addrList.get( i );
            if( i == 0 ) {
                assertTrue(rule.checkFaxNumber( address));
            } else if( i == 1 ) {
                assertFalse(rule.checkFaxNumber( address));
            }
        }
    }   
    
    /**
     * This method does the setup for the tests which examine the implementation
     * of the requirement that vendor contracts be validated so that their begin
     * date comes before their end date.  Uses two contracts.
     * 
     * @param   vcbedf
     * @return
     */
    protected VendorRule validateVendorContractBeginEndDates_TestHelper( 
            VendorContractBeginEndDatesFixture vcbedf ) {
        newVendor.setVendorContracts( vcbedf.populateContracts() );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        return rule;
    }
    
    public void testValidateVendorContractBeginEndDates_RightOrderRightOrder() {
        VendorRule rule = validateVendorContractBeginEndDates_TestHelper( 
             VendorContractBeginEndDatesFixture.RIGHT_ORDER_RIGHT_ORDER);
        
        List< VendorContract > contractList = newVendor.getVendorContracts();
        for( VendorContract contract: contractList ) {
            assertTrue(rule.validateVendorContractBeginEndDates(contract));
        }
    }
    
    public void testValidateVendorContractBeginEndDates_WrongOrder() {
        VendorRule rule = validateVendorContractBeginEndDates_TestHelper( 
             VendorContractBeginEndDatesFixture.WRONG_ORDER_RIGHT_ORDER);

        List< VendorContract > contractList = newVendor.getVendorContracts();
        // ripierce: the fixture sets up two contracts but we only want to test the 1st (bad) one.
        //   If we test the second (good) one last, the test fails.
        VendorContract contract = contractList.get(0);
        assertFalse(rule.validateVendorContractBeginEndDates(contract));
    }
    
    public void testValidateVendorContractBeginEndDates_RightOrderWrongOrder() {
        VendorRule rule = validateVendorContractBeginEndDates_TestHelper( 
             VendorContractBeginEndDatesFixture.RIGHT_ORDER_WRONG_ORDER);
        
        List< VendorContract > contractList = newVendor.getVendorContracts();
        // ripierce: the fixture sets up two contracts but we only want to test the 2nd bad one.
        VendorContract contract = contractList.get(1);
        assertFalse(rule.validateVendorContractBeginEndDates(contract));
    }
    
  
    /**
     * 
     * This method...
     * @param vcpolapf
     * @return
     */
    private VendorRule validateVendorContractPOLimitAmountPresence_testHelper(
            VendorContractPurchaseOrderLimitAmountPresenceFixture vcpolapf ) {
        List contracts = vcpolapf.populateContracts();
        newVendor.setVendorContracts( contracts );
        VendorRule rule = (VendorRule)setupMaintDocRule(newVendor, VendorRule.class);
        rule.refreshSubObjects( newVendor );
        return rule;
    }
    
    public void testValidateVendorContractPurchaseOrderLimitAmountPresence_NoExcludes() {
        VendorRule rule = validateVendorContractPOLimitAmountPresence_testHelper( 
            VendorContractPurchaseOrderLimitAmountPresenceFixture.NO_EXCLUDES );
        
        List< VendorContract > contractList = newVendor.getVendorContracts();
        for( VendorContract contract: contractList ) {
            assertTrue( rule.validateVendorContractPOLimitAndExcludeFlagCombination(contract) );
        }

    }
    
    public void testValidateVendorContractPurchaseOrderLimitAmountPresence_TwoNExcludesOnFirstTwoLimits() {
        VendorRule rule = validateVendorContractPOLimitAmountPresence_testHelper(
            VendorContractPurchaseOrderLimitAmountPresenceFixture.TWO_N_EXCLUDES_ON_FIRST_TWO_LIMITS);
        
        List< VendorContract > contractList = newVendor.getVendorContracts();
        for( VendorContract contract: contractList ) {
            assertTrue( rule.validateVendorContractPOLimitAndExcludeFlagCombination(contract) );
        }
    }
    
    public void testValidateVendorContractPurchaseOrderLimitAmountPresence_TwoNExcludesOnFirstLimitOnFirst() {
        VendorRule rule = validateVendorContractPOLimitAmountPresence_testHelper(
            VendorContractPurchaseOrderLimitAmountPresenceFixture.TWO_N_EXCLUDES_ON_FIRST_LIMIT_ON_FIRST);
        
        List< VendorContract > contractList = newVendor.getVendorContracts();
        // ripierce: the fixture sets up two contracts but we only want to test the 2nd bad one.
        VendorContract contract = contractList.get(1);
        assertFalse( rule.validateVendorContractPOLimitAndExcludeFlagCombination(contract) );
    }
    
    public void testValidateVendorContractPurchaseOrderLimitAmountPresence_OneYExcludeOnFirstNoLimitOnFirst() {        
        VendorRule rule = validateVendorContractPOLimitAmountPresence_testHelper(
            VendorContractPurchaseOrderLimitAmountPresenceFixture.ONE_Y_EXCLUDE_ON_FIRST_NO_LIMIT_ON_FIRST);

        List< VendorContract > contractList = newVendor.getVendorContracts();
        for( VendorContract contract: contractList ) {
            assertFalse( rule.validateVendorContractPOLimitAndExcludeFlagCombination(contract) );
        }
    }
    
}
