<?php
//This program checks the existence of user account.  
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['email'])&& isset($_POST['password'])) {
 
	$email = $_POST['email'];
	$pwd = $_POST['password'];
    // connecting to db
    $dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');	
	$sql = "SELECT * FROM UserAccount WHERE email = '$email' AND password = '$pwd' AND Activate=1";
	
	mysql_select_db('cmpln') or die("Could not connect to DB.");
    
    $result = mysql_query($sql, $dbconn);
    if(!empty($result))
	{
		if (mysql_num_rows($result) > 0)
		{
			$response["success"] = 1;
			$response["message"] = "User account exists";
			echo json_encode($response);
			
		}
		else
		{
			$response["success"] = 0;
			$response["message"] = "user account does not exist";
			echo json_encode($response);
			
		}		
		
	}	
	else
	{
		$response["success"] = 0;
			$response["message"] = "Could not find user account";
			echo json_encode($response);
	}
}
else{
	$response["success"] = 0;
    $response["message"] = "Required field(s) is missing";    
    echo json_encode($response);
}
?>