package com.javacademy.new_york_times;


import com.javacademy.new_york_times.controller.NewsController;
import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class NewsControllerTest {

    @Autowired
    private NewsController newsController;

    private final RequestSpecification requestSpecification = new RequestSpecBuilder()
            .setBasePath("/api/v1/news")
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .setDefaultParser(Parser.JSON)
            .build();

    // не работает
    @Test
    @DisplayName("Создание новости")
    public void createNewsSuccess() {
        NewsDto createdNewsDto = NewsDto.builder()
                .number(null)
                .title("My news")
                .text("Created news by me")
                .author("Me")
                .build();

        given(requestSpecification)
                .body(createdNewsDto)
                .post()
                .then()
                .spec(responseSpecification)
                .statusCode(201)
                .body("title", Matchers.equalTo("My news"))
                .body("text", Matchers.equalTo("Created news by me"))
                .body("author", Matchers.equalTo("Me"))
                .body("number", Matchers.equalTo(56));
    }


    // не работает - требует парсер для JSON - указал в responseSpecification - всё равно не работает....
    @Test
    @DisplayName("Получение всех новостей")
    public void getAllNewsSuccess() {
        PageDto pageDto = given(requestSpecification)
                .get("?pageNumber=1")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .body()
                .as(PageDto.class);

        assertEquals(1, pageDto.getCurrentPage());
    }

    @Test
    @DisplayName("Получение новости по номеру новости")
    public void getNewsByIdSuccess() {
        NewsDto newsDto = given(requestSpecification)
                .get("/1")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .body()
                .as(NewsDto.class);

        assertEquals(1, newsDto.getNumber());
        assertEquals("News #1", newsDto.getTitle());
        assertEquals("Today is Groundhog Day #1", newsDto.getText());
        assertEquals("Molodyko Yuri", newsDto.getAuthor());

    }

    @Test
    @DisplayName("Получение автора по номеру новости")
    public void getNewsAuthorByNewsNumberSuccess() {
        String author = given(requestSpecification)
                .get("/author?newsNumber=2")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .body()
                .asPrettyString();

        assertEquals("Molodyko Yuri", author);
    }

    @Test
    @DisplayName("Получение текста новости по номеру новости")
    public void getNewsTextByNewsNumberSuccess() {
        String newsText = given(requestSpecification)
                .get("/text?newsNumber=2")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .body()
                .asPrettyString();

        assertEquals("Today is Groundhog Day #2", newsText);
    }

    @Test
    @DisplayName("Удаление новости по id")
    public void deleteNewsByIdSuccess() {
        Boolean result = given(requestSpecification)
                .delete("/3")
                .then()
                .spec(responseSpecification)
                .statusCode(200)
                .extract()
                .as(Boolean.class);

        assertTrue(result);
    }


    // не работает - требует парсер для JSON - указал в responseSpecification - всё равно не работает....
    @Test
    @DisplayName("Обновление новости по номеру")
    public void updateNewsByIdSuccess() {
        NewsDto requestNewDto = NewsDto.builder()
                .number(4)
                .text("Created news by me")
                .build();

        given(requestSpecification)
                .body(requestNewDto)
                .patch()
                .then()
                .spec(responseSpecification)
                .statusCode(202)
                .body("title", Matchers.equalTo("News #4"))
                .body("text", Matchers.equalTo("Created news by me"))
                .body("author", Matchers.equalTo("Molodyko Yuri"))
                .body("number", Matchers.equalTo(4));

//        NewsDto newsDto = mapper.toDto(newsRepository.findAll().get(4));
//    assertEquals("Created news by me", newsDto.getText());
    }
}
