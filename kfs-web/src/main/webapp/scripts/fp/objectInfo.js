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

 /*
 * Given an form and an element name, returns the uppercased, trimmed value of that element
 */
function cleanupElementValue( kualiForm, name ) {
    var element = kualiForm.elements[ name ];

    if ( typeof element == 'undefined' ) {
        alert( 'undefined element "' + name + '"' );
    }

    element.value = element.value.toUpperCase().trim();

    return element.value;
}

/*
 * Clears the value of the given target.
 */
function clearTarget(kualiForm, targetBase) {
    setTargetValue(kualiForm, targetBase, '');
}


/*
 * Sets the value contained by the named div to the given value, or to a non-breaking space if the given value is empty.
 */
function setTargetValue(kualiForm, targetBase, value, isError) {
    var containerHidden = kualiForm.elements[targetBase];
    var containerName = targetBase + '.div';

    var containerDiv = document.getElementById( containerName );

    if (containerDiv) {
        if (value == '') {
			dwr.util.setValue( containerDiv.id, " " );
        } else {
        	setRecipientValue(containerDiv.id, value, isError?null:{escapeHtml:true});            	
		//	dwr.util.setValue( containerDiv.id, value, isError?null:{escapeHtml:true} );
        }
    }
    if (containerHidden) {
        containerHidden.value = value;
    }
}


function loadBankInfo(kualiForm, bankCodeFieldName, bankNameFieldName ) {
	var bankCode = cleanupElementValue( kualiForm, bankCodeFieldName );

	if (bankCode=='') {
		clearTarget(kualiForm, bankNameFieldName);
	} else {
		var dwrReply = {
			callback:function(data) {
				if ( data != null && typeof data == 'object' ) {
					setTargetValue( kualiForm, bankNameFieldName, data.bankName );
				} else {
					setTargetValue( kualiForm, bankNameFieldName, wrapError( "bank name not found" ), true );			
				}
			},
			errorHandler:function( errorMessage ) { 
				window.status = errorMessage;
				setTargetValue( kualiForm, bankNameFieldName, wrapError( "bank name not found" ), true );
			}
		};
		BankService.getByPrimaryId( bankCode, dwrReply );
	}
}
