package praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.CourierClient;
import praktikum.data.CourierCredentials;
import praktikum.data.DataForCreateNewCourier;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


public class LoginCourierTests {

    private CourierClient courierClient;
    private int courierId;
    private DataForCreateNewCourier courier;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        courier = DataForCreateNewCourier.getRandom();
    }

    @After
    public void tearDown() {
        courierClient.delete(courierId);
    }

    @Test
    @DisplayName("Successful authorization by courier")
    @Description("Courier authorization with all fields. Login and password")
    public void successfulAuthorization() {

        courierClient.create(courier);

        ValidatableResponse response = courierClient.login(new CourierCredentials(courier.login, courier.password));
        courierId = response.extract().path("id");
        int statusCodeResponse = response.extract().statusCode();

        assertThat("Courier ID is incorrect", courierId, is(not(0)));
        assertThat(statusCodeResponse, equalTo(200));
    }
}