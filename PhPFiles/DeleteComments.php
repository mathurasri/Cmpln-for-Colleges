<?php
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['email'])) {
    $email = $_POST['email'];    
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
 
    mysql_select_db('cmpln') or die("Could not connect to DB.");
     
    $result = mysql_query("DELETE FROM comments WHERE email = '$email'");
 
    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "User comments successfully deleted";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        //No comments found
        $response["success"] = 0;
        $response["message"] = "No User comments found";
 
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
