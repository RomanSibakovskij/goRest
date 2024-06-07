package lt.techin.example;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;


public class goRestTest {

    //test whether the user with specified id exists in the list
    @Test
    void userSearchTest(){

        String id = "6946931";

        given().
                pathParam("userId", id).

        when().
                get("https://gorest.co.in/public/v2/users/{userId}").
                then().
                statusCode(200).
                body("status", equalTo("inactive"))
                .log()
                .body();
    }

    //test user list output -> 20 per page on page 2
    @Test
    void userListSearchTest(){
        String id = "6949835";

        given().
                queryParams("page", 2).
                queryParams("per_page", 20).
                when().
                get("https://gorest.co.in/public/v2/users?page=2&per_page=20").
                then().
                statusCode(200).
                assertThat().//assertion fill field
                body("id", hasSize(20))
                .log()
                .body();
    }
    //crud structure test -> addition of a new user
    @Test
    void crudUserCreationTest(){
        Map<String, String> user = new HashMap<>();
        user.put("name", "Dr.Albert Mannheimer");
        user.put("email", "dr_mannheimer@moore.test");
        user.put("gender", "male");
        user.put("status", "inactive");

        given().
                contentType(ContentType.JSON)
                .header("Authorization", "Bearer 8a68b9969e47db58f398557b21c246d501dadb509935567f7566f2d34f4bcc1e")
                .body(user).
                when().
                post("https://gorest.co.in/public/v2/users")
                .then()
                .assertThat()
                .statusCode(201);

    }

    //non-registered user input test
    @Test
    void nonRegisteredUserCreationTest() {

        String token = "8a68b9969e47db58f398557b21c246d501dadb509935567f7566f2d34f4bcc1e";

        Map<String, String> user = new HashMap<>();
        user.put("name", "Dr. Thomas Thompson");
        user.put("email", "dr_thompson@moore.test");
        user.put("gender", "male");
        user.put("status", "inactive");

        given().
                headers("Authorization", "Bearer " + token, "Content-Type", ContentType.JSON).
                body(user).
                when().
                post("https://gorest.co.in/public/v2/users").
                then().
                log().body().
                assertThat().
                statusCode(201);


    }

    //secure API test -> failed authentication test
    @Test
    void secureAPINoUserAuthenticationTest(){
        String token = "8a68b9969e47db58f398557b21c246d501dadb509935567f7566f2d34f4bcc1e";

        Map<String, String> user = new HashMap<>();
        user.put("name", "Dr.John Kent");
        user.put("email", "dr_kent@moore.test");
        user.put("gender", "male");
        user.put("status", "active");

        given().
                headers("Authorization", "Bearer" + token, "Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
                .body(user)
                .when()
                .post("https://gorest.co.in/public/v2/users")
                .then()
                .log().body()
                .assertThat()
                .statusCode(401);
    }
}
