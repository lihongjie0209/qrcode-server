package cn.lihongjie.qrcode.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class QRCodeControllerTest {

    @Test
    public void testHealthEndpoint() {
        given()
          .when().get("/api/qrcode/health")
          .then()
             .statusCode(200)
             .body("service", is("QR Code Detection Service"));
    }

}
