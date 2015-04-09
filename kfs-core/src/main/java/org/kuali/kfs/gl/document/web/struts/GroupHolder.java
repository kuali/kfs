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
package org.kuali.kfs.gl.document.web.struts;

import java.io.Serializable;

import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;

/**
 * This represents a Group Holder which contains correction change and criteria objects
 */
public class GroupHolder implements Serializable {
    private CorrectionChange correctionChange = new CorrectionChange();
    private CorrectionCriteria correctionCriteria = new CorrectionCriteria();

    public CorrectionChange getCorrectionChange() {
        return correctionChange;
    }

    public void setCorrectionChange(CorrectionChange correctionChange) {
        this.correctionChange = correctionChange;
    }

    public CorrectionCriteria getCorrectionCriteria() {
        return correctionCriteria;
    }

    public void setCorrectionCriteria(CorrectionCriteria correctionCriteria) {
        this.correctionCriteria = correctionCriteria;
    }
}
