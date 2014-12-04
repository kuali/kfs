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
package org.kuali.kfs.module.tem.document.web.struts;

public interface Inquisitive<D, R> {

    D getDocument();

    String getReason();
    
    Object getQuestion();
    
    boolean denied(final String qid);

    boolean confirmed(final String qid);

    R finish() throws Exception;

    R back() throws Exception;

    R end() throws Exception;
    
    boolean wasQuestionAsked();

    R confirm(final String questionType, final String message, final boolean showReasonField, final String ... errorArgs) throws Exception;
}
