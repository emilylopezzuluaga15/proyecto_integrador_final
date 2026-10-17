package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.CarreraServices;
import com.example.gestionacademica.domain.models.Carrera;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/carreras")
public class CarreraController {

    private final CarreraServices carreraServices;

    public CarreraController(CarreraServices carreraServices) {
        this.carreraServices = carreraServices;
    }

    @PostMapping
    public ResponseEntity<Carrera> crear(@Valid @RequestBody Carrera carrera) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carreraServices.crear(carrera));
    }

    @GetMapping
    public List<Carrera> listar() {
        return carreraServices.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Carrera obtener(@PathVariable String id) {
        return carreraServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Carrera actualizar(@PathVariable String id, @Valid @RequestBody Carrera datos) {
        return carreraServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        carreraServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
