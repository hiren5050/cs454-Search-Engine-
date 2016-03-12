<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>Search Home</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<link rel="stylesheet"
	href="//code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<script src="//code.jquery.com/jquery-1.10.2.js"></script>
<script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
<script>
	$(function() {
		var asdf = ${terms};
		console.log(JSON.parse(asdf));
		var availableTags = [ "ActionScript", "AppleScript", "Asp", "BASIC",
				"C", "C++", "Clojure", "COBOL", "ColdFusion", "Erlang",
				"Fortran", "Groovy", "Haskell", "Java", "JavaScript", "Lisp",
				"Perl", "PHP", "Python", "Ruby", "Scala", "Scheme" ];
		$("#search").autocomplete({
			source : availableTags
		});
	});
</script>

</head>
<body>
	<h2>CS454 Search Engine</h2>
	<div class="row">
		<div class="col-md-4 col-md-offset-3">
			<form action="home.html" method="post" class="search-form">
				<div class="form-group has-feedback">
					<label for="search" class="sr-only">Search</label> <input
						type="text" class="form-control" name="txtsearch" id="search"
						placeholder="search"> <span
						class="glyphicon glyphicon-search form-control-feedback"></span>
				</div>
				<input type="submit" value="search" class="btn btn-primary">
			</form>
			<table class="table">
				<thead>
					<th>Link</th>
					<th>Rank</th>
				</thead>
				<c:forEach items="${map}" var="map">

					<tbody>
						<tr>
							<td><a href="file:///Users/Chavda/CS454/indexingranking/filesToIndex/wiki-small/en/articles/${map.key}"> ${map.key}</a></td>
							<td>${map.value}</td>
						</tr>

					</tbody>
				</c:forEach>
			</table>

		</div>

	</div>

</body>
</html>
