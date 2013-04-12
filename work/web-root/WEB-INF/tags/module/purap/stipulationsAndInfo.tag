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

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="stipulationAttributes" value="${DataDictionary.PurchaseOrderVendorStipulation.attributes}" />
<c:set var="tabindexOverrideBase" value="40" />
<c:set var="tabEdited" value="${(not empty KualiForm.editingMode['stipulationTabEdited'])}" />

<kul:tab tabTitle="Stipulations" defaultOpen="false" highlightTab="${tabEdited}" tabErrorKey="${PurapConstants.STIPULATIONS_TAB_ERRORS}">
    <div class="tab-container" align=center>
            <h3>Vendor Stipulations and Information</h3>
        <table cellpadding="0" cellspacing="0" class="datatable" summary="Stipulations & Info Section">
            <tr>
                <kul:htmlAttributeHeaderCell literalLabel="&nbsp;" scope="col" align="left"/>
                <kul:htmlAttributeHeaderCell attributeEntry="${stipulationAttributes.vendorStipulationDescription}" />
                <kul:htmlAttributeHeaderCell literalLabel="Actions" scope="col"/>
            </tr>
            <c:if test="${(fullEntryMode or (!empty KualiForm.editingMode['amendmentEntry']))}" >
                <tr>
                    <kul:htmlAttributeHeaderCell literalLabel="add:" scope="row"/>
                    <td class="infoline">
                        <kul:htmlControlAttribute 
                            attributeEntry="${stipulationAttributes.vendorStipulationDescription}" 
                            property="newPurchaseOrderVendorStipulationLine.vendorStipulationDescription" 
                            tabindexOverride="${tabindexOverrideBase + 0}"/>
                        <kul:lookup boClassName="org.kuali.kfs.module.purap.businessobject.VendorStipulation" 
                        	readOnlyFields="active" lookupParameters="'Y':active"
                        	fieldConversions="vendorStipulationDescription:document.vendorStipulationDescription" /></div>
                    </td>
                    <td class="infoline">
                		<div align="center"><html:image property="methodToCall.addStipulation" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-add1.gif" alt="Insert a Stipulation" title="Add a Stipulation" styleClass="tinybutton"/></div>
    				</td>
                </tr>
            </c:if>
        	<logic:notEmpty name="KualiForm" property="document.purchaseOrderVendorStipulations">
	 			<logic:iterate name="KualiForm" id="stipulation" property="document.purchaseOrderVendorStipulations" indexId="ctr">
                    <tr>
		                <td class="infoline">&nbsp;</td>
		                <td align=center valign=middle class="datacell">
		                    <kul:htmlControlAttribute 
		                        attributeEntry="${stipulationAttributes.vendorStipulationDescription}" 
		                        property="document.purchaseOrderVendorStipulation[${ctr}].vendorStipulationDescription" 
		                        readOnly="${not (fullEntryMode or (!empty KualiForm.editingMode['amendmentEntry']))}" 
		                        tabindexOverride="${tabindexOverrideBase + 0}" />
		                </td>
		                <c:if test="${(fullEntryMode or (!empty KualiForm.editingMode['amendmentEntry']))}" >		                
		                	<td class="infoline"><div align="center"><html:image property="methodToCall.deleteStipulation.line${ctr}" src="${ConfigProperties.kr.externalizable.images.url}tinybutton-delete1.gif" alt="Delete Stipulation ${ctr+1}" title="Delete Stipulation ${ctr+1}" styleClass="tinybutton"/></div></td>
						</c:if>
		            </tr>
	        	</logic:iterate>
	        </logic:notEmpty>
        </table>
    </div>
</kul:tab>
