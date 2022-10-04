package com.example.evaluacion1.services;


import com.example.evaluacion1.entities.EmpleadoEntity;
import com.example.evaluacion1.entities.InasistenciaEntity;
import com.example.evaluacion1.entities.MarcasRelojEntity;
import com.example.evaluacion1.repositories.InasistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class InasistenciaService {
    @Autowired
    InasistenciaRepository inasistenciaRepository;

    public ArrayList<InasistenciaEntity> marcarInasistencias(ArrayList<MarcasRelojEntity> marcasPorDia, ArrayList<EmpleadoEntity> empleados) {

        ArrayList<InasistenciaEntity> inasistencias = new ArrayList<>();
        for(EmpleadoEntity e:empleados){
            String rut = e.getRut();
            String fecha="" ;
            int encontrado = 0;
            for(MarcasRelojEntity m:marcasPorDia){
                fecha = m.getFecha();
                if (m.getRut() == e.getRut()){
                    encontrado = 1;

                }
            }
            if (encontrado == 0){

                InasistenciaEntity in = new InasistenciaEntity();
                in.setRut(rut);
                in.setFecha(fecha);
                inasistencias.add(in);
            }
        }
        return inasistencias;

    }
    public InasistenciaEntity guardarInasistencia(InasistenciaEntity in) {return inasistenciaRepository.save(in);}



}
