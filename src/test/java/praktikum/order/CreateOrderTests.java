package praktikum.order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import praktikum.client.OrderClient;
import praktikum.data.ColorOrder;
import praktikum.data.DataForCreateNewOrder;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class CreateOrderTests {

    private OrderClient orderClient;
    DataForCreateNewOrder order;
    private int expectedStatus;
    private List <ColorOrder> color;
    private int track;

    public CreateOrderTests(List <ColorOrder> color, int expectedStatus) {
        this.color = color;
        this.expectedStatus = expectedStatus;
    }

    @Parameterized.Parameters
    public static Object[][] getTestData() {
        return new Object[][] {
                {List.of(ColorOrder.BLACK), 201},
                {List.of(ColorOrder.GREY), 201},
                {null, 201},
                {List.of(ColorOrder.BLACK, ColorOrder.GREY), 201},
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
        order = DataForCreateNewOrder.getRandom().setColor(color);
    }

    @After
    public void tearDown() {
        orderClient.cancel(track);
    }

    @Test
    @DisplayName("Сheck the creation of an order with a different color")
    @Description("Сreating an order with all fields")
    public void CreateOrderFieldColor() {

        ValidatableResponse response = orderClient.create(order);

        int statusCodeResponse = response.extract().statusCode();
        track = response.extract().path("track");

        assertThat(statusCodeResponse, equalTo(expectedStatus));
        assertThat("Order is not created", track, is(not(0)));
    }
}
