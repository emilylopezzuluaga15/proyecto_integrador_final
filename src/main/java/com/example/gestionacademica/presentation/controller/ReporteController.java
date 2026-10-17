package com.example.gestionacademica.presentation.controller;

import com.example.gestionacademica.application.Services.ReporteServices;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final ReporteServices reporteServices;

    public ReporteController(ReporteServices reporteServices) {
        this.reporteServices = reporteServices;
    }

    @GetMapping("/boletin/{idEstudiante}")
    public Map<String, Object> boletin(@PathVariable String idEstudiante) {
        return reporteServices.boletin(idEstudiante);
    }

    // anio es opcional: por defecto el año actual
    @GetMapping("/grupo/{idGrupo}/en-riesgo")
    public Map<String, Object> enRiesgo(@PathVariable String idGrupo, @RequestParam(required = false) Integer anio) {
        if (anio == null) {
            anio = LocalDate.now().getYear();
        }
        return reporteServices.estudiantesEnRiesgo(idGrupo, anio);
    }

    @GetMapping("/grupo/{idGrupo}/planilla-notas")
    public Map<String, Object> planillaNotas(@PathVariable String idGrupo,
                                             @RequestParam String idMateria,
                                             @RequestParam(required = false) Integer anio) {
        if (anio == null) {
            anio = LocalDate.now().getYear();
        }
        return reporteServices.planillaNotas(idGrupo, idMateria, anio);
    }
}
