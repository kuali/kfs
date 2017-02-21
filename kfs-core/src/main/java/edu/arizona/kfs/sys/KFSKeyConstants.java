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
    
    //PCDO
    public static final String QUESTION_RETURN_DOCUMENT = "document.question.return.text";
    public static final String MESSAGE_RETURN_NOTE_TEXT_INTRO = "message.return.noteTextIntro";
    public static final String ERROR_DOCUMENT_RETURN_REASON_REQUIRED = "error.document.return.reasonRequired";

}
