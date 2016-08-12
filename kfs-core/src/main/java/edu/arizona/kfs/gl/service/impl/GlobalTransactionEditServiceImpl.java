package edu.arizona.kfs.gl.service.impl;

import edu.arizona.kfs.gl.businessobject.GlobalTransactionEditDetail;
import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GlobalTransactionEditServiceImpl implements GlobalTransactionEditService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlobalTransactionEditServiceImpl.class);

    private static final String WILDCARD = "@";
    private static final String WILDCARD_REPLACEMENT_TEXT = "[Any]";
    private static final String UNSPECIFIED_GTE_PURPOSE_TEXT = "Unspecified";
    private static final String OBJECT_SUB_TYPE_CODE = "objectSubTypeCode";
    private static final String GLOBAL_TRANSACTION_EDIT_IND = "GLOBAL_TRANSACTION_EDIT_IND";

    private BusinessObjectService boService;
    private ParameterService parmService;
    private HomeOriginationService originService;
    private String serviceName;


    public GlobalTransactionEditServiceImpl() {
        //
    }


    public Message isAccountingLineAllowable(AccountingLineBase accountingLine, String origin, String docTypeCd) {
        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        if (ObjectUtils.isNull(accountingLine.getAccount())) {
            throw new IllegalArgumentException("This account specified " + accountingLine.getChartOfAccountsCode() + "-" + accountingLine.getAccountNumber() + " does not exist. For sequence " + accountingLine.getReferenceFinancialSystemDocumentTypeCode());
        }

        accountingLine.getAccount().refreshReferenceObject(KFSPropertyConstants.SUB_FUND_GROUP);
        if (ObjectUtils.isNull(accountingLine.getAccount().getSubFundGroup())) {
            throw new IllegalArgumentException("The SubFundGroupCode on the account specified " + accountingLine.getAccount().getSubFundGroupCode() + " does not exist. For sequence " + accountingLine.getReferenceFinancialSystemDocumentTypeCode());
        }

        String fundGrpCd = accountingLine.getAccount().getSubFundGroup().getFundGroupCode();
        String subFundGrpCd = accountingLine.getAccount().getSubFundGroupCode();

        accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            throw new IllegalArgumentException("This ObjectCode specified " + accountingLine.getFinancialObjectCode() + " does not exist. For sequence " + accountingLine.getReferenceFinancialSystemDocumentTypeCode());
        }

        String objTypCd = accountingLine.getObjectCode().getFinancialObjectTypeCode();
        String subObjTypCd = accountingLine.getObjectCode().getFinancialObjectSubTypeCode();

        return isAccountingLineAllowable(origin, fundGrpCd, subFundGrpCd, docTypeCd, objTypCd, subObjTypCd);
    }

    @Override
    public Message isAccountingLineAllowable(AccountingLineBase accountingLine, String docTypeCd) {
        return this.isAccountingLineAllowable(accountingLine, getHomeOriginCode(), docTypeCd);
    }

    @Override
    public Message isAccountingLineAllowable(String originCd, String fundGrpCd, String subFundGrpCd, String docTypeCd, String objTypCd, String subObjTypCd) {
        if (!isGteParamIndActive()) {
            return null;
        }

        List<GlobalTransactionEditDetail> gteDetails = getMatchingGTEDetails(originCd, fundGrpCd, subFundGrpCd, docTypeCd, objTypCd, subObjTypCd);
        if (gteDetails == null || gteDetails.isEmpty()) {
            return null;
        }

        logGTEDetails(gteDetails);
        GlobalTransactionEditDetail gteDetail = gteDetails.get(0);
        Object[] invalidValues = getGteDetailValuesArray(gteDetail);
        Message msg = MessageBuilder.buildMessageWithPlaceHolder(
                KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_RULE_FAILURE,
                Message.TYPE_FATAL,
                invalidValues);
        return msg;
    }

    public boolean isAccountingLineBatchAllowable(CollectorBatch batch, MessageMap messageMap) {
        if (!isGteParamIndActive()) {
            return true;
        }
        boolean result = true;

        for (OriginEntryFull originEntry : batch.getOriginEntries()) {

            originEntry.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
            originEntry.refreshReferenceObject(KFSPropertyConstants.FINANCIAL_OBJECT);
            if (ObjectUtils.isNull(originEntry.getFinancialObject())) {
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES_ON_LINE, originEntry.getTransactionLedgerEntrySequenceNumber().toString(), "Object Code", originEntry.getChartOfAccountsCode() + "-" + originEntry.getFinancialObjectCode());
                continue;
            }
            originEntry.refreshReferenceObject(KFSPropertyConstants.ORIGINATION);
            OriginationCode origin = originEntry.getOrigination();
            if (ObjectUtils.isNull(origin)) {
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES_ON_LINE, originEntry.getTransactionLedgerEntrySequenceNumber().toString(), "Origin Code", originEntry.getFinancialSystemOriginationCode());
                continue;
            }
            Account account = originEntry.getAccount();
            if (ObjectUtils.isNull(account)) {
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES_ON_LINE, originEntry.getTransactionLedgerEntrySequenceNumber().toString(), "Account", originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber());
                continue;
            }
            account.refreshReferenceObject(KFSPropertyConstants.SUB_FUND_GROUP);
            if (ObjectUtils.isNull(account.getSubFundGroup())) {
                messageMap.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_SCRUBBER_INVALID_VALUES_ON_LINE, originEntry.getTransactionLedgerEntrySequenceNumber().toString(), "Sub Fund", account.getSubFundGroupCode());
                continue;
            }

            List<GlobalTransactionEditDetail> gteDetails = getMatchingGTEDetails(originEntry.getFinancialSystemOriginationCode(),
                    account.getSubFundGroup().getFundGroupCode(),
                    account.getSubFundGroupCode(),
                    originEntry.getFinancialDocumentTypeCode(),
                    originEntry.getFinancialObject().getFinancialObjectTypeCode(),
                    originEntry.getFinancialObject().getFinancialObjectSubTypeCode());
            if (gteDetails == null || gteDetails.isEmpty()) {
                continue;
            }
            logGTEDetails(gteDetails);
            //we will only pop the first item since that must get fixed before we can move on anyway
            GlobalTransactionEditDetail gteDetail = gteDetails.get(0);
            messageMap.putError(
                    KFSConstants.GLOBAL_ERRORS,
                    KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_RULE_FAILURE_W_ACCT_LINE,
                    originEntry.getAccountNumber(),
                    originEntry.getFinancialObjectCode(),
                    originEntry.getOrganizationDocumentNumber(),
                    StringUtils.isBlank(gteDetail.getObjectCodeRulePurpose()) ? UNSPECIFIED_GTE_PURPOSE_TEXT : gteDetail.getObjectCodeRulePurpose(),
                    WILDCARD.equals(gteDetail.getOriginCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getOriginCodeFullText(),
                    WILDCARD.equals(gteDetail.getFundGroupCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getFundGroupCodeFullText(),
                    WILDCARD.equals(gteDetail.getSubFundGroupCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getSubFundGroupCodeFullText(),
                    WILDCARD.equals(gteDetail.getDocumentTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getDocumentTypeCodeFullText(),
                    WILDCARD.equals(gteDetail.getObjectTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getObjectTypeCodeFullText(),
                    WILDCARD.equals(gteDetail.getObjectSubTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getObjectSubTypeCodeFullText());
            result = false;
        }
        return result;
    }

    private List<GlobalTransactionEditDetail> getMatchingGTEDetails(String originCd, String fundGrpCd, String subFundGrpCd, String docTypeCd, String objTypCd, String subObjTypCd) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        //constructing keys with an Array list so our match will include wildcards
        fieldValues.put(KFSPropertyConstants.ORIGIN_CODE, Arrays.asList(originCd, WILDCARD));
        fieldValues.put(KFSPropertyConstants.FUND_GROUP_CODE, Arrays.asList(fundGrpCd, WILDCARD));
        fieldValues.put(KFSPropertyConstants.SUB_FUND_GROUP_CODE, Arrays.asList(subFundGrpCd, WILDCARD));
        fieldValues.put(KFSPropertyConstants.DOCUMENT_TYPE_CODE, Arrays.asList(docTypeCd, WILDCARD));
        fieldValues.put(KFSPropertyConstants.OBJECT_TYPE_CODE, Arrays.asList(objTypCd, WILDCARD));
        fieldValues.put(OBJECT_SUB_TYPE_CODE, Arrays.asList(subObjTypCd, WILDCARD));
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);

        //using the findMatching routine which allows us to also search using wildcards - which is why we pass in a map
        return (List<GlobalTransactionEditDetail>) boService.findMatching(GlobalTransactionEditDetail.class, fieldValues);
    }

    private void logGTEDetails(List<GlobalTransactionEditDetail> gteDetails) {
        // this string will get displayed to the user in the case a rule fails, we will put that rule into the error message
        for (GlobalTransactionEditDetail gteDetail : gteDetails) {
            Object[] gteDetailValues = getGteDetailValuesArray(gteDetail);
            Message msg = MessageBuilder.buildMessageWithPlaceHolder(
                    KFSKeyConstants.ERROR_GLOBAL_TRANSACTION_EDIT_RULE_FAILURE,
                    Message.TYPE_FATAL,
                    gteDetailValues);
            LOG.error(msg.getMessage());
        }
    }

    private Object[] getGteDetailValuesArray(GlobalTransactionEditDetail gteDetail) {
        Object[] gteDetailValuesArray = new Object[]{
                WILDCARD.equals(gteDetail.getObjectCodeRulePurpose()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getObjectCodeRulePurpose(),
                WILDCARD.equals(gteDetail.getOriginCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getOriginCode(),
                WILDCARD.equals(gteDetail.getFundGroupCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getFundGroupCode(),
                WILDCARD.equals(gteDetail.getSubFundGroupCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getSubFundGroupCode(),
                WILDCARD.equals(gteDetail.getDocumentTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getDocumentTypeCode(),
                WILDCARD.equals(gteDetail.getObjectTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getObjectTypeCode(),
                WILDCARD.equals(gteDetail.getObjectSubTypeCode()) ? WILDCARD_REPLACEMENT_TEXT : gteDetail.getObjectSubTypeCode()};
        return gteDetailValuesArray;
    }

    private boolean isGteParamIndActive() {
        Boolean isGteActive = parmService.getParameterValueAsBoolean(
                KfsParameterConstants.FINANCIAL_SYSTEM_NAMESPACE,
                KfsParameterConstants.ALL_COMPONENT,
                GLOBAL_TRANSACTION_EDIT_IND);
        if (isGteActive == null) {
            throw new RuntimeException("ParameterService returned null for param with name: " + GLOBAL_TRANSACTION_EDIT_IND);
        }

        return isGteActive;
    }

    private String getHomeOriginCode() {
        return originService.getHomeOrigination().getFinSystemHomeOriginationCode();
    }

    public void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    public void setParmService(ParameterService parmService) {
        this.parmService = parmService;
    }

    public void setOriginService(HomeOriginationService originService) {
        this.originService = originService;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

}
