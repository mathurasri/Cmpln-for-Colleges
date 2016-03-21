<?php
//This program gets the number of replies for each unique commentid.
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
	 $sql = "SELECT count(*) as repliesCount FROM replies WHERE comment_id = '$comment_id'";				
	 mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	//if the query returns atleast one row
	if(!empty($retval))
	{	
		//if the query returns atleast one row
		if (mysql_num_rows($retval) > 0)
		{			
			$retval = mysql_fetch_array($retval);				
			$response["success"] = 1;
			$response["replies"] = $retval["repliesCount"];
			$response["message"] = "Got the number of replies";					
			echo json_encode($response);			
		 }
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not get the replies";			
			echo json_encode($response);			
	    }
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not get the replies";			
			echo json_encode($response);			
	}

	 mysql_close($conn);
}
else
{
	$response["success"] = 0;
			$response["message"] = "Required fields are missing";			
			echo json_encode($response);	
}
?>