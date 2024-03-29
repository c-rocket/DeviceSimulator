<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="en-us">
<head>
<title>Oracle Device Simulator</title>
<meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0" />
<meta name="description" content="Create Devices for Oracle IoT Cloud Service">
<meta name="keywords" content="Oracle IoT Cloud Service">
<meta name="author" content="Oracle">
<meta charset="UTF-8">
<!-- REMOVE THESE LATER -->
<meta http-equiv='cache-control' content='no-cache'>
<meta http-equiv='expires' content='0'>
<meta http-equiv='pragma' content='no-cache'>
<!-- REMOVE THESE LATER -->
<link rel="shortcut icon" href="<c:url value='/resources/pictures/favicon.ico'/>" type="image/x-icon" />

<!-- This is the main css file for the default Alta theme -->
<link rel="stylesheet" href="<c:url value='/resources/css/libs/oj/v2.0.0/alta/oj-alta-min.css'/>" type="text/css" />
<link rel="stylesheet" href="<c:url value='/resources/css/simulator-jet.css'/>" />

<!-- RequireJS configuration file -->
<script type="text/javascript">
    var require = {
        urlArgs : "bust="+new Date().getTime()
    };
</script>
<script data-main="<c:url value='/resources/js/main'/>" src="<c:url value='/resources/js/libs/require/require-debug.js'/>"></script>
</head>
<body>
	<input type="hidden" value="<c:url value='/'/>" name="baseUrl" id="baseUrl"/>
	<!-- template for rendering navigation items shared between nav bar and nav list -->
	<script type="text/html" id="navTemplate">
      <li><a href="#">
        <span data-bind="css: $data['iconClass']"></span>
        <!-- ko text: $data['name'] --> <!--/ko-->
      </a></li>
    </script>
	<div id="globalBody" class="oj-web-applayout-offcanvas-wrapper oj-offcanvas-outer-wrapper">
		<div class="oj-offcanvas-inner-wrapper">
			<div id="offcanvas" class="oj-contrast-marker oj-web-applayout-offcanvas oj-offcanvas-start"
				data-bind="ojModule: {viewName: 'navDrawer'}"></div>
			<div class="oj-web-applayout-scrollable-wrapper">
				<div class="oj-web-applayout-scrollable oj-web-applayout-page">
					<header role="banner" class="oj-web-applayout-header" data-bind="ojModule: 'header'"></header>
					<!-- This is where your main page content will be loaded -->
					<div class="oj-web-applayout-max-width oj-web-applayout-content">
						<div class="oj-flex">
							<div class="oj-xl-12 oj-lg-12 oj-sm-12 oj-flex-item oj-flex">
								<div id="mainContent" role="main" class="oj-panel oj-margin oj-flex-item" data-bind="ojModule: router.moduleConfig"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</body>
</html>
