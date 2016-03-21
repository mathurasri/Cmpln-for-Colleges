
<?php
//This program downloads the users who you can possibly follow and also downloads the status of you following them.
$response = array();
if (isset($_POST['university'] ) && isset($_POST['username']))
{
	$mysql_db = 'cmpln';
	$university= $_POST['university'];	
	$username = $_POST['username'];
	$followflag1 = 0;
	$followflag2 = 0;
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	$response["usercomments"] = array();
	if(!$conn)
	{
		die("Could not connect.");
	}
	
	
	$sql = "SELECT DISTINCT comments.username FROM comments WHERE comments.university = '$university' AND comments.username != '$username'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{			
			while($row = mysql_fetch_array($retval))
			{
				$comment = array();
				$comment["username"] = $row["username"];
	
				$comment["followflag"] = ReturnFollowedFlag($username,$comment["username"] );							
				array_push($response["usercomments"], $comment);
			}
			$response["success"] = 1;
			echo json_encode($response);
		}
		
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not select followed";
			
			echo json_encode($response);
			
		}		
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not select followed";
			
			echo json_encode($response);
			
	}    							
	mysql_close($conn);
}
else
{
	$response["success"] = 0;
	$response["message"] = "Required fields missing";
	
	echo json_encode($response);
}
function ReturnFollowedFlag($followed_by_name, $following_name){
	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	$sql="SELECT * FROM  follows WHERE following='$following_name' AND followed_by = '$followed_by_name'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	
		if (mysql_num_rows($retval) > 0)
		{
			return "1";
		}
		else{
			return "0";
		}
}
?>