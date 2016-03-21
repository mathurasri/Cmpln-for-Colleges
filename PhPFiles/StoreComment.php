<?php
//This program inserts new comment.
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['comment_id']) && isset($_POST['comment']) && isset($_POST['email']) && isset($_POST['university']) && isset($_POST['username'])) {
 
	$comment_id = $_POST['comment_id'];
	$comment = $_POST['comment'];
    $email = $_POST['email'];
	$university = $_POST['university'];
	$username = $_POST['username'];
	$datetime = new DateTime();
	$datetimeres = $datetime->format('Y-m-d H:i:s'); 	
    // connecting to db
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	
	
	$sql = "INSERT INTO comments(comment, comment_id, email, university, username, time_posted, server_time_posted) VALUES ('$comment', '$comment_id', '$email', '$university', '$username', NOW(), '$datetimeres')";
	mysql_select_db('cmpln') or die("Could not connect to DB.");
    // mysql inserting a new row
    $result = mysql_query($sql, $dbconn);
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "comment successfully inserted.";
 
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