<?php
  
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['name'])) {
 
	$name = $_POST['name'];
	    
    // connecting to db
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	$sql = "INSERT INTO test(name) VALUES ('$name')";
	//$sql = "INSERT INTO test(name) VALUES ('user1')";
	mysql_select_db('cmpln') or die("Could not connect to DB.");
    // mysql inserting a new row
    $result = mysql_query($sql, $dbconn);
 
    // check if row inserted or not
    if ($result) {        
        $response["success"] = 1;
        $response["message"] = "name successfully  inserted.";        
        echo json_encode($response);
    } else {        
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";         
        echo json_encode($response);
    }
} else {
    
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";    
    echo json_encode($response);
}
?>