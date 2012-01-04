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

//RICE20 is there a replacement for JSTLConstants or can this extension be eliminated?
import org.kuali.rice.core.util.JSTLConstants;

public class KcConstants  extends JSTLConstants {
    
    public static final String KC_NAMESPACE_URI = "KC";        
    public static final String MAXIMUM_ACCOUNT_RESPONSIBILITY_ID = "MAXIMUM_ACCOUNT_RESPONSIBILITY_ID";
    public static final String FEDERAL_SPONSOR_TYPE_CODES= "FEDERAL_SPONSOR_TYPE_CODES";
    public static final String ACCOUNT_CREATE_DEFAULT_IDENTIFIER = "accountDefaultId";
    public static final String WEBSERVICE_UNREACHABLE = "Access to the web service is unreachable: ";
    
    public static class AccountCreationDefaults {
        public static final String CHART_OF_ACCOUNT_CODE = "chartOfAccountsCode";
        public static final String ACCOUNT_NUMBER = "accountNumber";
        public static final String KcUnit = "kcUnit";
    }
    public static class BudgetAdjustment {
        public static final String SOAP_SERVICE_NAME = "budgetAdjustmentServiceSOAP";
        public static QName SERVICE = new QName("KFS", SOAP_SERVICE_NAME);
    }
    public static class BudgetCategory {
        public static final String SOAP_SERVICE_NAME = "budgetCategorySoapService";
        public static final String SERVICE_PORT = "budgetCategoryServicePort";
        public static final String SERVICE_NAME = "budgetCategoryService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("budgetCategoryTypeCode","description","budgetCategoryCode");
    }
    public static class Cfda {
        public static final String SOAP_SERVICE_NAME = "cfdaNumberSoapService";
        public static final String SERVICE_PORT = "CfdaNumberServicePort";
        public static final String SERVICE_NAME ="CfdaNumberService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS= Arrays.asList("cfdaNumber", "cfdaMaintenanceTypeId", "cfdaProgramTitleName");
    }
    public static class Unit {
        public static final String SOAP_SERVICE_NAME = "institutionalUnitSoapService";
        public static final String SERVICE_PORT = "institutionalUnitServicePort";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("unitName","unitNumber","parentUnitNumber","organizationId");       
    }
    public static class EffortReporting {
        public static final String SOAP_SERVICE_NAME = "effortReportingServiceSoapService";
        public static final String SERVICE_PORT = "effortReportingServicePort";
        public static final String SERVICE_NAME = "effortReportingService";
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
    }
    public static class AwardAccount {
        public static final String SOAP_SERVICE_NAME = "awardAccountSoapService";
        public static final String SERVICE_PORT = "awardAccountServicePort";
        public static final String SERVICE_NAME ="awardAccountService";
        
        public static QName SERVICE = new QName(KC_NAMESPACE_URI, SOAP_SERVICE_NAME);
        public static final List <String> KC_ALLOWABLE_CRITERIA_PARAMETERS = Arrays.asList("accountNumber", "chartOfAccountsCode");
    }
}
