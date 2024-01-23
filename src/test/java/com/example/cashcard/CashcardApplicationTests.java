package com.example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

// para implemetar PUT updates
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

// NOTA MENTAL: Si vemos que tenemos un 403 en alguno de nuestros test y tenemos enabled
// spring security dicho 403 es spring security defaults in action!!!


// This will start our Spring Boot application and make it available for our test to perform requests to it.

// comentario de prueba

@SpringBootTest(webEnvironment =  SpringBootTest.WebEnvironment.RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CashcardApplicationTests {
	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldReturnACashCardWhenDataIsSaved(){
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// This converts the response String into a JSON-aware object with lots of helper methods.
		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// $.id es el mismo parametro que tiene en el constructor
		// lo que estamos haciendo en estos tests es, cuando llegamos al endpoint /cashcards/99
		// nuestro controller devolvera un objeto de nuesta bbdd , esperamos que ese objeto exista(HttpStatus.OK)
		 // una vez con ese objeto comprobado que existe , convertimos la respuesta en un formato JSON: DocumentContext documentContext = JsonPath.parse(response.getBody());
		// y de esta manera vamos a poder revisar los campos que nos devuelve esta instancia id y amount para comprobar que existen
		// ya que este objeto lo tenmos en la bbdd , gracias al archivo data.sql y schema,sql
		Number id = documentContext.read("$.id");
		assertThat(id).isEqualTo(99);

		Double amount = documentContext.read("$.amount");
		assertThat(amount).isEqualTo(123.45);

	}

	@Test
	void shouldNotReturnACashCardWithAnUnknownId() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/1000", String.class);

		// nos aseguramos de que el test no pase si el id no es 99
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
		// aqui nos aseguramos de que el cuerpo de la respuesta este en blanco / no contenga nada
		assertThat(response.getBody()).isBlank();

	}

	// POST test
	@Test
	@DirtiesContext
	void  shouldCreateANewCashCard() {

		 // 1 - crear objeto que vamos a enviar como body de nuestra request
		// id = null pq la bbdd lo creara
		 //pd: cambiar 44L to null after test or in case i forgot to
		CashCard newCashCard = new CashCard(null,250.00, null);
		// no esperamos nada de vuelta de este metodo por eso ResponseEntity<Void>
		ResponseEntity<Void> createResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.postForEntity("/cashcards", newCashCard, Void.class);

		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		// con el POST tenemos que devolver en URI de donde se creo la resource
		// y es lo que vamos a hacer ahora
		//send a 201 (Created) response containing a Location header field that provides an identifier for the primary resource created
		URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity(locationOfNewCashCard, String.class);

		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		// more assertions

		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");

		assertThat(id).isNotNull();
		assertThat(amount).isEqualTo(250.00);
	}

	@Test
	void shouldReturnAllCashCardsWhenListIsRequested() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(response.getBody());

		// comprobando la longitud de nuestra array total
		 // docs: https://github.com/json-path/JsonPath
		int cashCardCount = documentContext.read("$.length()");
		assertThat(cashCardCount).isEqualTo(3);

		JSONArray ids = documentContext.read("$..id");
		assertThat(ids).containsExactlyInAnyOrder(99, 100, 101);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactlyInAnyOrder(123.45, 1.0, 150.00);
	}

	// pagination test
	@Test
	void shouldReturnAPageOfCashCards(){
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());
		JSONArray page = documentContext.read("$[*]");
		assertThat(page.size()).isEqualTo(1);

	}

	@Test
	void shouldReturnASortedPageOfCashCards() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards?page=0&size=1&sort=amount,desc", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		JSONArray read = documentContext.read("$[*]");
		assertThat(read.size()).isEqualTo(1);

		double amount = documentContext.read("$[0].amount");
		assertThat(amount).isEqualTo(150.00);
	}

	// este test lo hacemos en caso de que el usuario no nos envie todos los datos del URI

	@Test
	void shouldReturnASortedPageOfCashCardsWithNoParametersAndUseDefaultValues() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards", String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		DocumentContext documentContext = JsonPath.parse(response.getBody());

		JSONArray page = documentContext.read("$[*]");

		assertThat(page.size()).isEqualTo(3);

		JSONArray amounts = documentContext.read("$..amount");
		assertThat(amounts).containsExactly(1.00, 123.45,150.00);
	}


	// test para comporbar que los credenciales son malos
	// spring security apartado
	@Test
	void shouldNotReturnACashCardWhenUsingBadCredentials() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("BAD-USER", "abc123")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

		// ahora testeamos que la contraseña este mal
		response = restTemplate
				.withBasicAuth("sarah1", "BAD-PASSWORD")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
	}

	@Test
	void shouldRejectUsersWhoAreNotCardOwners() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("hank-owns-no-cards", "qrs456")
				.getForEntity("/cashcards/99", String.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

	@Test
	void shouldNotAllowAccessToCashCardsTheyDoNotOwn() {
		ResponseEntity<String> response = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/102", String.class); // kumar2 data
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	@DirtiesContext // usamos esta notacion porque vamos a modificar data de la app, por eso la usamos
	void shouldUpdateAnExistingCashCard() {
		CashCard cashCardUpdate = new CashCard(null, 19.99, null);
		// como su nombre indica esta sera la request que nos envien en nuestra PUT action
		HttpEntity<CashCard> request = new HttpEntity<>(cashCardUpdate);
		// aqui lo que estamos simulando es como hacemos HIT hacia nuestro end-point con los
		// datos que vienen en el body para hacer el update
		ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.PUT, request, Void.class);

		//por que usamos .exchange? -> putForEntity no existe, por ello usamos .exchange
		// exchange method es una version mas generalizada que xyzForEntity()

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		//asserting update was successful
		// 1- Here, we fetch CashCard 99 again, so we can verify that it was updated.
		ResponseEntity<String> getResponse = restTemplate
				.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
		DocumentContext documentContext = JsonPath.parse(getResponse.getBody());
		Number id = documentContext.read("$.id");
		Double amount = documentContext.read("$.amount");
		assertThat(id).isEqualTo(99);
		assertThat(amount).isEqualTo(19.99);


	}

	@Test
	void shouldNotUpdateACashCardThatDoesNotExist() {
		CashCard unknownCard  = new CashCard(null, 19.99, null);
		HttpEntity<CashCard> request = new HttpEntity<>(unknownCard);
		ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99999", HttpMethod.PUT, request, Void.class);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

    @Test
	void shouldNotUpdateACashCardThatIsOwnedBySomeoneElse() {
		CashCard kumarCard = new CashCard(null, 333.33, null);
		HttpEntity<CashCard> request = new HttpEntity<>(kumarCard);
		ResponseEntity<Void> response = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/cashcards/102", HttpMethod.PUT, request, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	// delete tests
	@Test
	@DirtiesContext // añadimos esta notacion a todos los tests que cambien la data que tenemos
	void shouldDeleteAnExistingCashCard() {
		ResponseEntity<Void> request = restTemplate.withBasicAuth("sarah1", "abc123")
				.exchange("/cashcards/99", HttpMethod.DELETE, null, Void.class);
		// por que usamos .exchange en lugar de delete ? -> The Spring Web framework supplies the delete() method as a convenience, but it comes with some assumptions:
		// A response to a DELETE request will have no body.
		//The client shouldn't care what the response code is unless it's an error, in which case, it'll throw an exception
		// en este caso: But, the second assumption makes delete() unsuitable for us: We need the ResponseEntity in order to assert on the status code! So, we won't use the convenience method, but rather let's use the more general method: exchange()
		assertThat(request.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		// como en el metodo de update tenemos que hacer una peticion
		// get para comprobar que hemos eliminado el elemento

		ResponseEntity<String> getRequest = restTemplate.withBasicAuth("sarah1", "abc123")
				.getForEntity("/cashcards/99", String.class);
		assertThat(getRequest.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	void shouldNotDeleteACashCardThatDoesNotExist() {
		ResponseEntity<Void> request = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/cashcards/9999", HttpMethod.DELETE, null, Void.class);
		assertThat(request.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

	}

	@Test
	void shouldNotDeleteACashCardThatIsOwnedBySomeoneElse() {
		ResponseEntity<Void> request = restTemplate.withBasicAuth("sarah1", "abc123").exchange("/cashcards/102", HttpMethod.DELETE, null, Void.class);
		assertThat(request.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

		//testeamos que el record sigue ahi y no se ha borrado
		ResponseEntity<String> getRequest = restTemplate.withBasicAuth("kumar2", "xyz789")
				.getForEntity("/cashcards/102", String.class);
		assertThat(getRequest.getStatusCode()).isEqualTo(HttpStatus.OK);

	}



}
