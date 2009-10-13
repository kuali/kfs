<%--
 Copyright 2007-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:tab tabTitle="Foreign Draft" defaultOpen="false" tabErrorKey="${KFSConstants.DV_FOREIGNDRAFTS_TAB_ERRORS}">
	<c:set var="wireTransAttributes" value="${DataDictionary.DisbursementVoucherWireTransfer.attributes}" />
    <div class="tab-container" align=center>
      <h3>Foreign Draft</h3>
              <table class="datatable" summary="Foreign Draft Section" cellpadding="0">
                <tbody>
                  <tr>
                    <td><div class="floaters" >
                       <strong>
                            <c:if test="${!fullEntryMode&&!frnEntryMode}">
                              <c:if test="${KualiForm.document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeCode=='C'}">
                                DV amount is stated in U.S. dollars; convert to foreign currency
                              </c:if>  
                              <c:if test="${KualiForm.document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeCode=='F'}">
                                DV amount is stated in foreign currency
                              </c:if> 
                            </c:if>
                           
                            <c:if test="${fullEntryMode||frnEntryMode}">
                            <html:radio property="document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeCode" value="C"/>
                            DV amount is stated in U.S. dollars; convert to foreign currency </label>
                            <br/>
                            <br/>
                            <html:radio property="document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeCode" value="F"/>
                            DV amount is stated in foreign currency <br/>
                           </c:if>
                           
                        <br/>
                        <kul:htmlAttributeLabel attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeName}"/>&nbsp;
                        <kul:htmlControlAttribute attributeEntry="${wireTransAttributes.disbVchrCurrencyTypeName}" property="document.dvWireTransfer.disbursementVoucherForeignCurrencyTypeName" readOnly="${!fullEntryMode&&!frnEntryMode}"/>
                      </strong></div></td>
                  </tr>
                </tbody>
              </table>   
        </div>
</kul:tab>
