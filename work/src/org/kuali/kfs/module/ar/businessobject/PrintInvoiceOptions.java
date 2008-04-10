package org.kuali.module.ar.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.Inactivateable;
import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */

public class PrintInvoiceOptions extends PersistableBusinessObjectBase implements Inactivateable {

	private String printInvoiceIndicator;
	private String printInvoiceDescription;
	private boolean active;

	/**
	 * Default constructor.
	 */
	public PrintInvoiceOptions() {

	}

	/**
	 * Gets the printInvoiceIndicator attribute.
	 * 
	 * @return Returns the printInvoiceIndicator
	 * 
	 */
	public String getPrintInvoiceIndicator() { 
		return printInvoiceIndicator;
	}

	/**
	 * Sets the printInvoiceIndicator attribute.
	 * 
	 * @param printInvoiceIndicator The printInvoiceIndicator to set.
	 * 
	 */
	public void setPrintInvoiceIndicator(String printInvoiceIndicator) {
		this.printInvoiceIndicator = printInvoiceIndicator;
	}


	/**
	 * Gets the printInvoiceDescription attribute.
	 * 
	 * @return Returns the printInvoiceDescription
	 * 
	 */
	public String getPrintInvoiceDescription() { 
		return printInvoiceDescription;
	}

	/**
	 * Sets the printInvoiceDescription attribute.
	 * 
	 * @param printInvoiceDescription The printInvoiceDescription to set.
	 * 
	 */
	public void setPrintInvoiceDescription(String printInvoiceDescription) {
		this.printInvoiceDescription = printInvoiceDescription;
	}


	/**
	 * Gets the active attribute.
	 * 
	 * @return Returns the active
	 * 
	 */
	public boolean isActive() { 
		return active;
	}

	/**
	 * Sets the active attribute.
	 * 
	 * @param active The active to set.
	 * 
	 */
	public void setActive(boolean active) {
		this.active = active;
	}


	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        m.put("printInvoiceIndicator", this.printInvoiceIndicator);
	    return m;
    }
}
