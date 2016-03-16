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
package edu.arizona.kfs.module.purap.document.authorization;

import java.util.Set;
import edu.arizona.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.rice.krad.document.Document;


public class PaymentRequestDocumentPresentationController extends org.kuali.kfs.module.purap.document.authorization.PaymentRequestDocumentPresentationController {

	@Override 
	public Set<String> getEditModes(Document document) { 
		Set<String> editModes = super.getEditModes(document); 
		
		//Remove ability for AP Specialist to EDIT PREQ vendor address. 
		editModes.add(PurapAuthorizationConstants.PaymentRequestEditMode.EDIT_VENDOR_ADDR_EDIT_MODE);
		
		return editModes; 
	} 
}
