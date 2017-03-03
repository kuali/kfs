package edu.arizona.kfs.fp.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent the procurement cardholder data from the bank. It is loaded through an
 *  XML ingestion process.
 */
public class ProcurementCardHolderLoad extends PersistableBusinessObjectBase {
    private String creditCardNumber;
    private String cardHolderName;
    private String cardHolderAlternateName;
    private String cardHolderLine1Address;
    private String cardHolderLine2Address;
    private String cardHolderCityName;
    private String cardHolderStateCode;
    private String cardHolderZipCode;
    private String cardHolderWorkPhoneNumber;
    private KualiDecimal cardLimit;
    private KualiDecimal cardCycleAmountLimit;
    private Integer cardCycleVolumeLimit;
    private Integer cardMonthlyNumber;
    private String cardStatusCode;
    private String cardNoteText;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;
    private String organizationCode;
    private String cardHolderSystemId;
    private Date cardOpenDate;
    private Date cardCancelDate;
    private String cardExpireDate;
    
    
    public String getCreditCardNumber() {
        return creditCardNumber;
    }

    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardHolderAlternateName() {
        return cardHolderAlternateName;
    }

    public void setCardHolderAlternateName(String cardHolderAlternateName) {
        this.cardHolderAlternateName = cardHolderAlternateName;
    }

    public String getCardHolderLine1Address() {
        return cardHolderLine1Address;
    }

    public void setCardHolderLine1Address(String cardHolderLine1Address) {
        this.cardHolderLine1Address = cardHolderLine1Address;
    }

    public String getCardHolderLine2Address() {
        return cardHolderLine2Address;
    }

    public void setCardHolderLine2Address(String cardHolderLine2Address) {
        this.cardHolderLine2Address = cardHolderLine2Address;
    }

    public String getCardHolderCityName() {
        return cardHolderCityName;
    }

    public void setCardHolderCityName(String cardHolderCityName) {
        this.cardHolderCityName = cardHolderCityName;
    }

    public String getCardHolderStateCode() {
        return cardHolderStateCode;
    }

    public void setCardHolderStateCode(String cardHolderStateCode) {
        this.cardHolderStateCode = cardHolderStateCode;
    }

    public String getCardHolderZipCode() {
        return cardHolderZipCode;
    }

    public void setCardHolderZipCode(String cardHolderZipCode) {
        this.cardHolderZipCode = cardHolderZipCode;
    }

    public String getCardHolderWorkPhoneNumber() {
        return cardHolderWorkPhoneNumber;
    }

    public void setCardHolderWorkPhoneNumber(String cardHolderWorkPhoneNumber) {
        this.cardHolderWorkPhoneNumber = cardHolderWorkPhoneNumber;
    }

    public KualiDecimal getCardLimit() {
        return cardLimit;
    }

    public void setCardLimit(KualiDecimal cardLimit) {
        this.cardLimit = cardLimit;
    }

    public void setCardLimit(String cardLimit) {
        if (StringUtils.isNotBlank(cardLimit)) {
            this.cardLimit = new KualiDecimal(cardLimit);
        }
        else {
            this.cardLimit = KualiDecimal.ZERO;
        }
    }    
    
    public KualiDecimal getCardCycleAmountLimit() {
        return cardCycleAmountLimit;
    }

    public void setCardCycleAmountLimit(KualiDecimal cardCycleAmountLimit) {
        this.cardCycleAmountLimit = cardCycleAmountLimit;
    }

    public void setCardCycleAmountLimit(String cardCycleAmountLimit) {
        if (StringUtils.isNotBlank(cardCycleAmountLimit)) {
            this.cardCycleAmountLimit = new KualiDecimal(cardCycleAmountLimit);
        }
        else {
            this.cardCycleAmountLimit = KualiDecimal.ZERO;
        }
    }
    
    public Integer getCardCycleVolumeLimit() {
        return cardCycleVolumeLimit;
    }

    public void setCardCycleVolumeLimit(Integer cardCycleVolumeLimit) {
        this.cardCycleVolumeLimit = cardCycleVolumeLimit;
    }
    
    public void setCardCycleVolumeLimit(String cardCycleVolumeLimit) {
        if (StringUtils.isNotBlank(cardCycleVolumeLimit)) {
            this.cardCycleVolumeLimit = new Integer(cardCycleVolumeLimit);
        }
        else {
            this.cardCycleVolumeLimit = 0;
        }
    }
    
    public Integer getCardMonthlyNumber() {
        return cardMonthlyNumber;
    }

    public void setCardMonthlyNumber(Integer cardMonthlyNumber) {
        this.cardMonthlyNumber = cardMonthlyNumber;
    }

    public void setCardMonthlyNumber(String cardMonthlyNumber) {
        if (StringUtils.isNotBlank(cardMonthlyNumber)) {
            this.cardMonthlyNumber = new Integer(cardMonthlyNumber);
        }
        else {
            this.cardMonthlyNumber = 0;
        }
    }
    
    public String getCardStatusCode() {
        return cardStatusCode;
    }

    public void setCardStatusCode(String cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }

    public String getCardNoteText() {
        return cardNoteText;
    }

    public void setCardNoteText(String cardNoteText) {
        this.cardNoteText = cardNoteText;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getCardHolderSystemId() {
        return cardHolderSystemId;
    }

    public void setCardHolderSystemId(String cardHolderSystemId) {
        this.cardHolderSystemId = cardHolderSystemId;
    }

    public Date getCardOpenDate() {
        return cardOpenDate;
    }

    public void setCardOpenDate(Date cardOpenDate) {
        this.cardOpenDate = cardOpenDate;
    }
    
    public void setCardOpenDate(String cardOpenDate) {
        if (StringUtils.isNotBlank(cardOpenDate)) {
            this.cardOpenDate = (Date) (new SqlDateConverter()).convert(Date.class, cardOpenDate);
        }
    }

    public Date getCardCancelDate() {
        return cardCancelDate;
    }

    public void setCardCancelDate(Date cardCancelDate) {
        this.cardCancelDate = cardCancelDate;
    }

    public void setCardCancelDate(String cardCancelDate) {
        if (StringUtils.isNotBlank(cardCancelDate)) {
            this.cardCancelDate = (Date) (new SqlDateConverter()).convert(Date.class, cardCancelDate);
        }
    }
    
    public String getCardExpireDate() {
        return cardExpireDate;
    }

    public void setCardExpireDate(String cardExpireDate) {
        this.cardExpireDate = cardExpireDate;
    }

    protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(KFSPropertyConstants.TRANSACTION_CREDIT_CARD_NUMBER, this.creditCardNumber);
        return m;
    }
}
