package org.kuali.module.gl.util;

import java.util.Arrays;

import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.gl.bo.OriginEntry;

public class OriginEntryKey {
	static BalanceTypService balanceTypService = 
		(BalanceTypService) SpringServiceLocator.getBalanceTypService();
	static DocumentTypeService documentTypeService = 
		(DocumentTypeService) SpringServiceLocator.getDocumentTypeService();
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(!(obj instanceof OriginEntryKey) || null == obj) {
			return false;
		}
		
		OriginEntryKey key = (OriginEntryKey) obj;
		return LogicHelper.isEqual(
				getAccountNumber(), key.getAccountNumber())
			&& LogicHelper.isEqual(
				getBalanceTypeCode(), key.getBalanceTypeCode())
			&& LogicHelper.isEqual(
				getChartCode(), key.getChartCode())
			&& LogicHelper.isEqual(
				getDocumentTypeCode(), key.getDocumentTypeCode())
			&& LogicHelper.isEqual(
				getEntrySequenceNumber(), key.getEntrySequenceNumber())
			&& LogicHelper.isEqual(
				getFinancialDocumentNumber(), key.getFinancialDocumentNumber())
			&& LogicHelper.isEqual(
				getFinancialObjectCode(), key.getFinancialObjectCode())
			&& LogicHelper.isEqual(
				getFiscalPeriodCode(), key.getFiscalPeriodCode())
			&& LogicHelper.isEqual(
				getFiscalYear(), key.getFiscalYear())
			&& LogicHelper.isEqual(
				getObjectTypeCode(), key.getObjectTypeCode())
			&& LogicHelper.isEqual(
				getSubAccountNumber(), key.getSubAccountNumber())
			&& LogicHelper.isEqual(
				getSubObjectCode(), key.getSubObjectCode())
			&& LogicHelper.isEqual(
				getSystemOriginationCode(), key.getSystemOriginationCode());
	}
	
	/**
	 * 
	 * @param entry
	 * @return
	 */
	static public OriginEntryKey buildFromOriginEntry(OriginEntry entry) {
		if(null == entry) {return null;}
		
		OriginEntryKey key = new OriginEntryKey();
		key.setAccountNumber(entry.getAccountNumber());
		key.setBalanceTypeCode(entry.getFinancialBalanceTypeCode());
		key.setChartCode(entry.getChartOfAccountsCode());
		key.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
		key.setEntrySequenceNumber(entry.getTrnEntryLedgerSequenceNumber().toString());
		key.setFinancialDocumentNumber(entry.getFinancialDocumentNumber());
		key.setFinancialObjectCode(entry.getFinancialObjectCode());
		key.setFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
		key.setFiscalYear(entry.getUniversityFiscalYear().toString());
		key.setObjectTypeCode(entry.getFinancialObjectTypeCode());
		key.setSubAccountNumber(entry.getSubAccountNumber());
		key.setSubObjectCode(entry.getFinancialSubObjectCode());
		key.setSystemOriginationCode(entry.getFinancialSystemOriginationCode());
		return key;
	}
	
	/**
	 * 
	 * @param entry
	 */
	public void setIntoOriginEntry(OriginEntry entry) {
		entry.setAccountNumber(getAccountNumber());
		entry.setBalanceType(balanceTypService.getBalanceTypByCode(getBalanceTypeCode()));
		entry.setChartOfAccountsCode(getChartCode());
		entry.setDocumentType(documentTypeService.getDocumentTypeByCode(getDocumentTypeCode()));
		entry.setTrnEntryLedgerSequenceNumber(new Integer(getEntrySequenceNumber()));
		entry.setFinancialDocumentNumber(getFinancialDocumentNumber());
		entry.setFinancialObjectCode(getFinancialObjectCode());
		entry.setUniversityFiscalPeriodCode(getFiscalPeriodCode());
		entry.setUniversityFiscalYear(new Integer(getFiscalYear()));
		entry.setFinancialObjectTypeCode(getObjectTypeCode());
		entry.setSubAccountNumber(getSubAccountNumber());
		entry.setFinancialSubObjectCode(getSubObjectCode());
		entry.setFinancialSystemOriginationCode(getSystemOriginationCode());
	}
	
	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return new String(accountNumber);
	}

	/**
	 * @param accountNumber
	 *            The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		if (null != accountNumber) {
			System.arraycopy(accountNumber.toCharArray(), 0,
					this.accountNumber, 0, this.accountNumber.length);
		} else {
			Arrays.fill(this.accountNumber, (char) 0);
		}
	}

	/**
	 * @return Returns the balanceTypeCode.
	 */
	public String getBalanceTypeCode() {
		return new String(balanceTypeCode);
	}

	/**
	 * @param balanceTypeCode
	 *            The balanceTypeCode to set.
	 */
	public void setBalanceTypeCode(String balanceTypeCode) {
		if (null != balanceTypeCode) {
			System.arraycopy(balanceTypeCode.toCharArray(), 0,
					this.balanceTypeCode, 0, this.balanceTypeCode.length);
		} else {
			Arrays.fill(this.balanceTypeCode, (char) 0);
		}
	}

	/**
	 * @return Returns the chartCode.
	 */
	public String getChartCode() {
		return new String(chartCode);
	}

	/**
	 * @param chartCode
	 *            The chartCode to set.
	 */
	public void setChartCode(String chartCode) {
		if (null != chartCode) {
			System.arraycopy(chartCode, 0, this.chartCode, 0,
					this.chartCode.length);
		} else {
			Arrays.fill(this.chartCode, (char) 0);
		}
	}

	/**
	 * @return Returns the documentTypeCode.
	 */
	public String getDocumentTypeCode() {
		return new String(documentTypeCode);
	}

	/**
	 * @param documentTypeCode
	 *            The documentTypeCode to set.
	 */
	public void setDocumentTypeCode(String documentTypeCode) {
		if (null != documentTypeCode) {
			System.arraycopy(documentTypeCode.toCharArray(), 0,
					this.documentTypeCode, 0, this.documentTypeCode.length);
		} else {
			Arrays.fill(this.documentTypeCode, (char) 0);
		}
	}

	/**
	 * @return Returns the entrySequenceNumber.
	 */
	public String getEntrySequenceNumber() {
		return new String(entrySequenceNumber);
	}

	/**
	 * @param entrySequenceNumber
	 *            The entrySequenceNumber to set.
	 */
	public void setEntrySequenceNumber(String entrySequenceNumber) {
		if (null != entrySequenceNumber) {
			System.arraycopy(entrySequenceNumber, 0, this.entrySequenceNumber,
					0, this.entrySequenceNumber.length);
		} else {
			Arrays.fill(this.entrySequenceNumber, (char) 0);
		}
	}

	/**
	 * @return Returns the financialDocumentNumber.
	 */
	public String getFinancialDocumentNumber() {
		return new String(financialDocumentNumber);
	}

	/**
	 * @param financialDocumentNumber
	 *            The financialDocumentNumber to set.
	 */
	public void setFinancialDocumentNumber(String financialDocumentNumber) {
		if (null != financialDocumentNumber) {
			System.arraycopy(financialDocumentNumber, 0,
					this.financialDocumentNumber, 0,
					this.financialDocumentNumber.length);
		} else {
			Arrays.fill(this.financialDocumentNumber, (char) 0);
		}
	}

	/**
	 * @return Returns the financialObjectCode.
	 */
	public String getFinancialObjectCode() {
		return new String(financialObjectCode);
	}

	/**
	 * @param financialObjectCode
	 *            The financialObjectCode to set.
	 */
	public void setFinancialObjectCode(String financialObjectCode) {
		if (null != financialObjectCode) {
			System.arraycopy(financialObjectCode, 0, this.financialObjectCode,
					0, this.financialObjectCode.length);
		} else {
			Arrays.fill(this.financialObjectCode, (char) 0);
		}
	}

	/**
	 * @return Returns the fiscalPeriodCode.
	 */
	public String getFiscalPeriodCode() {
		return new String(fiscalPeriodCode);
	}

	/**
	 * @param fiscalPeriodCode
	 *            The fiscalPeriodCode to set.
	 */
	public void setFiscalPeriodCode(String fiscalPeriodCode) {
		if (null != fiscalPeriodCode) {
			System.arraycopy(fiscalPeriodCode, 0, this.fiscalPeriodCode, 0,
					this.fiscalPeriodCode.length);
		} else {
			Arrays.fill(this.fiscalPeriodCode, (char) 0);
		}
	}

	/**
	 * @return Returns the fiscalYear.
	 */
	public String getFiscalYear() {
		return new String(fiscalYear);
	}

	/**
	 * @param fiscalYear
	 *            The fiscalYear to set.
	 */
	public void setFiscalYear(String fiscalYear) {
		if (null != fiscalYear) {
			System.arraycopy(fiscalYear, 0, this.fiscalYear, 0,
					this.fiscalYear.length);
		} else {
			Arrays.fill(this.fiscalYear, (char) 0);
		}
	}

	/**
	 * @return Returns the objectTypeCode.
	 */
	public String getObjectTypeCode() {
		return new String(objectTypeCode);
	}

	/**
	 * @param objectTypeCode
	 *            The objectTypeCode to set.
	 */
	public void setObjectTypeCode(String objectTypeCode) {
		if (null != objectTypeCode) {
			System.arraycopy(objectTypeCode, 0, this.objectTypeCode, 0,
					this.objectTypeCode.length);
		} else {
			Arrays.fill(this.objectTypeCode, (char) 0);
		}
	}

	/**
	 * @return Returns the subAccountNumber.
	 */
	public String getSubAccountNumber() {
		return new String(subAccountNumber);
	}

	/**
	 * @param subAccountNumber
	 *            The subAccountNumber to set.
	 */
	public void setSubAccountNumber(String subAccountNumber) {
		if (null != subAccountNumber) {
			System.arraycopy(subAccountNumber, 0, this.subAccountNumber, 0,
					this.subAccountNumber.length);
		} else {
			Arrays.fill(this.subAccountNumber, (char) 0);
		}
	}

	/**
	 * @return Returns the subObjectCode.
	 */
	public String getSubObjectCode() {
		return new String(subObjectCode);
	}

	/**
	 * @param subObjectCode
	 *            The subObjectCode to set.
	 */
	public void setSubObjectCode(String subObjectCode) {
		if (null != subObjectCode) {
			System.arraycopy(subObjectCode, 0, this.subObjectCode, 0,
					this.subObjectCode.length);
		} else {
			Arrays.fill(this.subObjectCode, (char) 0);
		}
	}

	/**
	 * @return Returns the systemOriginationCode.
	 */
	public String getSystemOriginationCode() {
		return new String(systemOriginationCode);
	}

	/**
	 * @param systemOriginationCode
	 *            The systemOriginationCode to set.
	 */
	public void setSystemOriginationCode(String systemOriginationCode) {
		if (null != systemOriginationCode) {
			System.arraycopy(systemOriginationCode.toCharArray(), 0,
					this.systemOriginationCode, 0,
					this.systemOriginationCode.length);
		} else {
			Arrays.fill(this.systemOriginationCode, (char) 0);
		}
	}

	public OriginEntryKey() {
		super();
	}

	final private char[] fiscalYear = new char[4];

	final private char[] chartCode = new char[2];

	final private char[] accountNumber = new char[7];

	final private char[] subAccountNumber = new char[5];

	final private char[] financialObjectCode = new char[4];

	final private char[] subObjectCode = new char[3];

	final private char[] balanceTypeCode = new char[2];

	final private char[] objectTypeCode = new char[2];

	final private char[] fiscalPeriodCode = new char[2];

	final private char[] documentTypeCode = new char[4];

	final private char[] systemOriginationCode = new char[2];

	final private char[] financialDocumentNumber = new char[9];

	final private char[] entrySequenceNumber = new char[5];
}
