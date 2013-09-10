<%--
    Copyright (C) 2006-2011 Serotonin Software Technologies Inc. All rights reserved.
    @author Matthew Lohbihler
--%>
<%@ include file="/WEB-INF/jsp/include/tech.jsp" %>

<tag:page >

<!-- Errors for CSV Uploader -->
<table>
      <c:if test="${!empty error}">
        <tr><td colspan="2" class="formError">${error}</td></tr>
      </c:if>
      <c:if test="${!empty result}">
        <tr><td colspan="2" class="formError">${result}</td></tr>
      </c:if>
</table>


 <div id="upload-type-choice">
 </div>


 <div id="csv-upload" style="display:none" >
  <form action="dataImport.shtm" method="post" enctype="multipart/form-data">
    <table>
      <tr>
        <td class="formLabelRequired"><fmt:message key="dataImport.importFile"/></td>
        <td><input type="file" name="uploadFile"/></td>
      </tr>      
      <tr>
        <td colspan="2" align="center">
          <input type="submit" value="<fmt:message key="dataImport.upload"/>" name="upload"/>
        </td>
      </tr>
    </table>
  </form>
  </div>
  
  <div id="xslx-upload">
    <jsp:include page="/WEB-INF/snippet/view/pointValue/pointValueEmport.jsp"/>
  </div>
  
  <script type="text/javascript" src="${modulePath}/web/js/dataImport.js"></script>
  
  
</tag:page>