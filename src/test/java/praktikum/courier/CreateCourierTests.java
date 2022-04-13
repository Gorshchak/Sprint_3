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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;


public class CreateCourierTests {

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
    @DisplayName("Сheck the successful creation of a courier")
    @Description("Creating a courier with all fields. Login, password and firstname")
    public void successfulCreateCourier() {

        ValidatableResponse response = courierClient.create(courier);

        int statusCodePositiveResponseCreate = response.extract().statusCode();
        boolean isCourierCreated = response.extract().path("ok");

        courierId = courierClient.login(new CourierCredentials(courier.login, courier.password)).extract().path("id");

        assertThat(statusCodePositiveResponseCreate, equalTo(201));
        assertTrue("Courier is not created", isCourierCreated);
        assertThat("Courier ID is incorrect", courierId, is(not(0)));
    }

    @Test
    @DisplayName("Сheck the unsuccessful creation of a courier")
    @Description("Creating a courier with a login, password and firstname twice")
    public void unsuccessfulCreateTwoIdenticalCouriers() {

        courierClient.create(courier);

        ValidatableResponse response = courierClient.create(courier);

        int statusCodeNegativeResponse = response.extract().statusCode();
        String message = response.extract().path("message");

        courierId = courierClient.login(new CourierCredentials(courier.login, courier.password)).extract().path("id");

        assertThat(statusCodeNegativeResponse, equalTo(409));
        assertThat("Wrong body - massage", message, (equalTo("Этот логин уже используется")));
        assertThat("Courier ID is incorrect", courierId, is(not(0)));
    }
}