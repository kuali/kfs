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
function loadCommodityCodeInfo( purCommodityCode, commodityCodeFieldName ) {
    var purchasingCommodityCode = dwr.util.getValue( purCommodityCode );
    var containerDiv = document.getElementById(commodityCodeFieldName + divSuffix);

    if (purchasingCommodityCode == "") {
        dwr.util.setValue( containerDiv.id, " " );
    } else {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == 'object' ) {
                dwr.util.setValue(containerDiv.id, data.commodityDescription, {escapeHtml:true} );
            } else {
            	setRecipientValue(containerDiv.id, wrapError("Commodity Code not found"), true);            	
            } },
            errorHandler:function( errorMessage ) { 
            	setRecipientValue(containerDiv.id, wrapError("Commodity Code not found"), true);
            }
        };
        CommodityCodeService.getByPrimaryId( purchasingCommodityCode, dwrReply );
    }
}

function loadVendorDetailInfo( vendorNumber, vendorNameFieldName ) {
    var vendorNumber = dwr.util.getValue( vendorNumber );
    var vendorNameDiv = document.getElementById(vendorNameFieldName + divSuffix);

    alert(vendorNameDiv);
    
    if (vendorNumber == "") {
        dwr.util.setValue( vendorNameDiv.id, "&nbsp;" );
    } else {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == 'object' ) {
                dwr.util.setValue(vendorNameDiv.id, data.vendorName, {escapeHtml:true} );
            } else {
                dwr.util.setValue(vendorNameDiv.id, wrapError( "vendor detail not found" ));
            } },
            errorHandler:function( errorMessage ) { 
                dwr.util.setValue(vendorNameDiv.id, wrapError( "vendor detail not found" ));
            }
        };
        
        VendorService.getByVendorNumber( vendorNumber, dwrReply );
    }
}
