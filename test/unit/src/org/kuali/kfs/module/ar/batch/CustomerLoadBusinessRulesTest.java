/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.module.ar.batch.vo.CustomerLoadVOGenerator;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class CustomerLoadBusinessRulesTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadBusinessRulesTest.class);

    CustomerLoadService customerLoadService;
    
    public void setUp() {
        customerLoadService = SpringContext.getBean(CustomerLoadService.class);
    }
    
    public void testErrorMessages_BadDocument1() {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        CustomerDigesterVO customerVO = CustomerLoadVOGenerator.generateCustomerVO(CustomerLoadVOGenerator.getValidCustomerVO1(), CustomerLoadVOGenerator.getValidAddressVO1());
        customerVOs.add(customerVO);
        
        boolean result = false;
        List<MaintenanceDocument> customerMaintDocs = new ArrayList<MaintenanceDocument>();
        
        assertTrue("GlobalVariables ErrorMap should be empty.", GlobalVariables.getMessageMap().isEmpty());
        
        result = customerLoadService.validateAndPrepare(customerVOs, customerMaintDocs, true);
        showErrorMap();
        
        assertTrue("The Validation should have produced no error messages.", GlobalVariables.getMessageMap().isEmpty());
        
    }
    
    /**
     * 
     * This method is used during debugging to dump the contents of the error map, including the key names. It is not used by the
     * application in normal circumstances at all.
     * 
     */
    private void showErrorMap() {

        if (GlobalVariables.getMessageMap().isEmpty()) {
            return;
        }

        Set<String> errorMapKeys = GlobalVariables.getMessageMap().keySet();
        ArrayList<ErrorMessage> errorMapEntry;
        for (String errorMapKey : errorMapKeys) {
            errorMapEntry = (ArrayList<ErrorMessage>) GlobalVariables.getMessageMap().get(errorMapKey);
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

