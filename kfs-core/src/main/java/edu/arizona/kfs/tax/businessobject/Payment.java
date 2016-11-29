package edu.arizona.kfs.tax.businessobject;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.NoteService;


public class Payment extends PersistableBusinessObjectBase implements Serializable {

	private static final long serialVersionUID = 1L;
	private Integer id; // PAYMENT_ID
    private KualiInteger paymentGroupId; // PAYMENT_GRP_ID
    private String docNbr; // DOC_NBR
    private String poNbr; // PO_NBR
    private Date disbursementDt; // DISB_DT
    private KualiInteger disbursementNbr; // DISB_NBR
    private String docType; // DOC_TYPE
    private String invoiceNbr; // INV_NBR
    private KualiInteger paymentAcctDetailId; // PMT_ACCT_DETAIL_ID
    private String finChartCode; // FIN_CHART_CD
    private String paymentTypeCode; // PMT_TYPE_CD
    private String accountNbr; // ACCOUNT_NBR
    private String finObjectCode; // FIN_OBJECT_CD
    private KualiDecimal acctNetAmount; // ACCT_NET_AMT
    private Integer payeeId;
    private Payee payee; // PAYEE_ID
    private Boolean excludeIndicator; // exclude_ind 
    private List<Note> boNotes;
    
    
    public Payment() {
        super();
    }
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public KualiInteger getPaymentGroupId() {
        return paymentGroupId;
    }

    public void setPaymentGroupId(KualiInteger kualiInteger) {
        this.paymentGroupId = kualiInteger;
    }

    public String getDocNbr() {
        return docNbr;
    }

    public void setDocNbr(String docNbr) {
        this.docNbr = docNbr;
    }

    public String getPoNbr() {
        return poNbr;
    }

    public void setPoNbr(String poNbr) {
        this.poNbr = poNbr;
    }

    public Date getDisbursementDt() {
        return disbursementDt;
    }

    public void setDisbursementDt(Date date) {
        this.disbursementDt = date;
    }

    public KualiInteger getDisbursementNbr() {
        return disbursementNbr;
    }

    public void setDisbursementNbr(KualiInteger kualiInteger) {
        this.disbursementNbr = kualiInteger;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getInvoiceNbr() {
        return invoiceNbr;
    }

    public void setInvoiceNbr(String invoiceNbr) {
        this.invoiceNbr = invoiceNbr;
    }

    public KualiInteger getPaymentAcctDetailId() {
        return paymentAcctDetailId;
    }

    public void setPaymentAcctDetailId(KualiInteger kualiInteger) {
        this.paymentAcctDetailId = kualiInteger;
    }

    public String getFinChartCode() {
        return finChartCode;
    }

    public void setFinChartCode(String finChartCode) {
        this.finChartCode = finChartCode;
    }

    public String getAccountNbr() {
        return accountNbr;
    }

    public void setAccountNbr(String accountNbr) {
        this.accountNbr = accountNbr;
    }

    public String getFinObjectCode() {
        return finObjectCode;
    }

    public void setFinObjectCode(String finObjectCode) {
        this.finObjectCode = finObjectCode;
    }

    public KualiDecimal getAcctNetAmount() {
        return acctNetAmount;
    }

    public void setAcctNetAmount(KualiDecimal acctNetAmount) {
        this.acctNetAmount = acctNetAmount;
    }

    public Boolean getExcludeIndicator() {
        return excludeIndicator;
    }

    public void setExcludeIndicator(Boolean excludeIndicator) {
        this.excludeIndicator = excludeIndicator;
    }

    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }

    public Payee getPayee() {
        return payee;
    }

    public void setPayee(Payee payee) {
        this.payee = payee;
    }

    public Integer getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(Integer payeeId) {
        this.payeeId = payeeId;
    }

    public String getViewPayment() {
        return "View Payment";
    }

	public List<Note> getBoNotes() {
		if(!StringUtils.isEmpty(getObjectId())) {
			boNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(getObjectId());
		}
		// ensure that the list is not null after this point
		if(boNotes == null) {
			boNotes = new ArrayList<Note>();
		}
		
		return boNotes;
	}

	public void setBoNotes(List<Note> boNotes) {
		this.boNotes = boNotes;
	}
	
	protected LinkedHashMap<String, String> toStringMapper() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();

        m.put("paymentGroupId", getPaymentGroupId().toString());
        m.put("poNbr", getPoNbr());
        m.put("invoiceNbr", getInvoiceNbr());
        
        return m;
    }
}
