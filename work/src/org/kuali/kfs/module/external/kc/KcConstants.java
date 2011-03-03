/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc;

import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.kuali.rice.core.util.JSTLConstants;

public class KcConstants  extends JSTLConstants {
    
    public static final String KC_NAMESPACE_URI = "KC";        
        
    public static class BudgetCategory {
        public static final String KC_SERVICE_NAME = "budgetCategorySoapService";
        public static final QName QUALIFIED_SERVICE_NAME = new QName(KC_NAMESPACE_URI, KC_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("budgetCategoryTypeCode","description","budgetCategoryCode");
    }
    
    public static class Unit {
        public static final String KC_SERVICE_NAME = "institutionalUnitSoapService";
        public static final QName QUALIFIED_SERVICE_NAME = new QName(KC_NAMESPACE_URI, KC_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("unitName","unitNumber","parentUnitNumber","organizationId");
        
    }
}
