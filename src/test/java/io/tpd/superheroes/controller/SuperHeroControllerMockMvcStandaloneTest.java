package io.tpd.superheroes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.tpd.superheroes.domain.SuperHero;
import io.tpd.superheroes.exceptions.NonExistingHeroException;
import io.tpd.superheroes.repository.SuperHeroRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@RunWith(MockitoJUnitRunner.class)
public class SuperHeroControllerMockMvcStandaloneTest {

    private MockMvc mvc;

    @Mock
    private SuperHeroRepository superHeroRepository;

    @InjectMocks
    private SuperHeroController superHeroController;


    @Before
    public void setup() {

        // MockMvc standalone approach
        mvc = MockMvcBuilders.standaloneSetup(superHeroController)
                .setControllerAdvice(new SuperHeroExceptionHandler())
                /*.addFilters(new SuperHeroFilter())*/
                .build();
    }

    @Test
    public void canRetrieveByIdWhenExists() throws Exception {
        // given
        given(superHeroRepository.getSuperHero(2))
                .willReturn(new SuperHero("James", "Bond", "LosAngles"));

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/superheroes/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        JSONAssert.assertEquals("{\"firstName\":\"James\",\"lastName\":\"Bond\",\"city\":\"LosAngles\"}", response.getContentAsString(), false);
        verify(superHeroRepository).getSuperHero(2);
    }

    @Test()
    public void canRetrieveByIdWhenDoesNotExist() throws Exception {

        // given
        given(superHeroRepository.getSuperHero(2))
                .willThrow(new NonExistingHeroException());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/superheroes/2")
                        .accept(MediaType.APPLICATION_JSON)).andDo(print())
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();

        verify(superHeroRepository).getSuperHero(2);

    }

    @Test
    public void canRetrieveByNameWhenExists() throws Exception {
        // given
        given(superHeroRepository.getSuperHero("Bond"))
                .willReturn(Optional.of( new SuperHero("James", "Bond", "LosAngles")));


        // when
        MockHttpServletResponse response = mvc.perform(
                get("/superheroes/?name=LosAngles")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        /*assertThat(response.getContentAsString()).isEqualTo(
                jsonSuperHero.write(new SuperHero("Rob", "Mannon", "RobotMan")).getJson()
        );*/
    }

    @Test
    public void canRetrieveByNameWhenDoesNotExist() throws Exception {
        // given
        given(superHeroRepository.getSuperHero("RobotMan"))
                .willReturn(Optional.empty());

        // when
        MockHttpServletResponse response = mvc.perform(
                get("/superheroes/?name=RobotMan")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentAsString()).isEqualTo("null");
    }

    //asJsonString is being used in so many code examples.
   @Test
    public void canCreateANewSuperHero() throws Exception {
        // when
        MockHttpServletResponse response = mvc.perform(
                           post("/superheroes/").contentType(MediaType.APPLICATION_JSON)
                          .content(asJsonString(new SuperHero("Terry", "Martin", "St.louis"))))
                          .andDo(print())
                          .andReturn().getResponse();
        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
    }

    @Test
    public void headerIsPresent() throws Exception {
        // when
        MockHttpServletResponse response = mvc.perform(
                get("/superheroes/2")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getHeaders("X-SUPERHERO-APP")).containsOnly("super-header");
    }


    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}