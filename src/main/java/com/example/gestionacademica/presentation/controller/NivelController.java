package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NivelServices;
import com.example.gestionacademica.domain.models.Nivel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/niveles")
public class NivelController {

    private final NivelServices nivelServices;

    public NivelController(NivelServices nivelServices) {
        this.nivelServices = nivelServices;
    }

    @PostMapping
    public ResponseEntity<Nivel> crear(@Valid @RequestBody Nivel nivel) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nivelServices.crear(nivel));
    }

    @GetMapping
    public List<Nivel> listar() {
        return nivelServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Nivel obtener(@PathVariable String id) {
        return nivelServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Nivel actualizar(@PathVariable String id, @Valid @RequestBody Nivel datos) {
        return nivelServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        nivelServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
