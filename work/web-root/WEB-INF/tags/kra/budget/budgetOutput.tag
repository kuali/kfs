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
<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html" %>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/tlds/fmt.tld" prefix="fmt" %>

<%@ taglib tagdir="/WEB-INF/tags" prefix="kul" %>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd" %>
<%@ taglib tagdir="/WEB-INF/tags/kra" prefix="kra" %>
<%@ taglib tagdir="/WEB-INF/tags/kra/budget" prefix="kra-b" %>

<div align="right">
	<kul:help documentTypeName="${DataDictionary.KualiBudgetDocument.documentTypeName}" pageName="${KraConstants.OUTPUT_HEADER_TAB}" altText="page help"/>
</div>

<table width="100%" border="0" cellpadding="0" cellspacing="0" class="t3" summary="">

        <tbody>
          <tr>
            <td ><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tl3"></td>
            <td align="right"><img src="images/pixel_clear.gif" alt="" width="12" height="12" class="tr3"></td>
          </tr>
        </tbody>
      </table>

<div id="workarea" >

        <div class="tab-container-error">
        	<div class="left-errmsg-tab">
				<kul:errors keyMatch="currentOutputReportType" />
				<kul:errors keyMatch="currentOutputDetailLevel" />
				<kul:errors keyMatch="currentOutputAgencyType" />
				<kul:errors keyMatch="currentOutputAgencyPeriod" />
			</div>
        </div>

        <div class="tab-container"  align="center">
           <kra-b:budgetOutputSelection />
        </div>
        <table width="100%" border="0" cellpadding="0" cellspacing="0" class="b3" summary="">
          <tr>
            <td align="left" class="footer"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="bl3"></td>
            <td align="right" class="footer-right"><img src="images/pixel_clear.gif" alt="" width="12" height="14" class="br3"></td>
          </tr>
        </table>
      </div>
      <div id="globalbuttons" class="globalbuttons">
        <html:image src="images/buttonsmall_genpdf.gif" styleClass="globalbuttons" property="methodToCall.pdfOutput" alt="Copy current document" onclick="excludeSubmitRestriction=true"/>
      </div>
<div class="exportlinks">Export options: 
<a href="researchBudgetOutput.do?document.documentNumber=${KualiForm.document.documentNumber}&methodToCall=xmlOutput"><span class="export xml">XML</span></a></div>
