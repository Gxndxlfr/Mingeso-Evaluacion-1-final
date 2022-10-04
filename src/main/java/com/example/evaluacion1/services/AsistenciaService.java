package com.example.evaluacion1.services;


import com.example.evaluacion1.entities.AsistenciaEntity;
import com.example.evaluacion1.entities.MarcasRelojEntity;
import com.example.evaluacion1.repositories.AsistenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class AsistenciaService {

    @Autowired
    AsistenciaRepository asistenciaRepository;

    public Integer calcularDiferenciaHora(Date fecha1, Date fecha2){

        long diff = fecha1.getTime() - fecha2.getTime();
        var horas = diff/(1000*60*60);
        return Math.toIntExact(horas);
    }

    public AsistenciaEntity crearAsistencia(MarcasRelojEntity m, MarcasRelojEntity mr) {
        AsistenciaEntity newAs = new AsistenciaEntity();
        newAs.setRut(m.getRut());
        newAs.setIngreso(m.getFechaH());
        newAs.setSalida(mr.getFechaH());
        newAs.setFecha(m.getFecha());

        Date fecha1= mr.getFechaH();
        Date fecha2= m.getFechaH();
        Integer horas = calcularDiferenciaHora(fecha1,fecha2);
        newAs.setHoras(horas);

        String newFecha = m.getFecha() + " " + "08:00";
        Date fechaBase;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        try {
            fechaBase = formato.parse(newFecha);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Integer atraso = calcularDiferenciaHora(fecha2,fechaBase);
        newAs.setAtraso(atraso);

        return newAs;
    }

    public AsistenciaEntity guardarAsistencia(AsistenciaEntity as) { return asistenciaRepository.save(as);
    }

    public ArrayList<AsistenciaEntity> obtenerAsistencias() {
        return (ArrayList<AsistenciaEntity>) asistenciaRepository.findAll();
    }


}
