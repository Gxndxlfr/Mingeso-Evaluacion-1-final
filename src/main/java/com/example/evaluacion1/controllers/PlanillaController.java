package com.example.evaluacion1.controllers;

import com.example.evaluacion1.entities.PlanillaEntity;
import com.example.evaluacion1.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.text.ParseException;
import java.util.ArrayList;

@Controller
public class PlanillaController {

    @Autowired
    private MarcasRelojService marcasRelojService;
    @Autowired
    private AutorizacionService autorizacionService;
    @Autowired
    private JustificativosService justificativosService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private InasistenciaService inasistenciaService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private PlanillaService planillaService;

    @GetMapping("/mostrarPlanilla")
    public String mostrarPlanilla(Model model)  {

        planillaService.calcularDatosParaSueldo();


        //existen todas las asistencias
        ArrayList<PlanillaEntity> planillas = null;
        try {
            planillas = planillaService.calcularSueldos();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        for(PlanillaEntity p:planillas){
            planillaService.guardarPlanilla(p);
        }


        model.addAttribute("planillas",planillas);
        return "planilla";
    }
}
