package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.GrupoServices;
import com.example.gestionacademica.application.Services.InscripcionServices;
import com.example.gestionacademica.domain.models.Grupo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grupos")
public class GrupoController {

    private final GrupoServices grupoServices;
    private final InscripcionServices inscripcionServices;

    public GrupoController(GrupoServices grupoServices, InscripcionServices inscripcionServices) {
        this.grupoServices = grupoServices;
        this.inscripcionServices = inscripcionServices;
    }

    @PostMapping
    public ResponseEntity<Grupo> crear(@Valid @RequestBody Grupo grupo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoServices.crear(grupo));
    }

    @GetMapping
    public List<Grupo> listar() {
        return grupoServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Grupo obtener(@PathVariable String id) {
        return grupoServices.obtenerPorId(id);
    }

    // Cupo del grupo para un año (por defecto el año actual)
    @GetMapping("/{id}/cupo")
    public Map<String, Object> cupo(@PathVariable String id, @RequestParam(required = false) Integer anio) {
        if (anio == null) {
            anio = LocalDate.now().getYear();
        }
        return inscripcionServices.consultarCupo(id, anio);
    }

    @PutMapping("/{id}")
    public Grupo actualizar(@PathVariable String id, @Valid @RequestBody Grupo datos) {
        return grupoServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        grupoServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
