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
package org.kuali.kfs.module.purap.util.cxml;

import org.apache.commons.lang.builder.ToStringBuilder;

public class PunchOutSetupResponse extends B2BShoppingCartBase {

    private String punchOutUrl = null;

    public String getPunchOutUrl() {
        if (isSuccess()) {
            return punchOutUrl;
        }
        else {
            return null;
        }
    }

    public void setPunchOutUrl(String punchOutUrl) {
        this.punchOutUrl = punchOutUrl;
    }

    public String toString() {

        ToStringBuilder toString = new ToStringBuilder(this);
        toString.append("StatusCode", getStatusCode());
        toString.append("StatusText", getStatusText());
        toString.append("isSuccess", isSuccess());
        toString.append("punchOutUrl", getPunchOutUrl());

        return toString.toString();
    }

}
