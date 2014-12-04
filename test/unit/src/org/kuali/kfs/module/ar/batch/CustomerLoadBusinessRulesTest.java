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
package org.kuali.kfs.module.ar.batch;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.batch.vo.CustomerLoadVOGenerator;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.util.AutoPopulatingList;

@ConfigureContext(session = khuntley)
public class CustomerLoadBusinessRulesTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadBusinessRulesTest.class);

    CustomerLoadService customerLoadService;
    
    public void setUp() {
        customerLoadService = SpringContext.getBean(CustomerLoadService.class);
    }
    
    public void testNothing() {
        
    }
    
    public void NORUN_testErrorMessages_BadDocument1() {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        CustomerDigesterVO customerVO = CustomerLoadVOGenerator.generateCustomerVO(CustomerLoadVOGenerator.getValidCustomerVO1(), CustomerLoadVOGenerator.getValidAddressVO1());
        customerVOs.add(customerVO);
        
        boolean result = false;
        List<MaintenanceDocument> customerMaintDocs = new ArrayList<MaintenanceDocument>();
        
        assertTrue("GlobalVariables MessageMap should be empty.", GlobalVariables.getMessageMap().hasErrors());
        
        result = customerLoadService.validateAndPrepare(customerVOs, customerMaintDocs, true);
        showMessageMap();
        
        assertTrue("The Validation should have produced no error messages.", GlobalVariables.getMessageMap().hasErrors());
        
    }
    
    /**
     * 
     * This method is used during debugging to dump the contents of the error map, including the key names. It is not used by the
     * application in normal circumstances at all.
     * 
     */
    private void showMessageMap() {

        if (GlobalVariables.getMessageMap().hasErrors()) {
            return;
        }

        Set<String> errorMapKeys = ((Map<String, String>) GlobalVariables.getMessageMap()).keySet();
        AutoPopulatingList<ErrorMessage> errorMapEntry;
        for (String errorMapKey : errorMapKeys) {
            errorMapEntry = (AutoPopulatingList<ErrorMessage>) (GlobalVariables.getMessageMap()).getMessages(errorMapKey);
            for (ErrorMessage errorMessage : errorMapEntry) {
                
                if (errorMessage.getMessageParameters() == null) {
                    LOG.error("[" + errorMapKey + "] " + errorMessage.getErrorKey());
                }
                else {
                    LOG.error("[" + errorMapKey + "] " + errorMessage.getErrorKey() + " == " + parseStringArray(errorMessage.getMessageParameters()));
                }
            }
        }
    }

    private String parseStringArray(String[] stringArray) {
        StringBuffer sb = new StringBuffer();
        String comma = "";
        for (int i = 0; i < stringArray.length; i++) {
            sb.append(comma + stringArray[i]);
            if ("".equals(comma)) comma = ","; 
        }
        return sb.toString();
    }
}

