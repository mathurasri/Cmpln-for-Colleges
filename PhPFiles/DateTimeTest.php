<?php
	$mydate = getdate();
	$nmonth = date('m',strtotime($mydate[month]));
	echo "$mydate[weekday], $mydate[month] $mydate[mday], $mydate[year], $mydate[hours], $mydate[minutes], $mydate[seconds]";
	echo "Hello";
	echo $nmonth;
	echo "<br/>";
	$date = new DateTime();
	$result = $date->format('Y-m-d H:i:s');
	echo $result;
	echo "<br/>";
	echo substr($result, 11, 2);
	echo "<br/>";
	echo $mydate[weekday];	
?>