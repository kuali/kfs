/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
function loadCommodityCodeInfo( purCommodityCode, commodityCodeFieldName ) {
    var purchasingCommodityCode = DWRUtil.getValue( purCommodityCode );
    var containerDiv = document.getElementById(commodityCodeFieldName + divSuffix);

    if (purchasingCommodityCode == "") {
        DWRUtil.setValue( containerDiv.id, "&nbsp;" );
    } else {
        var dwrReply = {
            callback:function(data) {
            if ( data != null && typeof data == 'object' ) {
                DWRUtil.setValue(containerDiv.id, data.commodityDescription, {escapeHtml:true} );
            } else {
                DWRUtil.setValue(containerDiv.id, wrapError( "commodity code not found" ));
            } },
            errorHandler:function( errorMessage ) { 
                DWRUtil.setValue(containerDiv.id, wrapError( "commodity code not found" ));
            }
        };
        CommodityCodeService.getByPrimaryId( purchasingCommodityCode, dwrReply );
    }
}
