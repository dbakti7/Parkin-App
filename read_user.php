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
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["user_name"])) {
    $user_name = $_GET['user_name'];
 
    // get a product from products table
    $result = mysql_query("SELECT *FROM userlogin WHERE user_name = $user_name");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $user = array();
			$user["id"] = $result["id"];
            $user["user_name"] = $result["user_name"];
            $user["name"] = $result["name"];
            $user["email"] = $result["email"];
            $user["phone_number"] = $result["phone_number"];
            $user["password"] = $result["password"];
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
