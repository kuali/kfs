<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes for this row's fields." %>

<c:set var="fullEntryMode" value="${KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="stipulationAttributes" value="${DataDictionary.PurchaseOrderVendorStipulation.attributes}" />
<c:set var="tabindexOverrideBase" value="40" />

<kul:tab tabTitle="Stipulations" defaultOpen="false" tabErrorKey="${PurapConstants.STIPULATIONS_TAB_ERRORS}">
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
