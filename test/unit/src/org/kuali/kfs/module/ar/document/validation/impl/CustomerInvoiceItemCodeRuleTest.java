package org.kuali.module.ar.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.math.BigDecimal;

import org.kuali.core.maintenance.MaintenanceRuleTestBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.ar.bo.CustomerInvoiceItemCode;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class CustomerInvoiceItemCodeRuleTest extends MaintenanceRuleTestBase {

    CustomerInvoiceItemCode customerInvoiceItemCode;
    
    private static String CHART_CODE = "BA";
    private static String ORGANIZATION_CODE = "ACAC";
    private static String INVOICE_ITEM_CODE = "TRE";
    private static String INVOICE_ITEM_DESCRIPTION = "TEST";
    private static String POSITIVE_VALUE = "1";
    private static String NON_POSITIVE_VALUE = "0";
    private static boolean ACTIVE_INDEX = true;
   
    @Override
    protected void setUp() throws Exception {
    
        super.setUp();
        customerInvoiceItemCode = new CustomerInvoiceItemCode();
        customerInvoiceItemCode.setChartOfAccountsCode(CHART_CODE);
        customerInvoiceItemCode.setOrganizationCode(ORGANIZATION_CODE);
        customerInvoiceItemCode.setInvoiceItemCode(INVOICE_ITEM_CODE);
        customerInvoiceItemCode.setInvoiceItemDescription(INVOICE_ITEM_DESCRIPTION);
        customerInvoiceItemCode.setActive(ACTIVE_INDEX);
    }
    
    /**
     * This method tests if the validateItemDefaultPrice rule returns true when itemDefaultPrice is positive.
     */
    public void testValidateItemDefaultPrice_True(){
        customerInvoiceItemCode.setItemDefaultPrice(new KualiDecimal(POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultPrice(customerInvoiceItemCode);
        assertEquals( "When item default price is " + POSITIVE_VALUE + ", validateItemDefaultPrice should return true. ", true, result );
    }
    
    /**
     * This method tests if the validateItemDefaultPrice rule returns false when itemDefaultPrice is not positive.
     */
    public void testValidateItemDefaultPrice_False(){  
        customerInvoiceItemCode.setItemDefaultPrice(new KualiDecimal(NON_POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultPrice(customerInvoiceItemCode);
        assertEquals( "When item default price is " + NON_POSITIVE_VALUE + ", validateItemDefaultPrice should return false. ", false, result );
        
    }
    
    /**
     * This method tests if the validItemDefaultQuantity rule returns true when itemDefaultQuantity is positive
     */

    public void testValidItemDefaultQuantity_True(){
        customerInvoiceItemCode.setItemDefaultQuantity(new BigDecimal(POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultQuantity(customerInvoiceItemCode);
        assertEquals( "When item default quantity is " + POSITIVE_VALUE + ", validItemDefaultQuantity should return true. ", true, result );        
    }

    /**
     * This method tests if the validItemDefaultQuantity rule returns false when itemDefaultQuantity is not positive
     */

    public void testValidItemDefaultQuantity_False(){
        customerInvoiceItemCode.setItemDefaultQuantity(new BigDecimal(NON_POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultQuantity(customerInvoiceItemCode);
        assertEquals( "When item default quantity is " + NON_POSITIVE_VALUE + ", validateItemDefaultQuantity should return false. ", false, result );  
        
    }

}

