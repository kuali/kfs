<%--
 Copyright 2005-2006 The Kuali Foundation.
 
 $Source: /opt/cvs/kfs/work/web-root/WEB-INF/tags/kra/routingform/routingFormProjectDetailsSubcontracts.tag,v $
 
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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>
<%@ taglib uri="/tlds/fn.tld" prefix="fn" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="docHeaderAttributes" value="${DataDictionary.DocumentHeader.attributes}" />

<dd:evalNameToMap mapName="DataDictionary.${KualiForm.docTypeName}.attributes" returnVar="documentAttributes"/>

<kul:tab tabTitle="Subcontracts" defaultOpen="true" tabErrorKey="${Constants.DOCUMENT_ERRORS}" >

          <div class="tab-container" align="center">
            <div class="h2-container"> <span class="subhead-left">
              <h2>Subcontracts</h2>

              </span> </div>
            <table cellpadding=0 cellspacing="0"  summary="">
              <tr>
                <th width="50">&nbsp;</th>
                <th> <div align="center">Subcontract Name </div></th>
                <th ><div align="right">Amount</div></th>
                <th >Action</th>

              </tr>
              <tr>
                <th scope="row">add:</th>
                <td class="infoline"><div align="center">
                    <input name="textfield" type="text" size="20">
                    <a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td class="infoline"><div align="right">
                    <input name="textfield" type="text" size="12" class="right">

                  </div></td>
                <td class="infoline"><div align=center><a href="ib-multi09.html"><img src="images/tinybutton-add1.gif" alt="add" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th scope="row"><div align="center">1</div></th>
                <td><div align="center"><span class="infoline">
                    <input name="textfield" type="text" value="ACME Subcontract 1" size="20">
                    </span><a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>

                <td><div align="right"><span class="infoline">
                    <input name="textfield" type="text" value="600.00" size="12" class="right">
                    </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>
                <th  scope="row"><div align="center">2</div></th>
                <td><div align="center"><span class="infoline">

                    <input name="textfield" type="text" value="ACME Subcontract 2" size="20">
                    </span><a href="lookups/lookup-param1.html"><img src="images/searchicon.gif" alt="search" width=16 height=16 border=0 align="absmiddle"></a></div></td>
                <td><div align="right"><span class="infoline">
                    <input name="textfield" type="text" value="400.00" size="12" class="right">
                    </span></div></td>
                <td><div align=center> <a href="ib10c.html"><img src="images/tinybutton-delete1.gif" alt="delete" width=40 height=15 hspace=3 vspace=3 border=0></a></div></td>
              </tr>
              <tr>

                <td colspan="2" class="total-line"  scope="row">&nbsp;</td>
                <td class="total-line">$1000.00</td>
                <td class="total-line">&nbsp;</td>
              </tr>
            </table>
          </div>

</kul:tab>
