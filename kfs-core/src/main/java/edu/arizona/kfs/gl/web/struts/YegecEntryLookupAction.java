package edu.arizona.kfs.gl.web.struts;

import edu.arizona.kfs.gl.businessobject.GecEntry;

public class YegecEntryLookupAction extends GecEntryLookupAction {

    @Override
    protected boolean isFiscalYearValid(GecEntry entry) {
        Integer validYegecYear = getSystemOptions().getUniversityFiscalYear() - 1;
        boolean isFiscalYearValid = validYegecYear.toString().equals(entry.getUniversityFiscalYear().toString());
        return isFiscalYearValid;
    }

}
