<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%--
This is displayed by the complexExample() method in ExampleAction.

This demonstrates how CamelCased method names are converted to web-friendly
URLs.
--%>

<html>
<body>
<p>This is a very simple complex example.</p>
<p>Name: <c:out value="${name}" /><br />
ID: ${id}</p>
<p>Greeting: <c:out value="${ComplexForm.map.greeting}" /></p>
</body>
</html>