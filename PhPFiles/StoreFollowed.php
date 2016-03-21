<?php
//Inserts the followed and followed by
if (isset($_POST['followed'])&& isset($_POST['username'])){	 

	$followed_by = $_POST['username'];
	$followed = $_POST['followed'];
	 $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 $sql = "INSERT INTO follows(following, followed_by) VALUES ('$followed', '$followed_by')";
	 mysql_select_db('cmpln') or die("Could not connect to DB.");
	 $result = mysql_query($sql, $dbconn);	
	if($result){
		$response["success"] = 1;
		echo json_encode($response);
	}	
	else{
		$response["success"] = 0;
		echo json_encode($response);
	}

	mysql_close($dbconn);
}
else {
    
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";    
    echo json_encode($response);
}
?>