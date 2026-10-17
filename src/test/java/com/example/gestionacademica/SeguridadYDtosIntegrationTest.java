package com.example.gestionacademica;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Inicio de sesión, permisos por rol y formato de las respuestas DTO contra una BD H2 en memoria
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/datos-prueba.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
class SeguridadYDtosIntegrationTest {

    private static final String DEMO = "Demo1234*";

    @Autowired
    private MockMvc mockMvc;

    // ---------- Inicio de sesión ----------

    @Test
    void loginCorrectoDevuelveTokenYDatosDelUsuario() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"est001\",\"password\":\"" + DEMO + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.usuario.rol").value("ESTUDIANTE"))
                .andExpect(jsonPath("$.usuario.idEstudiante").value("EST001"))
                .andExpect(jsonPath("$.usuario.passwordHash").doesNotExist());
    }

    @Test
    void loginConClaveErradaDa401() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"est001\",\"password\":\"otra\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sinTokenDa401() throws Exception {
        mockMvc.perform(get("/api/carreras")).andExpect(status().isUnauthorized());
    }

    @Test
    void adminInicialSeCreaAlArrancar() throws Exception {
        mockMvc.perform(conToken(get("/api/auth/me"), login("admin", "Admin123*")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rol").value("ADMINISTRATIVO"));
    }

    // ---------- ESTUDIANTE ----------

    @Test
    void estudianteSoloVeSuInformacion() throws Exception {
        String token = login("est001", DEMO);
        mockMvc.perform(conToken(get("/api/notas/estudiante/EST001"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/reportes/boletin/EST001"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/notas/1"), token)).andExpect(status().isOk());

        mockMvc.perform(conToken(get("/api/notas/estudiante/EST002"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/notas/3"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/estudiantes"), token)).andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/carreras"), token)).andExpect(status().isOk());
        mockMvc.perform(conToken(post("/api/carreras").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idCarrera\":\"X\",\"nombreCarrera\":\"X\"}"), token))
                .andExpect(status().isForbidden());
    }

    // ---------- PROFESOR ----------

    @Test
    void profesorCalificaSoloSusMaterias() throws Exception {
        String token = login("profesor1", DEMO); // dicta MAT001 en GRP001

        // Nota 1 es de MAT001: puede calificar y la definitiva se recalcula
        mockMvc.perform(conToken(post("/api/movimiento-notas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaRegistro\":\"2026-03-10\",\"calificacion\":4.0,\"idNota\":1}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idNota").value(1))
                .andExpect(jsonPath("$.idMateria").value("MAT001"));
        mockMvc.perform(conToken(get("/api/notas/1"), token))
                .andExpect(jsonPath("$.definitiva").value(4.0))
                .andExpect(jsonPath("$.aprobada").value(true));

        // Nota 2 es de MAT002, que dicta otro profesor
        mockMvc.perform(conToken(post("/api/movimiento-notas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaRegistro\":\"2026-03-10\",\"calificacion\":4.0,\"idNota\":2}"), token))
                .andExpect(status().isForbidden());

        mockMvc.perform(conToken(get("/api/reportes/grupo/GRP001/planilla-notas?idMateria=MAT001&anio=2026"), token))
                .andExpect(status().isOk());
        mockMvc.perform(conToken(get("/api/reportes/grupo/GRP001/planilla-notas?idMateria=MAT002&anio=2026"), token))
                .andExpect(status().isForbidden());
        mockMvc.perform(conToken(get("/api/usuarios"), token)).andExpect(status().isForbidden());
    }

    // ---------- COORDINADOR / ADMINISTRATIVO ----------

    @Test
    void coordinadorGestionaLoAcademicoPeroNoLosUsuarios() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(post("/api/materias").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idMateria\":\"MAT900\",\"nombreMateria\":\"Física I\"}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombreMateria").value("Física I"));
        mockMvc.perform(conToken(get("/api/usuarios"), token)).andExpect(status().isForbidden());
    }

    @Test
    void administrativoCreaUsuariosVinculados() throws Exception {
        String token = login("admin", "Admin123*");
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"profesor2\",\"password\":\"Clave1234\",\"rol\":\"PROFESOR\","
                                + "\"activo\":true,\"idProfesor\":\"PROF002\"}"), token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idProfesor").value("PROF002"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        // Un PROFESOR sin profesor vinculado es inválido
        mockMvc.perform(conToken(post("/api/usuarios").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"malo\",\"password\":\"Clave1234\",\"rol\":\"PROFESOR\",\"activo\":true}"), token))
                .andExpect(status().isConflict());

        login("profesor2", "Clave1234");
    }

    // ---------- DTOs ----------

    @Test
    void respuestasSonDtosPlanosSinEntidadesAnidadas() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(get("/api/notas/3"), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreEstudiante").value("María López"))
                .andExpect(jsonPath("$.nombreMateria").value("Cálculo I"))
                .andExpect(jsonPath("$.definitiva").value(nullValue()))
                .andExpect(jsonPath("$.estudiante").doesNotExist())
                .andExpect(jsonPath("$.hibernateLazyInitializer").doesNotExist());

        mockMvc.perform(conToken(get("/api/grupos/GRP001"), token))
                .andExpect(jsonPath("$.nombreNivel").value("Primer semestre"));
    }

    @Test
    void referenciaInexistenteDa404() throws Exception {
        String token = login("coordinador", DEMO);
        mockMvc.perform(conToken(post("/api/grupos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idGrupo\":\"G9\",\"nombreGrupo\":\"G9\",\"idNivel\":\"NO-EXISTE\"}"), token))
                .andExpect(status().isNotFound());
    }

    private String login(String username, String password) throws Exception {
        String body = mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(body, "$.token");
    }

    private MockHttpServletRequestBuilder conToken(MockHttpServletRequestBuilder request, String token) {
        return request.header("Authorization", "Bearer " + token);
    }
}
