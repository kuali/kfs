package org.kuali.kfs.sys.batch;

import org.kuali.rice.kns.service.KNSServiceLocator;

/**
 * The FixedWidthFlatFilePropertySpecification is used to
 * specify details relating to record in the given line of the input file.
 */
public class FixedWidthFlatFilePropertySpecification extends AbstractFlatFilePropertySpecificationBase {
    protected int start;
    protected int end;

    /**
     * @return the beginning index of the substring to parse
     */
    public int getStart() {
        return start;
    }

    /**
     * Sets the beginning index of the substring to parse
     * @param start the beginning index of the substring to parse
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * @return the ending index of the substring to parse; if empty, line will be parsed to end of String
     */
    public int getEnd() {
        return end;
    }

    /**
     * Sets the ending index of the substring to parse; if not set, line will be parsed to end of String
     * @param end the ending index of the substring to parse
     */
    public void setEnd(int end) {
        this.end = end;
    }
}
