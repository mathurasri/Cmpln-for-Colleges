<?php
//This program inserts the reply for specific comment.  
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['comment_id']) && isset($_POST['reply']) && isset($_POST['username'])) {
 
	$comment_id = $_POST['comment_id'];
	$comment = $_POST['reply'];
	$username = $_POST['username'];
	$datetime = new DateTime();
	$datetimeres = $datetime->format('Y-m-d H:i:s'); 	
    // connecting to db
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	
	
	$sql = "INSERT INTO replies(comment_id, username, reply_comment, time_posted, server_time_posted) VALUES ('$comment_id','$username','$comment', NOW(), '$datetimeres')";
	mysql_select_db('cmpln') or die("Could not connect to DB.");
    // mysql inserting a new row
    $result = mysql_query($sql, $dbconn);
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Reply successfully inserted";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>