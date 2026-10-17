package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.MovimientoNotaServices;
import com.example.gestionacademica.domain.models.MovimientoNota;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movimiento-notas")
public class MovimientoNotaController {

    private final MovimientoNotaServices movimientoNotaServices;

    public MovimientoNotaController(MovimientoNotaServices movimientoNotaServices) {
        this.movimientoNotaServices = movimientoNotaServices;
    }

    @PostMapping
    public ResponseEntity<MovimientoNota> crear(@Valid @RequestBody MovimientoNota movimientoNota) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoNotaServices.crear(movimientoNota));
    }

    @GetMapping
    public List<MovimientoNota> listar() {
        return movimientoNotaServices.obtenerTodos();
    }

    @GetMapping("/nota/{idNota}")
    public List<MovimientoNota> listarPorNota(@PathVariable Long idNota) {
        return movimientoNotaServices.obtenerPorNota(idNota);
    }

    @GetMapping("/{id}")
    public MovimientoNota obtener(@PathVariable Long id) {
        return movimientoNotaServices.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public MovimientoNota actualizar(@PathVariable Long id, @Valid @RequestBody MovimientoNota datos) {
        return movimientoNotaServices.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        movimientoNotaServices.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
