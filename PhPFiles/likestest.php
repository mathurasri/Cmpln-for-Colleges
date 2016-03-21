
<?php
$response = array();
//if (isset($_POST['comment_id'] ) && isset($_POST['username'] ) )
//{
	$mysql_db = 'cmpln';
	$comment_id = "9a96fb48-fbf5-430e-a390-27816e6778b6";
	$username = "msridhar";
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	//$sql = "SELECT comments.comment, comments.comment_id, UserProfile.avatarPosition, comments.username, comments.server_time_posted FROM comments inner join UserProfile on comments.email=UserProfile.email where comments.university = '$university' order by comments.time_posted desc LIMIT $start, $count ";		
	$sql = "SELECT * FROM likes WHERE comment_id='$comment_id' AND username='$username'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{
			$response["success"] = 0;
			$response["message"] = "User  had already liked the comment";
			echo json_encode($response);
			/*$retval = mysql_fetch_array($retval);	
			//echo $retval["imagePath"];
			$response["success"] = 1;
			$response["message"] = "User image downloaded";
			$response["imageUri"] = (String)$retval["imagePath"];
			//$response["imageUri"] = "Test";
			echo json_encode($response);			*/
		}
		else
		{
			$sql1 = "INSERT INTO likes(comment_id, username) VALUES ('$comment_id', '$username')";
			mysql_select_db($mysql_db) or die("Could not connect to DB.");
			$retval1 = mysql_query($sql1, $conn);
			if($retval1)
			{
				$response["success"] = 1;
				$response["message"] = "New like to the comment";
				echo json_encode($response);
			}
			else{
				$response["success"] = 0;
				$response["message"] = "New like could not be inserted";
				echo json_encode($response);
			}
			
			//echo "No rows found";
		}		
		
	}	
	    		
	mysql_close($conn);
//}
?>