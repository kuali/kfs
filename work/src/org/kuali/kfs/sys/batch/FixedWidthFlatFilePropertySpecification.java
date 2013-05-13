/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch;


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
