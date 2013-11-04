/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec;

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectConsolidation;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.sec.businessobject.SecurityAttributeMetadata;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants;


/**
 * General Constants for Access Security Module
 */
public class SecConstants {

    public final static String ACCESS_SECURITY_NAMESPACE_CODE = KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY;
    public final static String LABOR_MODULE_NAMESPACE_CODE = KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION;
    
    public final static String ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME = "module.access.security.enabled";
    
    public final static String ACCOUNTING_GROUP_ERROR_KEY_PREFIX = "SecurityGroupErrors:";
    public final static String ALL_DOCUMENT_TYPE_NAME = KFSConstants.ROOT_DOCUMENT_TYPE;
    public final static String ALL_PARAMETER_DETAIL_COMPONENT = ParameterConstants.ALL_COMPONENT;
    public final static String SECURITY_DEFINITION_ID_SEQUENCE_NAME = "SEC_SCRTY_DEFN_ID_SEQ";
    public final static String SECURITY_MODEL_ID_SEQUENCE_NAME = "SEC_SCRTY_MDL_ID_SEQ";
    public final static String ACCESS_DENIED_ERROR_FORWARD = "accessDeniedError";
    public final static String OPEN_DOCUMENT_SECURITY_ACCESS_DENIED_ERROR_KEY = "openDocumentSecurityAccessDeniedError";
    public final static String ACCESS_ERROR_STRING_REQUEST_KEY = "securityAccessErrorMessage";

    public final static Map<String, SecurityAttributeMetadata> SECURITY_ATTRIBUTE_METADATA_MAP = new HashMap<String, SecurityAttributeMetadata>();
    static {
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.ACCOUNT, new SecurityAttributeMetadata(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.CHART, new SecurityAttributeMetadata(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.CHART_DESCEND_HIERARCHY, new SecurityAttributeMetadata(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.OBJECT_CONSOLIDATION, new SecurityAttributeMetadata(ObjectConsolidation.class, KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.OBJECT_LEVEL, new SecurityAttributeMetadata(ObjectLevel.class, KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.ORGANIZATION, new SecurityAttributeMetadata(Organization.class, KFSPropertyConstants.ORGANIZATION_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.ORGANIZATION_DESCEND_HIERARCHY, new SecurityAttributeMetadata(Organization.class, KFSPropertyConstants.ORGANIZATION_CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.PRINCIPAL_INVESTIGATOR, new SecurityAttributeMetadata(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.PROJECT_CODE, new SecurityAttributeMetadata(ProjectCode.class, KFSPropertyConstants.CODE));
        SECURITY_ATTRIBUTE_METADATA_MAP.put(SecurityAttributeNames.SUB_ACCOUNT, new SecurityAttributeMetadata(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER));
    }

    public final static Map<String, SecurityAttributeMetadata> ATTRIBUTE_SIMULATION_MAP = new HashMap<String, SecurityAttributeMetadata>();
    static {
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.ACCOUNT, new SecurityAttributeMetadata(Account.class, KFSPropertyConstants.ACCOUNT_NUMBER, KFSPropertyConstants.ACCOUNT_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.CHART, new SecurityAttributeMetadata(Chart.class, KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, KFSPropertyConstants.FIN_CHART_OF_ACCOUNT_DESCRIPTION));
        ATTRIBUTE_SIMULATION_MAP.put(NonSecurityAttributeNames.OBJECT_CODE, new SecurityAttributeMetadata(ObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_CODE_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.OBJECT_CONSOLIDATION, new SecurityAttributeMetadata(ObjectConsolidation.class, KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.OBJECT_LEVEL, new SecurityAttributeMetadata(ObjectLevel.class, KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.ORGANIZATION, new SecurityAttributeMetadata(Organization.class, KFSPropertyConstants.ORGANIZATION_CODE, KFSPropertyConstants.ORGANIZATION_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.PROJECT_CODE, new SecurityAttributeMetadata(ProjectCode.class, KFSPropertyConstants.PROJECT_CODE, KFSPropertyConstants.PROJECT_DESCRIPTION));
        ATTRIBUTE_SIMULATION_MAP.put(SecurityAttributeNames.SUB_ACCOUNT, new SecurityAttributeMetadata(SubAccount.class, KFSPropertyConstants.SUB_ACCOUNT_NUMBER, KFSPropertyConstants.SUB_ACCOUNT_NAME));
        ATTRIBUTE_SIMULATION_MAP.put(NonSecurityAttributeNames.SUB_OBJECT_CODE, new SecurityAttributeMetadata(SubObjectCode.class, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE_NAME));
    }

    public class SecurityConstraintCodes {
        public static final String ALLOWED = "A";
        public static final String DENIED = "D";
    }

    public class SecurityDefinitionOperatorCodes {
        public static final String EQUAL = "=";
        public static final String NOT_EQUAL = "<>";
        public static final String LESS_THAN = "<";
        public static final String LESS_THAN_EQUAL = "<=";
        public static final String GREATER_THAN = ">";
        public static final String GREATER_THAN_EQUAL = ">=";
    }

    public class SecurityAttributeNames {
        public static final String ACCOUNT = "Account";
        public static final String CHART = "Chart";
        public static final String CHART_DESCEND_HIERARCHY = "Chart-Descend Hierarchy";
        public static final String OBJECT_CONSOLIDATION = "Object Consolidation";
        public static final String OBJECT_LEVEL = "Object Level";
        public static final String ORGANIZATION = "Organization";
        public static final String ORGANIZATION_DESCEND_HIERARCHY = "Organization-Descend Hierarchy";
        public static final String PRINCIPAL_INVESTIGATOR = "Principal Investigator";
        public static final String PROJECT_CODE = "Project Code";
        public static final String SUB_ACCOUNT = "Sub Account";
    }

    public class NonSecurityAttributeNames {
        public static final String OBJECT_CODE = "Object Code";
        public static final String SUB_OBJECT_CODE = "Sub-Object Code";
    }

    public class SecurityParameterNames {
        public static final String ACCESS_SECURITY_DOCUMENT_TYPES = "ACCESS_SECURITY_DOCUMENT_TYPES";
        public static final String ENABLE_ACCESS_SECURITY = "ENABLE_ACCESS_SECURITY";
        public static final String ALWAYS_ALLOW_INITIATOR_DOCUMENT_ACCESS_IND = "ALWAYS_ALLOW_INITIATOR_DOCUMENT_ACCESS_IND";
        public static final String ALWAYS_ALLOW_INITIATOR_LINE_ACCESS_IND = "ALWAYS_ALLOW_INITIATOR_LINE_ACCESS_IND";
        public static final String ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND = "ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND";
        public static final String ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND = "ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND";
    }

    public class SecurityTemplateNames {
        public static final String VIEW_DOCUMENT_FIELD_VALUE = "View Document with Field Value";
        public static final String VIEW_ACCOUNTING_LINE_FIELD_VALUE = "View Accounting Line with Field Value";
        public static final String VIEW_NOTES_ATTACHMENTS_FIELD_VALUE = "View Notes/Attachments with Field Value";
        public static final String EDIT_DOCUMENT_FIELD_VALUE = "Edit Document with Field Value";
        public static final String EDIT_ACCOUNTING_LINE_FIELD_VALUE = "Edit Accounting Line with Field Value";
        public static final String LOOKUP_FIELD_VALUE = "Lookup with Field Value";
        public static final String INQUIRY_FIELD_VALUE = "Balance Inquiry with Field Value";
    }

    public class SecurityValueSpecialCharacters {
        public static final String MULTI_VALUE_SEPERATION_CHARACTER = ";";
        public static final String WILDCARD_CHARACTER = "*";
    }
}
