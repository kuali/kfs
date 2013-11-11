/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.pdp.businessobject;

/**
 * Utility class which holds the extraction unit and sub-unit of a PDP customer
 */
public class ExtractionUnit {
    private String unit;
    private String subUnit;

    /**
     * Constructs a new ExtractionUnit object with the given unit and sub-unit
     * @param unit the unit; see KFS-FP / Document / PRE_DISBURSEMENT_EXTRACT_ORGANIZATION
     * @param subUnit the sub-unit; see KFS-FP / Document / PRE_DISBURSEMENT_EXTRACT_SUB_UNIT
     */
    public ExtractionUnit(String unit, String subUnit) {
        this.unit = unit;
        this.subUnit = subUnit;
    }

    public String getUnit() {
        return unit;
    }

    public String getSubUnit() {
        return subUnit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subUnit == null) ? 0 : subUnit.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ExtractionUnit other = (ExtractionUnit) obj;
        if (subUnit == null) {
            if (other.subUnit != null) {
                return false;
            }
        }
        else if (!subUnit.equals(other.subUnit)) {
            return false;
        }
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        }
        else if (!unit.equals(other.unit)) {
            return false;
        }
        return true;
    }

}
