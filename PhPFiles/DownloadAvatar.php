<?php
//Sorry for the wrong naming
//This program downloads entire user profile
$response = array();
if (isset($_POST['email']))
{				
	 $mysql_db = 'cmpln';
	 $conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 if(!$conn)
	{
		die("Could not connect.");
	}
	
	 $email = $_POST['email'];
	 
	 $sql = "SELECT * FROM UserProfile WHERE email = '$email'";	
		
	 mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{		
		if (mysql_num_rows($retval) > 0)
		{			
			$retval = mysql_fetch_array($retval);	
			
			$response["success"] = 1;
			 $response["message"] = "User image downloaded";
			$response["imageUri"] = (String)$retval["avatarPosition"];
			$response["username"] = (String)$retval["username"];
			$response["bio"] = (String)$retval["bio"];
			
			echo json_encode($response);			
		 }
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not download user image, username and bio. No rows returned";
			$response["imageUri"] = "No image present";
			$response["username"] = "No username";
			echo json_encode($response);
			
	    }
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not download user image, username and bio. No rows returned";
			$response["imageUri"] = "No image present";
			$response["username"] = "No username";
			echo json_encode($response);
			
	}

	 mysql_close($conn);
}
else
{
	$response["success"] = 0;
	$response["message"] = "could not download the image. Email is not set";
	$response["imageUri"] = "No image present";
	$response["username"] = "No username";
	echo json_encode($response);
}
?>