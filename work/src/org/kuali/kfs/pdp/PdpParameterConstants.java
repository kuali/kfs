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
package org.kuali.kfs.pdp;

/**
 * Contains PDP parameter constants
 */
public class PdpParameterConstants {
    public static final String DISBURSEMENT_CANCELLATION_EMAIL_ADDRESSES = "DISBURSEMENT_CANCELLATION_TO_EMAIL_ADDRESSES";
    public static final String PAYMENT_LOAD_FAILURE_EMAIL_SUBJECT_PARAMETER_NAME = "FAILURE_EMAIL_SUBJECT";
    public static final String PAYMENT_LOAD_SUCCESS_EMAIL_SUBJECT_PARAMETER_NAME = "SUCCESS_EMAIL_SUBJECT";
    public static final String PAYMENT_LOAD_THRESHOLD_EMAIL_SUBJECT_PARAMETER_NAME = "THRESHOLD_EMAIL_SUBJECT";
    public static final String PAYMENT_LOAD_TAX_EMAIL_SUBJECT_PARAMETER_NAME = "TAX_EMAIL_SUBJECT";
    public static final String ACH_SUMMARY_EMAIL_SUBJECT_PARAMETER_NAME = "ACH_SUMMARY_EMAIL_SUBJECT";
    public static final String ACH_SUMMARY_TO_EMAIL_ADDRESS_PARMAETER_NAME = "ACH_SUMMARY_TO_EMAIL_ADDRESSES";
    
    public static class BatchConstants{
        public static final String BATCH_ID_PARAM = "batchId";
    }
    
    public static class PaymentDetail {
        public static final String DETAIL_ID_PARAM = "detailId";
        public static final String SHOW_PAYMENT_DETAIL = "showPaymentDetail";
    }
    
    public static class FormatProcess {
        public static final String PROCESS_ID_PARAM = "processId";
    }
    
    public static final String MESSAGE_PARAM = "message";
    public static final String ERROR_KEY_LIST_PARAM = "errorKeyList";
    public static final String ERROR_KEY_LIST_SEPARATOR = ";";
    public static final String ACTION_SUCCESSFUL_PARAM = "actionSuccesful";
}
