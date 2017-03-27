package edu.arizona.kfs.module.ld.util;

import edu.arizona.kfs.module.ld.LaborConstants;

public class EreSweepBalanceHelper {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EreSweepBalanceHelper.class);
	
	private String accountNumber;
	private String subAccountNumber = LaborConstants.EMPTY_SUB_ACCOUNT_STRING;
	private String financialSubObjectCode = LaborConstants.EMPTY_SUB_OBJECT_STRING;
	private String finObjectCode;
	private String employeeId;
	private String positionNbr;
	
	public EreSweepBalanceHelper(Object[] rowData) {
		for (int i=0; i < rowData.length; ++i) {
			if (rowData[i] != null) {
				switch(i) {
				case 0:
					this.accountNumber = rowData[0].toString();
					break;
				case 1:
					this.finObjectCode = rowData[1].toString();
					break;
				case 2:
					this.employeeId = rowData[2].toString();
					break;
				case 3:
					this.positionNbr = rowData[3].toString();
					break;
				case 4:
					this.subAccountNumber = rowData[4].toString();
					break;
				case 5:
					this.financialSubObjectCode = rowData[5].toString();
					break;
				default:
					LOG.warn("No position defined");
					break;
				}
			}
		}
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getSubAccountNumber() {
		return subAccountNumber;
	}

	public void setSubAccountNumber(String subAccountNumber) {
		this.subAccountNumber = subAccountNumber;
	}

	public String getFinancialSubObjectCode() {
		return financialSubObjectCode;
	}

	public void setFinancialSubObjectCode(String financialSubObjectCode) {
		this.financialSubObjectCode = financialSubObjectCode;
	}

	public String getFinObjectCode() {
		return finObjectCode;
	}

	public void setFinObjectCode(String finObjectCode) {
		this.finObjectCode = finObjectCode;
	}

	public String getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(String employeeId) {
		this.employeeId = employeeId;
	}

	public String getPositionNbr() {
		return positionNbr;
	}

	public void setPositionNbr(String positionNbr) {
		this.positionNbr = positionNbr;
	}

}
