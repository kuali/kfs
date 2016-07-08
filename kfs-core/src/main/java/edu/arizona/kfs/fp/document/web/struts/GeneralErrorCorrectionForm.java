package edu.arizona.kfs.fp.document.web.struts;

/**
 * This class is the UA Modification to the Struts specific form object that works in conjunction with the pojo utilities to build the UI.
 *
 * @author Adam Kost <kosta@email.arizona.edu> with some code adapted from UCI
 */
public class GeneralErrorCorrectionForm extends org.kuali.kfs.fp.document.web.struts.GeneralErrorCorrectionForm {

    private static final long serialVersionUID = 2589170663827583574L;

    private Integer universityFiscalYear;
    private String glDocId;
    private final String universityFiscalPeriodCodeLookupOverride = "*";

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }

    public String getGlDocId() {
        return glDocId;
    }

    public void setGlDocId(String glDocId) {
        this.glDocId = glDocId;
    }

    public String getUniversityFiscalPeriodCodeLookupOverride() {
        return universityFiscalPeriodCodeLookupOverride;
    }

}
