<?php
//This program inserts the keywords.
$response = array();

	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(! $conn)
	{
		die("Could not connect.");
	}
	
	$keywords = $_POST['keyword'];		
	foreach($keywords as &$keyword)
	{
		$sql = "INSERT INTO keywords(keyword) VALUES ('$keyword')";
	
		mysql_select_db($mysql_db) or die("Could not connect to DB.");
		$retval = mysql_query($sql, $conn);
	}
	if($retval)
	{
		$response["success"] = 1;
		$response["message"] = "User account entered";
		echo json_encode($response);
	
	}
	else
	{
		$response["success"] = 0;
		$response["message"] = "Could not insert the user account";
		echo json_encode($response);
		//echo 'Data not entered';
	}	
	mysql_close($conn);

?>