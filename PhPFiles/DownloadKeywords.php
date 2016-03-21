<?php
//This program downloads the keywords(#tagged words)
$response = array();

	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	$sql = "SELECT * FROM keywords";
	
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{
			$response["keywords"] = array();
			while($row = mysql_fetch_array($retval))
			{
				$keyword = array();
				$keyword["keyword"] = $row["keyword"];				
				array_push($response["keywords"], $keyword);
			}
			$response["success"] = 1;
			echo json_encode($response);
			
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not download user image";			
			echo json_encode($response);
			
		}		
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not download the image";			
			echo json_encode($response);
			
	}

	mysql_close($conn);

?>