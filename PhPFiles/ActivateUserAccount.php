<?php
	$response = array();
	//Gets the input for $email passed through url
	$email=$_GET['email'];
	if($email != null)
	{
		$dbconn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
		$sql = "UPDATE UserAccount SET Activate=1 WHERE email='$email'";	
		mysql_select_db('cmpln') or die("Could not connect to DB.");
		$result = mysql_query($sql, $dbconn);
 
		// check if row is updated
		if ($result) {        			        
			echo "Activate in UserAccount updated";
		} else {        
			echo "Activate in UserAccount not updated";
		}
	}
	else
	{
		echo "Required email field is missing. So User Account can't be activated";
	}
?>