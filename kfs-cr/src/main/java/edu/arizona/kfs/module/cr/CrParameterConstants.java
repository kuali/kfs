package edu.arizona.kfs.module.cr;

/**
 * This class houses constants used for the parameter names used within the Check Reconciliation Module.
 */
public class CrParameterConstants {

    public static final String CR_NAMESPACE_CODE = "KFS-CR";
    public static final String CHECK_RECONCILIATION_IMPORT_STEP_COMPONENT = "CheckReconciliationImportStep";
    public static final String CHECK_RECONCILIATION_TRANSACTION_STEP_COMPONENT = "CheckReconciliationTransactionStep";

    public static class PurapParameters {
        public static final String TRADE_IN_OBJECT_CODE_FOR_CAPITAL_ASSET = "TRADE_IN_OBJECT_CODE_FOR_CAPITAL_ASSET";
        public static final String TRADE_IN_OBJECT_CODE_FOR_CAPITAL_LEASE = "TRADE_IN_OBJECT_CODE_FOR_CAPITAL_LEASE";
    }

    public static class CheckReconciliationImportStep {
        public static final String CLRD_STATUS = "CR_STATUS_CLRD_CODES";
        public static final String CNCL_STATUS = "CR_STATUS_CNCL_CODES";
        public static final String ISSD_STATUS = "CR_STATUS_ISSD_CODES";
        public static final String STAL_STATUS = "CR_STATUS_STAL_CODES";
        public static final String STOP_STATUS = "CR_STATUS_STOP_CODES";
        public static final String VOID_STATUS = "CR_STATUS_VOID_CODES";
    }

    public static class CheckReconciliationTransactionStep {
        public static final String CLEARING_ACCOUNT = "CR_CLEARING_ACCOUNT";
        public static final String CLEARING_OBJECT_CODE = "CR_CLEARING_OBJECT_CODE";
    }

}
