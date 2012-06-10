/*
 * Copyright 2007-2008 The Kuali Foundation
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
function loadItemUnitOfMeasureInfo( itemUnitOfMeasureCodeField, itemUnitOfMeasureDescriptionField ) {
    var itemUnitOfMeasureCode = dwr.util.getValue( itemUnitOfMeasureCodeField );
    var containerDiv = document.getElementById(itemUnitOfMeasureDescriptionField + divSuffix);

    if (itemUnitOfMeasureCode == "") {
        dwr.util.setValue( containerDiv.id, " " );
    } else {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == 'object' ) {
                dwr.util.setValue(containerDiv.id, data.itemUnitOfMeasureDescription, {escapeHtml:true} );
            } else {
            	setRecipientValue(containerDiv.id, wrapError("Item of Measure code not found"), true);            	
            } },
            errorHandler:function( errorMessage ) { 
            	setRecipientValue(containerDiv.id, wrapError("Item of Measure code not found"), true);             	
            }
        };
        ItemUnitOfMeasureService.getByPrimaryId( itemUnitOfMeasureCode, dwrReply );
    }
}

function loadCommodityCodeDescription( purCommodityCode, commodityCodeFieldName ) {
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

