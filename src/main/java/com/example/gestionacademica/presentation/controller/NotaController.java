package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.NotaServices;
import com.example.gestionacademica.domain.models.Nota;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notas")
public class NotaController {

    private final NotaServices notaServices;

    public NotaController(NotaServices notaServices) {
        this.notaServices = notaServices;
    }

    @PostMapping
    public ResponseEntity<Nota> crear(@Valid @RequestBody Nota nota) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notaServices.crear(nota));
    }

    @GetMapping
    public List<Nota> listar() {
        return notaServices.obtenerTodos();
    }

    @GetMapping("/estudiante/{idEstudiante}")
    public List<Nota> listarPorEstudiante(@PathVariable String idEstudiante) {
        return notaServices.obtenerPorEstudiante(idEstudiante);
    }

    @GetMapping("/estudiante/{idEstudiante}/promedio")
    public Map<String, Object> promedio(@PathVariable String idEstudiante) {
        return notaServices.calcularPromedio(idEstudiante);
    }

    @GetMapping("/{id}")
    public Nota obtener(@PathVariable Long id) {
        return notaServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Nota actualizar(@PathVariable Long id, @Valid @RequestBody Nota datos) {
        return notaServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        notaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
