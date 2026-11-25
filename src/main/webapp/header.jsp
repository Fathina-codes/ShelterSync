<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>ShelterSync</title>
  <link rel="stylesheet" href="css/style.css">
</head>
<body>
  <div class="navbar">
    <a class="brand" href="dashboard.jsp">🏠 ShelterSync</a>
    <a class="link ${param.active == 'home' ? 'active' : ''}" href="dashboard.jsp">Home</a>
    <a class="link ${param.active == 'shelters' ? 'active' : ''}" href="shelters">Shelters</a>
    <a class="link ${param.active == 'persons' ? 'active' : ''}" href="persons">Affected People</a>
    <a class="link ${param.active == 'resources' ? 'active' : ''}" href="resources">Resources</a>
    <a class="link ${param.active == 'requests' ? 'active' : ''}" href="requests">Requests</a>
    <a class="link ${param.active == 'distribution' ? 'active' : ''}" href="distribution">Distribution</a>
  </div>
  <div class="container">