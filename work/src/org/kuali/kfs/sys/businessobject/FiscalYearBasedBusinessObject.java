/*
 * Copyright 2011 The Regents of the University of California.
 */
package org.kuali.kfs.sys.businessobject;

import org.kuali.rice.kns.bo.PersistableBusinessObject;

public interface FiscalYearBasedBusinessObject extends PersistableBusinessObject {

    Integer getUniversityFiscalYear();
    void setUniversityFiscalYear( Integer fiscalYear );
}
