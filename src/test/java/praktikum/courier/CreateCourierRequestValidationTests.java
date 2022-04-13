package praktikum.courier;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.client.CourierClient;
import praktikum.data.CourierCredentials;
import praktikum.data.DataForCreateNewCourier;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
;

@RunWith(Parameterized.class)
public class CreateCourierRequestValidationTests {

    private CourierClient courierClient;
    private int courierId;
    DataForCreateNewCourier courier;
    private int expectedStatus;
    private String expectedErrorTextMessage;

    public CreateCourierRequestValidationTests(DataForCreateNewCourier courier, int expectedStatus, String expectedErrorTextMessage) {
        this.courier = courier;
        this.expectedStatus = expectedStatus;
        this.expectedErrorTextMessage = expectedErrorTextMessage;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {DataForCreateNewCourier.getWithLoginOnly(), 400, "Недостаточно данных для создания учетной записи"},
                {DataForCreateNewCourier.getWithPasswordOnly(), 400, "Недостаточно данных для создания учетной записи"},
                {DataForCreateNewCourier.getWithLoginAndPasswordOnly() , 201, null}
        };
    }

    @Before
    public void setUp() {
        courierClient = new CourierClient();
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Сheck the creation of a courier without fields")
    @Description("Creating a courier only with the: " +
            "1. Login field " +
            "2. Password field " +
            "3. Login and password fields")
    public void createCouriersWithoutFields() {

        ValidatableResponse response = courierClient.create(courier);

        int statusCode = response.extract().statusCode();
        String errorTextMessage = response.extract().path("message");

        if(statusCode == 201) {
            courierId = courierClient.login(new CourierCredentials(courier.login, courier.password)).extract().path("id");
        }

        assertThat(statusCode, equalTo(expectedStatus));
        assertThat(errorTextMessage, equalTo(expectedErrorTextMessage));
    }
}