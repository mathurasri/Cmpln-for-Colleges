<?php
//This program retrieves for each unique comment_id the number of likes
$response = array();
if (isset($_POST['comment_id']))
{				
	 $mysql_db = 'cmpln';
	 $conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 if(!$conn)
	{
		die("Could not connect.");
	}
	
	 $comment_id = $_POST['comment_id'];
	 
	 $sql = "SELECT count(*) as likesCount FROM likes WHERE comment_id = '$comment_id'";			
	 mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	//if the query returns rows
	if(!empty($retval))
	{	//if there is atleast  1 row
		if (mysql_num_rows($retval) > 0)
		{			
			$retval = mysql_fetch_array($retval);				
			$response["success"] = 1;
			$response["likes"] = $retval["likesCount"];
			$response["message"] = "Got the number of likes";						
			echo json_encode($response);			
		 }
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not get the likes";			
			echo json_encode($response);
			
	    }
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not get the likes";			
			echo json_encode($response);			
	}

	 mysql_close($conn);
}
else
{
	$response["success"] = 0;
			$response["message"] = "Required field is missin";			
			echo json_encode($response);	
}
?>