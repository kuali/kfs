package edu.arizona.kfs.sys;

public class KFSKeyConstants extends org.kuali.kfs.sys.KFSKeyConstants {

    // DV
    public static final String MESSAGE_DV_DUPLICATE_INVOICE = "message.dv.duplicate.invoice";

    // GL
    public static final String ERROR_GLOBAL_TRANSACTION_EDIT_RULE_FAILURE = "error.gl.GlobalTransactionEdit";
    public static final String ERROR_GLOBAL_TRANSACTION_EDIT_RULE_FAILURE_W_ACCT_LINE = "error.gl.GlobalTransactionEdit.accountingLine";
    public static final String ERROR_GLOBAL_TRANSACTION_EDIT_ACCOUNT_NUMBER_CHANGED = "error.gl.GlobalTransactionEdit.accountChanged";
    public static final String ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES_ON_LINE = "error.gl.GlobalTransactionEdit.invalidFieldValueOnLine";
    public static final String ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES = "error.gl.GlobalTransactionEdit.invalidFieldValue";

    // GEC
    public static final String ERROR_GEC_REF_NUMBER_INVALID = "error.gec.ref.number";
    public static final String ERROR_ERROR_CERT_FIELDS_REQ = "error.errorCert.fieldsreq";
    public static final String ERROR_ERROR_CERT_DATE_PARAM_TRIGGERED = "error.errorCert.dateParamTriggered";
    public static final String QUESTION_ERROR_CERTIFICATION_STMT = "question.gec.errorCertStmt";

    //CM
    public static final String WARNING_BANK_NOT_REQUIRED = "warning.document.disbursementvoucher.bank.not.required";
    public static final String ERROR_BANK_REQUIRED_PER_PAYMENT_METHOD = "error.document.disbursementvoucher.bank.required";
    
    // Shipping Account
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_SHIP = "message.batchUpload.title.shipping";
    
    //DV NonEmployee
    public static final String ERROR_DV_PER_DIEM_START_DT_AFTER_END_DT = "error.dv.perDiemStartDtBeforeAfterEndDt";
    public static final String ERROR_DV_DISALLOWED_BY_PAYMENT_METHOD = "error.dv.disallowedByPaymentMethod";

    // Income Type Error messages
    public static final String ERROR_INVALID_INCOME_TYPES_TOTAL_AMOUNT = "errors.document.ap.incomeTypesTotalAmount.invalid";
    public static final String ERROR_INCOME_TYPE_COA_REQUIRED = "error.income.type.coa.required";
    public static final String ERROR_INCOME_TYPE_CODE_REQUIRED = "error.income.type.code.required";
    public static final String ERROR_INCOME_TYPE_AMOUNT_REQUIRED = "error.income.type.amount.required";
    
    //ACH Banking
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_ACH_BANK = "message.batchUpload.title.achBank";
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_ACH_PAYEE = "message.batchUpload.title.achPayee";

    // Procurement Cardholder
    public static final String MESSAGE_BATCH_UPLOAD_TITLE_PCDH = "message.batchUpload.title.procurementCardHolder";

    // DV Payment Reason
    public static final String ERROR_DISBURSEMENT_VOUCHER_PAYMENT_REASON_CODE_INACTIVE = "error.disbursement.voucher.payment.reason.code.inactive";

  //PCDO
    public static final String QUESTION_RETURN_DOCUMENT = "document.question.return.text";
    public static final String MESSAGE_RETURN_NOTE_TEXT_INTRO = "message.return.noteTextIntro";
    public static final String ERROR_DOCUMENT_RETURN_REASON_REQUIRED = "error.document.return.reasonRequired";
}
