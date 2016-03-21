
<?php
//This program inserts the like by the specific user to the specific comment.
$response = array();
if (isset($_POST['comment_id'] ) && isset($_POST['username'] ) )
{
	$mysql_db = 'cmpln';
	$comment_id = $_POST['comment_id'];
	$username = $_POST['username'];
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
		
	$sql = "SELECT * FROM likes WHERE comment_id='$comment_id' AND username='$username'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{
			
			$sql1 = "DELETE FROM likes WHERE comment_id='$comment_id' AND username='$username'";
			mysql_select_db($mysql_db) or die("Could not connect to DB.");
			$retval1 = mysql_query($sql1, $conn);
			if($retval1)
			{
				$response["success"] = 1;
				$response["message"] = "Unlike to the comment";
				$response["likeflag"]="0";
				echo json_encode($response);
			}
			else{
				$response["success"] = 0;
				$response["message"] = "Unlike comment not deleted";
				echo json_encode($response);
			}
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
				$response["likeflag"] = "1";
				echo json_encode($response);
			}
			else{
				$response["success"] = 0;
				$response["message"] = "New like could not be inserted";
				echo json_encode($response);
			}
			
			
		}		
		
	}	
	    		
	mysql_close($conn);
}
?>