<?php
 
/*
 * Following code will get single user details
 * A product is identified by username (user_name)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/db_connect.php';
 
// connecting to db
$mysqli = new mysqli("localhost", "parkinapp", "parkinapppassword", "parkinapp");
 
// check for post data
if (isset($_GET["email"])) {
    $email = $_GET['email'];
 
    // get a product from products table
    $result = $mysqli->query("SELECT *FROM userlogin WHERE email = '$email'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysqli_num_rows($result) > 0) {
 
            $result = mysqli_fetch_array($result);
 
            $user = array();
			$user["id"] = $result["id"];
            $user["name"] = $result["name"];
            $user["email"] = $result["email"];
            $user["phone_number"] = $result["phone_number"];
            $user["password"] = $result["password"];
            $user["score1"] = $result["score1"];
            $user["score2"] = $result["score2"];
            $user["score3"] = $result["score3"];
            $user["play_time1"] = $result["play_time1"];
            $user["play_time2"] = $result["play_time2"];
            $user["play_time3"] = $result["play_time3"];
            $user["reminder_period"] = $result["reminder_period"];
            $user["average_game1"] = $result["average_game1"];
            $user["average_game2"] = $result["average_game2"];
            $user["average_game3"] = $result["average_game3"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["user"] = array();
 
            array_push($response["user"], $user);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no product found
            $response["success"] = 0;
            $response["message"] = "No user found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no product found
        $response["success"] = 0;
        $response["message"] = "No user found";
 
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
