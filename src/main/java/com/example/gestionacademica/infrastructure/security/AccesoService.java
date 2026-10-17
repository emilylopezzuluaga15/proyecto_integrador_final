package com.example.gestionacademica.infrastructure.security;

import com.example.gestionacademica.domain.models.Asistencia;
import com.example.gestionacademica.domain.models.MovimientoNota;
import com.example.gestionacademica.domain.models.Nota;
import com.example.gestionacademica.domain.models.Rol;
import com.example.gestionacademica.domain.repository.AsignacionMateriaProfesorRepository;
import com.example.gestionacademica.domain.repository.AsistenciaRepository;
import com.example.gestionacademica.domain.repository.MovimientoNotaRepository;
import com.example.gestionacademica.domain.repository.NotaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

// Reglas de acceso que dependen de los datos, usadas desde @PreAuthorize como @acceso.<metodo>(...).
// ADMINISTRATIVO y COORDINADOR ("gestores") pueden todo lo académico.
// PROFESOR solo modifica notas/asistencia de las materias y grupos que tiene asignados.
// ESTUDIANTE solo consulta su propia información.
// Si el registro consultado no existe se permite el paso para que el servicio responda 404.
@Component("acceso")
@Transactional(readOnly = true)
public class AccesoService {

    public static final String CLAIM_ROL = "rol";
    public static final String CLAIM_ESTUDIANTE = "idEstudiante";
    public static final String CLAIM_PROFESOR = "idProfesor";

    private final NotaRepository notaRepository;
    private final MovimientoNotaRepository movimientoNotaRepository;
    private final AsistenciaRepository asistenciaRepository;
    private final AsignacionMateriaProfesorRepository asignacionRepository;

    public AccesoService(NotaRepository notaRepository,
                         MovimientoNotaRepository movimientoNotaRepository,
                         AsistenciaRepository asistenciaRepository,
                         AsignacionMateriaProfesorRepository asignacionRepository) {
        this.notaRepository = notaRepository;
        this.movimientoNotaRepository = movimientoNotaRepository;
        this.asistenciaRepository = asistenciaRepository;
        this.asignacionRepository = asignacionRepository;
    }

    // ADMINISTRATIVO, COORDINADOR o PROFESOR
    public boolean esPersonal(Authentication auth) {
        Rol rol = rol(auth);
        return rol != null && rol != Rol.ESTUDIANTE;
    }

    // ADMINISTRATIVO o COORDINADOR
    public boolean esGestor(Authentication auth) {
        Rol rol = rol(auth);
        return rol == Rol.ADMINISTRATIVO || rol == Rol.COORDINADOR;
    }

    public boolean puedeVerEstudiante(Authentication auth, String idEstudiante) {
        return esPersonal(auth) || esElEstudiante(auth, idEstudiante);
    }

    public boolean puedeVerNota(Authentication auth, Long idNota) {
        if (esPersonal(auth)) {
            return true;
        }
        Optional<Nota> nota = notaRepository.findById(idNota);
        return nota.isEmpty() || esElEstudiante(auth, nota.get().getEstudiante().getIdEstudiante());
    }

    public boolean puedeCalificarMateria(Authentication auth, String idMateria) {
        if (esGestor(auth)) {
            return true;
        }
        String idProfesor = idProfesor(auth);
        return idProfesor != null && asignacionRepository.existsByProfesorIdProfesorAndMateriaIdMateria(idProfesor, idMateria);
    }

    public boolean puedeModificarNota(Authentication auth, Long idNota) {
        if (esGestor(auth)) {
            return true;
        }
        if (idProfesor(auth) == null) {
            return false;
        }
        Optional<Nota> nota = notaRepository.findById(idNota);
        return nota.isEmpty() || puedeCalificarMateria(auth, nota.get().getMateria().getIdMateria());
    }

    public boolean puedeModificarMovimiento(Authentication auth, Long idMovimiento) {
        if (esGestor(auth)) {
            return true;
        }
        if (idProfesor(auth) == null) {
            return false;
        }
        Optional<MovimientoNota> movimiento = movimientoNotaRepository.findById(idMovimiento);
        return movimiento.isEmpty() || puedeModificarNota(auth, movimiento.get().getNota().getIdNota());
    }

    public boolean puedeGestionarGrupo(Authentication auth, String idGrupo) {
        if (esGestor(auth)) {
            return true;
        }
        String idProfesor = idProfesor(auth);
        return idProfesor != null && asignacionRepository.existsByProfesorIdProfesorAndGrupoIdGrupo(idProfesor, idGrupo);
    }

    public boolean puedeModificarAsistencia(Authentication auth, Long idAsistencia) {
        if (esGestor(auth)) {
            return true;
        }
        if (idProfesor(auth) == null) {
            return false;
        }
        Optional<Asistencia> asistencia = asistenciaRepository.findById(idAsistencia);
        return asistencia.isEmpty() || puedeGestionarGrupo(auth, asistencia.get().getGrupo().getIdGrupo());
    }

    public boolean puedeVerPlanilla(Authentication auth, String idGrupo, String idMateria) {
        if (esGestor(auth)) {
            return true;
        }
        String idProfesor = idProfesor(auth);
        return idProfesor != null
                && asignacionRepository.existsByProfesorIdProfesorAndGrupoIdGrupoAndMateriaIdMateria(idProfesor, idGrupo, idMateria);
    }

    private boolean esElEstudiante(Authentication auth, String idEstudiante) {
        return rol(auth) == Rol.ESTUDIANTE && idEstudiante != null && idEstudiante.equals(claim(auth, CLAIM_ESTUDIANTE));
    }

    private String idProfesor(Authentication auth) {
        return rol(auth) == Rol.PROFESOR ? claim(auth, CLAIM_PROFESOR) : null;
    }

    private Rol rol(Authentication auth) {
        String rol = claim(auth, CLAIM_ROL);
        return rol == null ? null : Rol.valueOf(rol);
    }

    private String claim(Authentication auth, String nombre) {
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString(nombre);
        }
        return null;
    }
}
