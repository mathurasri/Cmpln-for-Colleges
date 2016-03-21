<?php
if (isset($_POST['email'])&& isset($_POST['imageuri'])&& isset($_POST['username'])&& isset($_POST['bio'])){
	 $email = $_POST['email'];
	 $imageuri = $_POST['imageuri'];
	 $username = $_POST['username'];
	 $bio = $_POST['bio'];
	
	 $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 $sql = "SELECT * from UserProfile WHERE email = '$email'";
	 mysql_select_db('cmpln') or die("Could not connect to DB.");
	 $result = mysql_query($sql, $dbconn);
	 if(!empty($result))
	{
		echo "Enetered nonempty rows";
		if (mysql_num_rows($result) > 0)
		{
			echo "Entered row > 0";
			/*$response["success"] = 1;
			$response["message"] = "User account entered";
			echo json_encode($response);*/
			//echo 'User login right';
			if($imageuri != null)
			{
				$sql = "UPDATE UserProfile SET avatarPosition = '$imageuri' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
						$response["success"] = 1;
						$response["message"] = "UserProfile successfully  inserted.";        
						echo json_encode($response);
				} else {        
					$response["success"] = 0;
					$response["message"] = "Oops! An error occurred.";         
					echo json_encode($response);
				}
			}
			if($username != null)
			{
				$sql = "UPDATE UserProfile SET username = '$username' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
					$response["success"] = 1;
					$response["message"] = "UserProfile successfully  inserted.";        
					echo json_encode($response);
				} else {        
					$response["success"] = 0;
					$response["message"] = "Oops! An error occurred.";         
					echo json_encode($response);
				}
			}
			if($bio != null)
			{
				$sql = "UPDATE UserProfile SET bio = '$bio' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
					$response["success"] = 1;
					$response["message"] = "UserProfile successfully  inserted.";        
					echo json_encode($response);
				} else {        
					$response["success"] = 0;
					$response["message"] = "Oops! An error occurred.";         
					echo json_encode($response);
				}
			}
			echo "A row exists";
		}
		else
		{
			
			echo "A row does not exists";
		}		
		
	}	
	else
	{
			/*$response["success"] = 0;
			$response["message"] = "Could not insert the user account";
			echo json_encode($response);*/
			$sql = "INSERT INTO UserProfile(email, username, avatarPosition, bio) VALUES ('$email', '$username', '$imageuri', '$bio')";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
					$response["success"] = 1;
					$response["message"] = "UserProfile successfully  inserted.";        
					echo json_encode($response);
				} else {        
					$response["success"] = 0;
					$response["message"] = "Oops! An error occurred.";         
					echo json_encode($response);
				}
			echo "A row does not exists";
	}

	mysql_close($dbconn);
}
else {
    
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";    
    echo json_encode($response);
}
?>