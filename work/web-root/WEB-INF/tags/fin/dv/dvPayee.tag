<%--
 Copyright 2006 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<kul:tab tabTitle="Payee Information" defaultOpen="true" tabErrorKey="${KFSConstants.DV_PAYEE_TAB_ERRORS},document.dvPayeeDetail.disbursementVoucherPayeeTypeCode">
	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />
    <div class="tab-container" align=center > 
    <div class="h2-container">
                <h2>Payee Information</h2>
              </div>
    <script language="JavaScript" src="dwr/interface/PayeeService.js" type="text/javascript"></script>
    <script language="JavaScript" type="text/javascript">

    	var dwrReply = getDwrReply();
    	function getDwrReply() {
    		return { callback:function(data) {
    			if(data) {
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeePersonName').value=data.payeePersonName
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeeLine1Addr').value=data.payeeLine1Addr
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeeLine2Addr').value=data.payeeLine2Addr
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeeCityName').value=data.payeeCityName
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeeStateCode').value=data.payeeStateCode
	    			document.getElementById('document.dvPayeeDetail.disbVchrPayeeZipCode').value=data.payeeZipCode
	    			var ddown = document.getElementsByName('document.dvPayeeDetail.disbVchrPayeeCountryCode')[0];
	    			if(ddown) {
		    			for (var i=0; i<ddown.options.length; i++) {
		    				if (ddown.options[i].value == data.payeeCountryCode) {
		    					ddown.options[i].selected=true
		    				}
		    			}
	    			}
    			}
    		},
    		errorHandler:function(errorMessage) {
    			alert(errorMessage)
    		}};
    	}

    	function addLoadEvent(func) {
    		var oldonload = window.onload;
    		if(typeof window.onload != 'function') {
    			window.onload = func;
    		} else {
    			window.onload = function() {
    				if(oldonload) {
    					oldonload();
    				}
    				func();
    			}
    		}
   		}
   		
   		function fillInDv() {
   			
   			var dvpn = document.getElementById('document.dvPayeeDetail.disbVchrPayeeIdNumber');
	    	dwrReply = getDwrReply();
   			if(dvpn) {
				dvpn = dvpn.value
				if(dvpn != '') {
					PayeeService.getByPayeeIdNumber(dvpn, dwrReply);
				}
   			}
   		}
   		
   		addLoadEvent(fillInDv);
   		
    </script>
	<table cellpadding=0 class="datatable" summary="Payee Section">
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}" property="document.dvPayeeDetail.disbVchrPayeeIdNumber" onblur="fillInDv()" />
                <c:if test="${fullEntryMode}">
                  <kul:lookup boClassName="org.kuali.module.financial.bo.Payee" fieldConversions="payeeIdNumber:document.dvPayeeDetail.disbVchrPayeeIdNumber"/>
                </c:if>
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbursementVoucherPayeeTypeCode}" property="document.dvPayeeDetail.disbursementVoucherPayeeTypeCode" extraReadOnlyProperty="document.dvPayeeDetail.disbursementVoucherPayeeTypeName" readOnly="${!fullEntryMode}"/>
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeCityName}"/>
                  <c:if test="${fullEntryMode}">
                      <br> *required for US
                  </c:if>
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCityName}" property="document.dvPayeeDetail.disbVchrPayeeCityName" readOnly="${!fullEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeePersonName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeePersonName}" property="document.dvPayeeDetail.disbVchrPayeePersonName" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeStateCode}"/>
                  <c:if test="${fullEntryMode}">
                      <br> *required for US
                  </c:if>
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeStateCode}" property="document.dvPayeeDetail.disbVchrPayeeStateCode" readOnly="${!fullEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeLine1Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine1Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine1Addr" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeZipCode}"/>
                  <c:if test="${fullEntryMode}">
                      <br> *required for US
                  </c:if> 
              </div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeZipCode}" property="document.dvPayeeDetail.disbVchrPayeeZipCode" readOnly="${!fullEntryMode}"/>
              </td>
            </tr>
            
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeLine2Addr}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeLine2Addr}" property="document.dvPayeeDetail.disbVchrPayeeLine2Addr" readOnly="${!fullEntryMode}"/>  
              </td>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeCountryCode}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeCountryCode}" property="document.dvPayeeDetail.disbVchrPayeeCountryCode" readOnly="${!fullEntryMode}"/>  
              </td>
            </tr>
     </table>
     </div>
</kul:tab>
