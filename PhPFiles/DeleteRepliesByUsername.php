<?php
//This program deletes the replies for specific comment
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['username'])) {
    $username = $_POST['username'];    
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
 
    mysql_select_db('cmpln') or die("Could not connect to DB.");
 
    // mysql update row with matched pid
    $result = mysql_query("DELETE FROM replies WHERE username = '$username'");
 
    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "User replies successfully deleted";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        //No replies found
        $response["success"] = 0;
        $response["message"] = "No User replies found";
 
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
