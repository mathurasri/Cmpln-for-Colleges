<?php
//This program deletes the likes for a specific comment.
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['username'])) {
    $username = $_POST['username'];    
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
 
    mysql_select_db('cmpln') or die("Could not connect to DB.");
     
    $result = mysql_query("DELETE FROM likes WHERE username = '$username'");
 
    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "likes successfully deleted";
 
        // echoing JSON response
        echo json_encode($response);
    } else {        
        $response["success"] = 0;
        $response["message"] = "No likes found";
 
        // echo no users JSON
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
