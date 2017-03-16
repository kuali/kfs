package edu.arizona.kfs.gl.web.struts;

import org.kuali.kfs.gl.businessobject.Entry;

public class YegecEntryLookupAction extends GecEntryLookupAction {

    @Override
    protected boolean isFiscalYearValid(Entry entry) {
        Integer validYegecYear = getSystemOptions().getUniversityFiscalYear() - 1;
        boolean isFiscalYearValid = validYegecYear.toString().equals(entry.getUniversityFiscalYear().toString());
        return isFiscalYearValid;
    }

}
