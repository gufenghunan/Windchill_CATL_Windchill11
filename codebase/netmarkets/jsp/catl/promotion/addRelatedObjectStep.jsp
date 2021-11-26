<%@page language="java" session="true" pageEncoding="UTF-8"%>
<%@page import="java.util.Locale"%>
<%@page import="wt.util.WTMessage"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="wt.session.SessionHelper"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://www.ptc.com/windchill/taglib/core" prefix="wc"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="mvc"  uri="http://www.ptc.com/windchill/taglib/mvc"%>
<%@ include file="/netmarkets/jsp/util/begin.jspf"%>
<fmt:setLocale value="${localeBean.locale}" />

<b>点击确定按钮启动流程，请在我的任务中查看名为“无有效上层BOM的物料报表结果”来下载报表结果。</b>

<%@ include file="/netmarkets/jsp/util/end.jspf"%>