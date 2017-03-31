package edu.arizona.kfs.module.ld.document;

import org.kuali.kfs.fp.document.YearEndDocument;
import org.kuali.kfs.sys.document.AccountingDocument;

public class YearEndSalaryExpenseTransferDocument extends SalaryExpenseTransferDocument implements YearEndDocument {

	private static final long serialVersionUID = 1L;

	/**
	 * Class constructor that invokes <code>SalaryExpenseTransferDocument</code>
	 * constructor.
	 */
	public YearEndSalaryExpenseTransferDocument() {
		super();
	}

	@Override
	public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
		return SalaryExpenseTransferDocument.class;
	}

}
