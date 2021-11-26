<head>
    <meta http-equiv="X-UA-Compatible" content="IE=10" />
</head>
<%@ include file="../../../wtcore/jsp/ext/ipe/ecaddoc/edit.jsp"%>
<script>
    window.onunload = refreshParent;
    function refreshParent() {
       // window.opener.location.reload();
	    window.opener.history.go(-1);
    }
</script>