<?php
if (isset($_POST['email'])&& isset($_POST['imageuri'])&& isset($_POST['username'])&& isset($_POST['bio'])){
	 
	$imgFlag = 0;
	$bioFlag = 0;
	$usernameFlag = 0;
	$email = $_POST['email'];
	 $imageuri = $_POST['imageuri'];
	 $username = $_POST['username'];
	 $bio = $_POST['bio'];
	 $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 $sql = "SELECT * from UserProfile WHERE email = '$email'";
	 mysql_select_db('cmpln') or die("Could not connect to DB.");
	 $result = mysql_query($sql, $dbconn);	
		if (mysql_num_rows($result) > 0)
		{			
			if($imageuri != null)
			{
				$sql = "UPDATE UserProfile SET avatarPosition = '$imageuri' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
						$imgFlag = 1;
						
				} else {        
					$imgFlag = 0;
					
				}
			}
			else
			{
				$imgFlag = 0;
			}
			if($username != null)
			{
				$sql1 = "SELECT * FROM UserProfile WHERE username = '$username' AND email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql1, $dbconn);
				if ($result) {        
						$usernameFlag = 0;
				} else {
					$sql = "UPDATE UserProfile SET username = '$username' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
					if ($result) {        
						$usernameFlag = 1;
					} else {        
						$usernameFlag = 0;
					}
					
				}
				
			}
			else
			{
				$usernameFlag = 0;
			}
			if($bio != null)
			{
				$sql = "UPDATE UserProfile SET bio = '$bio' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
					$bioFlag = 1;
				} else {        
					$bioFlag = 0;
				}
			}
			else
			{
				$bioFlag = 0;
			}
			//echo "A row exists";
			if($usernameFlag == 1 || $bioFlag ==  1|| $imgFlag == 1){
				$response["success"] = 1;
					$response["message"] = "UserProfile successfully  inserted.";        
					echo json_encode($response);
			}
		}
		else
		{
			$sql = "INSERT INTO UserProfile(email, username, avatarPosition, bio) VALUES ('$email', '$username', '$imageuri', '$bio')";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
					$response["success"] = 1;
					$response["message"] = "UserProfile successfully  inserted.";        
					echo json_encode($response);
				} else {        
					$response["success"] = 0;
					$response["message"] = "Oops! An error occurred while inserting the record.";         
					echo json_encode($response);
				}
			//echo "A row does not exists";
		}		
			
		$sql = "SELECT * from comments WHERE email = '$email'";
	 mysql_select_db('cmpln') or die("Could not connect to DB.");
	 $result = mysql_query($sql, $dbconn);	
		if (mysql_num_rows($result) > 0)
		{			
			if($imageuri != null)
			{
				$sql = "UPDATE comments SET avatarPosition = '$imageuri' WHERE email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql, $dbconn);
				if ($result) {        
						
				} else {        
					
				}
			}
			else
			{
				
			}
			if($username != null)
			{
				$sql1 = "SELECT * FROM comments WHERE username = '$username' AND email = '$email'";
				mysql_select_db('cmpln') or die("Could not connect to DB.");
				$result = mysql_query($sql1, $dbconn);
				if ($result) {        
						$usernameFlag = 0;
				} else {
				
					
				}
				
				
			}
			else
			{
				
			}
			
			//echo "A row exists";
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "No row exists";    
			echo json_encode($response);
			//echo "A row does not exists";
		}		
			

	mysql_close($dbconn);
}
else {
    
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";    
    echo json_encode($response);
}
?>