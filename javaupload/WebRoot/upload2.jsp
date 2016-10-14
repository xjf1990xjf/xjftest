<%@ page contentType="text/html;charset=gb2312"%>

<jsp:useBean id="TheBean" scope="page" class="com.FileUploadBean" />

<%
	TheBean.setSavePath("d:\\javaupdowntest\\");

	//TheBean.doUpload1(request);

	TheBean.doUpload(request);

	out.println("Filename:" + TheBean.getFilename());

	out.println("<BR>内容类型:" + TheBean.getContentType());

	out.println("<BR>作者:" + TheBean.getFieldValue("author"));

	out.println("<BR>公司:" + TheBean.getFieldValue("company"));

	out.println("<BR>说明:" + TheBean.getFieldValue("comment"));
%>

