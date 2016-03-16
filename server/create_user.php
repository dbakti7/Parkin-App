<?php
 
/*
 * Following code will create a new user row
 * All user details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp"); 
// check for required fields
if (isset($_GET['name']) && isset($_GET['email']) 
	&& isset($_GET['phone_number']) && isset($_GET['password'])) {
	
	
    $name = $_GET['name'];
    $email = $_GET['email'];
    $phone_number = $_GET['phone_number'];
    $password = $_GET['password'];
 
    // include db connect class
    require_once __DIR__ . '/db_connect.php';
 
    // mysql inserting a new row
    $result = $mysqli->query("INSERT INTO userlogin
    (id, name, email, phone_number, password) 
    VALUES(null, '$name', '$email', '$phone_number', '$password')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "User successfully created";
 
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
