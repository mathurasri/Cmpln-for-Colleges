
<?php
//This program downloads the comments of people who you follow.
$response = array();
if (isset($_POST['university'] ) && isset($_POST['start'] ) && isset($_POST['count'])&& isset($_POST['username']))
{
	$mysql_db = 'cmpln';
	$university= $_POST['university'];
	$start = intval($_POST['start']);
	$count = intval($_POST['count']);
	$time = "timespent";
	$username = $_POST['username'];
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
			
	
	$sql = "select count(*) as likesCount, table3.comment_id,table3.comment, table3.avatarPosition, table3.username, table3.server_time_posted, table3.likecommentid, table3.time_posted from (select * from ((select comments.comment_id, comments.time_posted as time_posted, comments.comment, UserProfile.avatarPosition, comments.username, comments.server_time_posted from comments, UserProfile where comments.email = UserProfile.email and comments.university = '$university' and comments.username in (select following from follows where followed_by='$username')) as table1 left outer join (select likes.comment_id as likecommentid from likes) as table2 on table1.comment_id=table2.likecommentid) ) as table3 group by table3.comment_id, table3.avatarPosition, table3.username, table3.server_time_posted, table3.likecommentid, table3.comment, table3.time_posted order by table3.time_posted desc LIMIT $start, $count";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	if(!empty($retval))
	{
		if (mysql_num_rows($retval) > 0)
		{
			$response["usercomments"] = array();
			while($row = mysql_fetch_array($retval))
			{
				$comment = array();
				$comment["avatar"] = $row["avatarPosition"];
				$comment["comment"] = $row["comment"];
				$comment["username"] = $row["username"];
				$comment_time_posted = $row["server_time_posted"];				
				$comment["time"] = CalculateCommentTimeSpent($comment_time_posted);
				if($row["likecommentid"] == null)
				{
					$comment["likes"] = 0;
				}
				else{
					$comment["likes"] = $row["likesCount"];
				}
				$comment["comment_id"] = $row["comment_id"];
				$comment["likesflag"] = ReturnCommentLikeFlag($comment["comment_id"],$username);
				$comment["noofreplies"] = CalculateNoOfReplies($comment["comment_id"]);
				array_push($response["usercomments"], $comment);
			}
			$response["success"] = 1;
			echo json_encode($response);
			
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "Could not download user image";
			$response["imageUri"] = "No image present";
			echo json_encode($response);
			
		}		
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not download the image";
			$response["imageUri"] = "No image present";
			echo json_encode($response);
			
	}
    
	
	
	mysql_close($conn);
}
else
{
	$response["success"] = 0;
	$response["message"] = "could not download the image";
	$response["imageUri"] = "No image present";
	echo json_encode($response);
}
function CalculateNoOfReplies($comment_id){
	$mysql_db = 'cmpln';
	$conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	if(!$conn)
	{
		die("Could not connect.");
	}
	$sql="SELECT count(*) as repliesCount FROM replies WHERE comment_id = '$comment_id'";
	mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	$row = mysql_fetch_array($retval);
	
		if ($row["repliesCount"] > 0)
		{
			return $row["repliesCount"];
			//return "1";
		}
		else{
			return 0;
		}
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
	
		if (mysql_num_rows($retval) > 0)
		{
			return 1;
		}
		else{
			return 0;
		}
	
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