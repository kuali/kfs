<%--
 Copyright 2007 The Kuali Foundation.
 
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

<kul:tabTop tabTitle="Payee Initiation" defaultOpen="true" tabErrorKey="${KFSConstants.DV_PAYEE_INIT_TAB_ERRORS}">
	<c:set var="payeeAttributes" value="${DataDictionary.DisbursementVoucherPayeeDetail.attributes}" />

    <div class="tab-container" align=center > 
    <div class="h2-container">
        <h2>Select Payee Type</h2>
    </div>
	<table cellpadding=0 class="datatable" summary="Payee Section">
			<tr>
				<th align=right valign=middle class="bord-l-b">Select type of payee: </th>
				<td align=left valign=middle class="datacell">
	                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbursementVoucherPayeeTypeCode}" property="payeeTypeCode" readOnly="false"/>
				</td>
			</tr>
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeeIdNumber}" property="payeeIdNumber" readOnly="true" />
                  <kul:lookup boClassName="org.kuali.kfs.vnd.businessobject.VendorDetail" fieldConversions="vendorNumber:payeeIdNumber,vendorName:payeePersonName"/>
              </td>
            </tr>
            <tr>
              <th align=right valign=middle class="bord-l-b"><div align="right"><kul:htmlAttributeLabel attributeEntry="${payeeAttributes.disbVchrPayeePersonName}"/></div></th>
              <td align=left valign=middle class="datacell">
                <kul:htmlControlAttribute attributeEntry="${payeeAttributes.disbVchrPayeePersonName}" property="payeePersonName" readOnly="true"/>  
              </td>
            </tr>
	</table>
     </div>
</kul:tabTop>
