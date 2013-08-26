/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.service.impl;

import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.COMPONENT;
import org.kuali.rice.coreservice.framework.parameter.ParameterConstants.NAMESPACE;
import org.kuali.rice.krad.util.KRADConstants;



public class KfsParameterConstants  {

    public static final String NERVOUS_SYSTEM_NAMESPACE = KRADConstants.KNS_NAMESPACE;
    public static final String ALL_COMPONENT = "All";
    public static final String DOCUMENT_COMPONENT = "Document";
    public static final String LOOKUP_COMPONENT = "Lookup";
    public static final String BATCH_COMPONENT = "Batch";

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class NERVOUS_SYSTEM_ALL {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class NERVOUS_SYSTEM_DOCUMENT {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class NERVOUS_SYSTEM_LOOKUP {
    }

    @NAMESPACE(namespace = NERVOUS_SYSTEM_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class NERVOUS_SYSTEM_BATCH {
    }

    public static final String FINANCIAL_NAMESPACE_PREFIX = "KFS-";
    public static final String FINANCIAL_SYSTEM_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "SYS";
    public static final String ACCOUNTS_RECEIVABLE_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "AR";
    public static final String BUDGET_CONSTRUCTION_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "BC";
    public static final String CAPITAL_ASSET_BUILDER_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "CAB";
    public static final String CAPITAL_ASSETS_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "CAM";
    public static final String CONTRACTS_AND_GRANTS_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "CG";
    public static final String CHART_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "COA";
    public static final String EFFORT_REPORTING_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "EC";
    public static final String FINANCIAL_PROCESSING_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "FP";
    public static final String GENERAL_LEDGER_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "GL";
    public static final String LABOR_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "LD";
    public static final String PRE_DISBURSEMENT_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "PDP";
    public static final String PURCHASING_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "PURAP";
    public static final String VENDOR_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "VND";
    public static final String ENDOWMENT_NAMESPACE = FINANCIAL_NAMESPACE_PREFIX + "ENDOW";
    public static final String PARAMETER_CONFIG_TYPE_CODE = "CONFG";
    public static final String PARAMETER_ALL_DETAIL_TYPE = "All";

    @NAMESPACE(namespace = FINANCIAL_SYSTEM_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class FINANCIAL_SYSTEM_ALL {
    }

    @NAMESPACE(namespace = FINANCIAL_SYSTEM_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class FINANCIAL_SYSTEM_DOCUMENT {
    }

    @NAMESPACE(namespace = FINANCIAL_SYSTEM_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class FINANCIAL_SYSTEM_LOOKUP {
    }

    @NAMESPACE(namespace = FINANCIAL_SYSTEM_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class FINANCIAL_SYSTEM_BATCH {
    }

    @NAMESPACE(namespace = ACCOUNTS_RECEIVABLE_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class ACCOUNTS_RECEIVABLE_ALL {
    }

    @NAMESPACE(namespace = ACCOUNTS_RECEIVABLE_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class ACCOUNTS_RECEIVABLE_DOCUMENT {
    }

    @NAMESPACE(namespace = ACCOUNTS_RECEIVABLE_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class ACCOUNTS_RECEIVABLE_LOOKUP {
    }

    @NAMESPACE(namespace = ACCOUNTS_RECEIVABLE_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class ACCOUNTS_RECEIVABLE_BATCH {
    }

    @NAMESPACE(namespace = BUDGET_CONSTRUCTION_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class BUDGET_CONSTRUCTION_ALL {
    }

    @NAMESPACE(namespace = BUDGET_CONSTRUCTION_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class BUDGET_CONSTRUCTION_DOCUMENT {
    }

    @NAMESPACE(namespace = BUDGET_CONSTRUCTION_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class BUDGET_CONSTRUCTION_LOOKUP {
    }

    @NAMESPACE(namespace = BUDGET_CONSTRUCTION_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class BUDGET_CONSTRUCTION_BATCH {
    }

    @NAMESPACE(namespace = CAPITAL_ASSETS_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class CAPITAL_ASSETS_ALL {
    }

    @NAMESPACE(namespace = CAPITAL_ASSETS_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class CAPITAL_ASSETS_DOCUMENT {
    }

    @NAMESPACE(namespace = CAPITAL_ASSETS_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class CAPITAL_ASSETS_LOOKUP {
    }

    @NAMESPACE(namespace = CAPITAL_ASSETS_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class CAPITAL_ASSETS_BATCH {
    }

    @NAMESPACE(namespace = CAPITAL_ASSET_BUILDER_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class CAPITAL_ASSET_BUILDER_ALL {
    }

    @NAMESPACE(namespace = CAPITAL_ASSET_BUILDER_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class CAPITAL_ASSET_BUILDER_DOCUMENT {
    }

    @NAMESPACE(namespace = CAPITAL_ASSET_BUILDER_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class CAPITAL_ASSET_BUILDER_LOOKUP {
    }

    @NAMESPACE(namespace = CAPITAL_ASSET_BUILDER_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class CAPITAL_ASSET_BUILDER_BATCH {
    }

    @NAMESPACE(namespace = CONTRACTS_AND_GRANTS_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class CONTRACTS_AND_GRANTS_ALL {
    }

    @NAMESPACE(namespace = CONTRACTS_AND_GRANTS_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class CONTRACTS_AND_GRANTS_DOCUMENT {
    }

    @NAMESPACE(namespace = CONTRACTS_AND_GRANTS_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class CONTRACTS_AND_GRANTS_LOOKUP {
    }

    @NAMESPACE(namespace = CONTRACTS_AND_GRANTS_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class CONTRACTS_AND_GRANTS_BATCH {
    }

    @NAMESPACE(namespace = CHART_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class CHART_ALL {
    }

    @NAMESPACE(namespace = CHART_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class CHART_DOCUMENT {
    }

    @NAMESPACE(namespace = CHART_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class CHART_LOOKUP {
    }

    @NAMESPACE(namespace = CHART_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class CHART_BATCH {
    }

    @NAMESPACE(namespace = EFFORT_REPORTING_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class EFFORT_REPORTING_ALL {
    }

    @NAMESPACE(namespace = EFFORT_REPORTING_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class EFFORT_REPORTING_DOCUMENT {
    }

    @NAMESPACE(namespace = EFFORT_REPORTING_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class EFFORT_REPORTING_LOOKUP {
    }

    @NAMESPACE(namespace = EFFORT_REPORTING_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class EFFORT_REPORTING_BATCH {
    }

    @NAMESPACE(namespace = ENDOWMENT_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class ENDOWMENT_ALL {
    }

    @NAMESPACE(namespace = ENDOWMENT_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class ENDOWMENT_DOCUMENT {
    }

    @NAMESPACE(namespace = ENDOWMENT_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class ENDOWMENT_LOOKUP {
    }

    @NAMESPACE(namespace = ENDOWMENT_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class ENDOWMENT_BATCH {
    }

    @NAMESPACE(namespace = FINANCIAL_PROCESSING_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class FINANCIAL_PROCESSING_ALL {
    }

    @NAMESPACE(namespace = FINANCIAL_PROCESSING_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class FINANCIAL_PROCESSING_DOCUMENT {
    }

    @NAMESPACE(namespace = FINANCIAL_PROCESSING_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class FINANCIAL_PROCESSING_LOOKUP {
    }

    @NAMESPACE(namespace = FINANCIAL_SYSTEM_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class FINANCIAL_PROCESSING_BATCH {
    }

    @NAMESPACE(namespace = GENERAL_LEDGER_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class GENERAL_LEDGER_ALL {
    }

    @NAMESPACE(namespace = GENERAL_LEDGER_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class GENERAL_LEDGER_DOCUMENT {
    }

    @NAMESPACE(namespace = GENERAL_LEDGER_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class GENERAL_LEDGER_LOOKUP {
    }

    @NAMESPACE(namespace = GENERAL_LEDGER_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class GENERAL_LEDGER_BATCH {
    }

    @NAMESPACE(namespace = LABOR_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class LABOR_ALL {
    }

    @NAMESPACE(namespace = LABOR_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class LABOR_DOCUMENT {
    }

    @NAMESPACE(namespace = LABOR_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class LABOR_LOOKUP {
    }

    @NAMESPACE(namespace = LABOR_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class LABOR_BATCH {
    }

    @NAMESPACE(namespace = PRE_DISBURSEMENT_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class PRE_DISBURSEMENT_ALL {
    }

    @NAMESPACE(namespace = PRE_DISBURSEMENT_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class PRE_DISBURSEMENT_DOCUMENT {
    }

    @NAMESPACE(namespace = PRE_DISBURSEMENT_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class PRE_DISBURSEMENT_LOOKUP {
    }

    @NAMESPACE(namespace = PRE_DISBURSEMENT_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class PRE_DISBURSEMENT_BATCH {
    }

    @NAMESPACE(namespace = PURCHASING_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class PURCHASING_ALL {
    }

    @NAMESPACE(namespace = PURCHASING_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class PURCHASING_DOCUMENT {
    }

    @NAMESPACE(namespace = PURCHASING_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class PURCHASING_LOOKUP {
    }

    @NAMESPACE(namespace = PURCHASING_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class PURCHASING_BATCH {
    }

    @NAMESPACE(namespace = VENDOR_NAMESPACE)
    @COMPONENT(component = ALL_COMPONENT)
    public final class VENDOR_ALL {
    }

    @NAMESPACE(namespace = VENDOR_NAMESPACE)
    @COMPONENT(component = DOCUMENT_COMPONENT)
    public final class VENDOR_DOCUMENT {
    }

    @NAMESPACE(namespace = VENDOR_NAMESPACE)
    @COMPONENT(component = LOOKUP_COMPONENT)
    public final class VENDOR_LOOKUP {
    }

    @NAMESPACE(namespace = VENDOR_NAMESPACE)
    @COMPONENT(component = BATCH_COMPONENT)
    public final class VENDOR_BATCH {
    }

    // CSU 6702 BEGIN
    public static class YEAR_END_ACCOUNTING_PERIOD_PARAMETER_NAMES {
        public static final String DETAIL_PARAMETER_TYPE = "Document";
        public static final String ENABLE_FISCAL_PERIOD_SELECTION_IND = "ENABLE_FISCAL_PERIOD_SELECTION_IND";
        public static final String FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES = "FISCAL_PERIOD_SELECTION_DOCUMENT_TYPES";
        public static final String NUMBER_OF_POSTBACK_PERIODS = "NUMBER_OF_POSTBACK_PERIODS";
    }

    public static final String INSTITUTION_NAME = "INSTITUTION_NAME";
}
