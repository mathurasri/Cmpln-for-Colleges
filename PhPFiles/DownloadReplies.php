
<?php
//this program downloads the replies for specific comment
$response = array();
if (isset($_POST['comment_id'])&& isset($_POST['start']) && isset($_POST['count']))
{	
	$mysql_db = 'cmpln';
	$comment_id = $_POST['comment_id'];
	$start = intval($_POST['start']);
	$count = intval($_POST['count']);
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}	
	$sql = "SELECT replies.comment_id, replies.username, replies.reply_comment, replies.server_time_posted, UserProfile.avatarPosition from replies, UserProfile WHERE replies.username = UserProfile.username AND replies.comment_id = '$comment_id' order by replies.time_posted desc LIMIT $start, $count";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{
			$response["userreplies"] = array();
			while($row = mysql_fetch_array($retval))
			{
				$comment = array();
				$comment["avatar"] = $row["avatarPosition"];
				$comment["comment"] = $row["reply_comment"];
				$comment["username"] = $row["username"];
				$comment_time_posted = $row["server_time_posted"];				
				$comment["time"] = CalculateCommentTimeSpent($comment_time_posted);				
				$comment["comment_id"] = $row["comment_id"];				
				array_push($response["userreplies"], $comment);
			}
			$response["success"] = 1;
			echo json_encode($response);			
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not download the replies";
			
			echo json_encode($response);			
		}		
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not download the replies";
			
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
function ReturnCommentLikeFlag($comment_id, $username){
	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	$sql="SELECT * FROM likes WHERE comment_id='$comment_id' AND username = '$username'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	//if(!empty($retval))
	//{
		if (mysql_num_rows($retval) > 0)
		{
			return 1;
		}
		else{
			return 0;
		}
	//}
	/*else{
		return 0;
	}*/
}
function CalculateLikesPerComment($commentid)
{
	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	$sql = "SELECT count(*) `likes` from likes where comment_id='$comment_id'";
	
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{			
			while($row = mysql_fetch_array($retval))
			{
				if($row != null)
				{
					return $row["likes"];
				}
			}
			//return $row["likes"];			
		}
	}
	else
	{
		return 0;
	}
}
function  CalculateCommentTimeSpent($comment_time_posted)
	{
		// Posted date
		$posted_date = new DateTime();
		$year = intval(substr($comment_time_posted, 0, 4));
		$month = intval(substr($comment_time_posted, 5, 2));
		$day = intval(substr($comment_time_posted, 8, 2));
		$posted_date->setDate($year, $month, $day);
		$hour = intval(substr($comment_time_posted, 11, 2));
		$minute = intval(substr($comment_time_posted, 14, 2));
		$second = intval(substr($comment_time_posted, 17, 2));
		$posted_date->setTime($hour, $minute, $second);
		// Retrieval date
		$retrieval_date = new DateTime();
		$datetimediff = $posted_date->diff($retrieval_date);
		if($datetimediff->y > 0)
		{
			return substr($comment_time_posted, 0, 10);
		}
		else 
		{
			if($datetimediff->m > 0)
			{
				return substr($comment_time_posted, 0, 10);
			}
			else 
			{
				if($datetimediff->d > 0)
				{
					$timestring = (string)$datetimediff->d." days ago";
					return $timestring;
				}
				else 
				{
					if($datetimediff->h > 0)
					{
						$timestring = (string) $datetimediff->h."h";
						return $timestring;
					}
					else 
					{
						if($datetimediff->i > 0)
						{
							$timestring = (string) $datetimediff->i."m";
							return $timestring;
						}
						else 
						{
							return "0m";
						}
					}
				}
			}
		}
	}
	?>