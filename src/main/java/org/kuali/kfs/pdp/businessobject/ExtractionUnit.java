/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
