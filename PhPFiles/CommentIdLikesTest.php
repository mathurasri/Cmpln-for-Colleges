<?php
$response = array();
//if (isset($_POST['comment_id']))
//{				
	 $mysql_db = 'cmpln';
	 $conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 if(!$conn)
	{
		die("Could not connect.");
	}
	//$response["imageUri"] = "No image present";
	 //mail = $_POST['email'];
	 $comment_id = "9a96fb48-fbf5-430e-a390-27816e6778b6";
	 //$username = "VillanovaStudent";
	 $sql = "SELECT count(*) as likesCount FROM likes WHERE comment_id = '$comment_id'";	
		//$sql = "SELECT * FROM UserProfile";	
	 mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		//echo "inside not empty";
		if (mysql_num_rows($retval) > 0)
		{
			//echo "numrows > 0";
			$retval = mysql_fetch_array($retval);	
			// //echo $retval["imagePath"];
			$response["success"] = 1;
			$response["likes"] = $retval["likesCount"];
			$response["message"] = "Got the number of likes";			
			// //$response["imageUri"] = "Test";
			echo json_encode($response);			
		 }
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not get the likes";			
			echo json_encode($response);
			// //echo "No rows found";
	    }
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not get the likes";			
			echo json_encode($response);
			//echo "No rows found";
	}

	 mysql_close($conn);
//}
/*else
{
	$response["success"] = 0;
			$response["message"] = "Could not get the likes";			
			echo json_encode($response);	
}*/
?>