


<?php
  
// array for JSON response

if (isset($_POST['email'])) {
	require 'PHPMailer-master/PHPMailerAutoload.php';
	$response = array();
	$email = $_POST['email'];
	$cmpln_email = "";
	$cmpln_pwd="";
	$url = "http://cmpln.com/Scripts/ActivateUserAccount.php?email=".$email;
	$mysql_db = 'cmpln';
	 $conn = mysql_connect('db.cmpln.com','complain', 'trashtalk');
	 if(!$conn)
	{
		die("Could not connect.");
	}
		 	 
	 $sql = "SELECT email, password FROM cmpln_account";			
	 mysql_select_db($mysql_db) or die("Could not connect to DB.");
	$retval = mysql_query($sql, $conn);	
	//if the query returns rows
	if(!empty($retval))
	{	//if there is atleast  1 row
		if (mysql_num_rows($retval) > 0)
		{			
			$retval = mysql_fetch_array($retval);							
			$cmpln_email = $retval["email"];
			$cmpln_pwd = $retval["password"];											
		 }
		else
		{
			$cmpln_email = "";
			$cmpln_pwd = "";			
	    }
		
	}	
	else
	{
		$cmpln_email = "";
		$cmpln_pwd = "";		
	}

	 mysql_close($conn);
	$mail = new PHPMailer;
	//$body = file_get_contents('contents.html');
	$mail->isSMTP();
	$mail->SMTPDebug = 2;
	$mail->Debugoutput = 'html';
	$mail->Host = 'smtp.gmail.com';
	$mail->SMTPAuth = true;
	//$mail->SMTPKeepAlive = true; // SMTP connection will not close after each email sent, reduces SMTP overhead
	//$mail->Port = 25;
	$mail->Port = 587;
	$mail->SMTPSecure = 'tls';
	$mail->Username = $cmpln_email;
	$mail->Password = $cmpln_pwd;
	$mail->setFrom($cmpln_email, 'Cmpln Admin');
	$mail->addReplyTo($cmpln_email, 'Cmpln Admin');
	$mail->Subject = "Cmpln Account Verification";
	$mail->addAddress($email);
	$mail->Body = "Hello Welcome to Cmpln. Please click the given link to activate your user account: ".$url;
	 
	// echo phpinfo() . "\n";
	if(!$mail->send()) {
	   echo 'Message could not be sent.';
	   echo 'Mailer Error: ' . $mail->ErrorInfo;
	   exit;
	}
}
else {
    
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing and email not sent";    
    echo json_encode($response);
}
?>