<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
	<title>Home</title>
</head>
<body>

<form:form method="POST" commandName="homeModel" > <!-- action="" action="edit-currency" action="addcurrency"  -->

	<h2  align="center">Exchange rates</h2>

	<form:input size="80" path="errorMessage" cssStyle="color: #ff0000;"/>

	<fieldset>
		<form:label path="currency.code">To add:</form:label>
		<form:select path="currency.code">
			<form:options items="${availableCurrenciesSet}" />
		</form:select>

		<form:label path="currency.description">Description:</form:label>
		<form:input path="currency.description"/>

		<input type="submit" value="Add" name = "action_1" tabindex="3"  >
	</fieldset>
	
	<fieldset>
		<form:label path="currencyEdit.code">To edit:</form:label>
		<form:select path="currencyEdit.code">
			<form:options items="${userCurrenciesCodes}" />
		</form:select>
		<form:label path="currencyEdit.description">Description:</form:label>
		<form:input path="currencyEdit.description"/>
		<input type="submit" value="Show description" name = "action_3" tabindex="4" >
		<input type="submit" value="Save" name = "action_4" tabindex="5" >
		<input type="submit" value="Delete" name = "action_2" tabindex="2" >
	</fieldset>
	
	<h3>User currencies list:</h3>
	<c:if test="${not empty currencies}">
		<ul>
			<c:forEach var="entry" items="${currencies}">
				<li>${entry.getKey()}: ${entry.getValue()}</li>
			</c:forEach>		
		</ul>
	</c:if>

	<fieldset>
		<form:label path="dateFrom">From date:</form:label>
		<form:input path="dateFrom" />
		<form:errors path="dateFrom" cssStyle="color: #ff0000;"/>

		<form:label path="dateTo">To date:</form:label>
		<form:input path="dateTo"/>
		<form:errors path="dateTo" cssStyle="color: #ff0000;" />
	</fieldset>

	<fieldset>
		<form:label path="selectedUserCurrency">User currency:</form:label>
		<form:select path="selectedUserCurrency">
			<form:options items="${userCurrenciesCodes}" />
		</form:select>
		<form:label path="selectedBaseCurrency">Base currency:</form:label>
		<form:select path="selectedBaseCurrency">
			<form:options items="${baseCurrencies}" />
		</form:select>

		<input type="submit" value="Request Rate" name = "action_5" tabindex="5">
	</fieldset>
	
	<h3>Exchange rate for the period:</h3>
	<c:if test="${not empty rates}">
		<ul>
			<c:forEach var="rate" items="${rates}">
				<li>${rate}</li>
			</c:forEach>		
		</ul>
	</c:if>
</form:form>
	
</body>
</html>
