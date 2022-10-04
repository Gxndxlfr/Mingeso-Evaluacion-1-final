package com.example.evaluacion1.services;

import com.example.evaluacion1.entities.*;
import com.example.evaluacion1.repositories.PlanillaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@Service
public class PlanillaService {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private AsistenciaService asistenciaService;

    @Autowired
    private MarcasRelojService marcasRelojService;

    @Autowired
    private InasistenciaService inasistenciaService;

    @Autowired
    private JustificativosService justificativoService;

    @Autowired
    private AutorizacionService autorizacionService;
    @Autowired
    private PlanillaRepository planillaRepository;
    public ArrayList<PlanillaEntity> calcularSueldos() throws ParseException {

        //Recorrer Empleados
        ArrayList<EmpleadoEntity> empleados=empleadoService.obtenerEmpleados();

        ArrayList<MarcasRelojEntity> marcasReloj =marcasRelojService.obtenerMarcasReloj();

        int lenMarcas = marcasReloj.size();
        String fechaActual = marcasReloj.get(lenMarcas-1).getFecha();

        ArrayList<PlanillaEntity> planillas = new ArrayList<>();
        for(EmpleadoEntity e:empleados){

            PlanillaEntity newPlanilla = new PlanillaEntity();

            newPlanilla.setRut(e.getRut());

            String nombre = e.getNombre() + " " + e.getApellidos();
            newPlanilla.setNombre(nombre);

            newPlanilla.setCategoria(e.getCategoria());

            //String fecha acual pasarlo a timestamp
            Date fechaBase;
            SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
            fechaBase = formato.parse(fechaActual);
            long diff = fechaBase.getTime() - e.getFecha_in().getTime();
            int yearsService = (int) (diff/(1000l * 60 * 60 * 24 * 365));

            newPlanilla.setYearsService( yearsService);
            double sueldoF = 0;
            if (e.getCategoria().equals("1")){
                sueldoF = 1700000.0;

            }if (e.getCategoria().equals("2")){
                sueldoF = 1200000.0;
            }if (e.getCategoria().equals("3")){
                sueldoF = 800000.0;
            }


            newPlanilla.setSueldoFijo(sueldoF);


            //pago horas extras
            ArrayList<AsistenciaEntity> horasExtra = asistenciaService.obtenerAsistencias();
            int horasExtrasEmpleado = 0;

            AutorizacionEntity autorizacion = autorizacionService.obtenerAutorizacionByRut(e.getRut());
            if(autorizacion != null) {
                for (AsistenciaEntity he : horasExtra) {


                    if (he.getHoras() > 10 && he.getRut().equals(e.getRut())) {
                        horasExtrasEmpleado = horasExtrasEmpleado + (he.getHoras() - 10);
                    }

                }
            }
            int pagoHorasExtras = 0;
            if (e.getCategoria().equals("1")){
                pagoHorasExtras = 25000*horasExtrasEmpleado;

            }if (e.getCategoria().equals("2")){
                pagoHorasExtras = 20000*horasExtrasEmpleado;
            }if (e.getCategoria().equals("3")){
                pagoHorasExtras = 10000*horasExtrasEmpleado;
            }

            newPlanilla.setPagoHorasExtra(pagoHorasExtras);


            //Descuento
            ArrayList<AsistenciaEntity> atrasos = asistenciaService.obtenerAsistencias();

            double montoDescuento = 0;
            for(AsistenciaEntity a:atrasos){
                int minAtrasos = a.getAtraso();
                if(minAtrasos > 10 && a.getRut().equals(e.getRut())){

                    if(minAtrasos > 10 && minAtrasos <= 25){
                        sueldoF =  (sueldoF - (sueldoF*0.01));
                        montoDescuento =  (montoDescuento + (sueldoF*0.01));
                    }if(minAtrasos > 25 && minAtrasos <= 45){
                        sueldoF = (sueldoF - (sueldoF*0.03));
                        montoDescuento =  (montoDescuento + (sueldoF*0.03));
                    }if(minAtrasos > 45 && minAtrasos <= 70){
                        sueldoF = (sueldoF - (sueldoF*0.06));
                        montoDescuento =  (montoDescuento + (sueldoF*0.06));
                    }if(minAtrasos > 70){
                        InasistenciaEntity inasistencia = new InasistenciaEntity();
                        inasistencia.setFecha(a.getFecha());
                        inasistencia.setRut(e.getRut());

                        inasistenciaService.guardarInasistencia(inasistencia);

                        JustificativoEntity justf = justificativoService.obtenerJustificativoPorRutYFecha(e.getRut(),a.getFecha());

                        if(justf == null){
                            sueldoF =  (sueldoF - (sueldoF*0.15));
                            montoDescuento =  (montoDescuento + (sueldoF*0.15));
                        }
                    }
                }
            }
            newPlanilla.setDescuento(montoDescuento);

            double bonificacion = 0;
            if (yearsService < 5){
                bonificacion = 0;
            }
            else if (yearsService >= 5 && yearsService < 10){
                bonificacion = sueldoF*0.05;
            }
            else if (yearsService >= 10 && yearsService < 15){
                bonificacion =  sueldoF*0.08;
            }
            else if (yearsService >= 15 && yearsService < 20){
                bonificacion =sueldoF*0.11;
            }
            else if (yearsService >= 20 && yearsService < 25){
                bonificacion =sueldoF*0.14;
            }
            else if (yearsService >= 25){
                bonificacion = sueldoF*0.17;
            }

            newPlanilla.setBonificacionYears(bonificacion);


            double sueldoFinal = sueldoF + bonificacion + pagoHorasExtras;
            newPlanilla.setSueldoBruto(sueldoFinal);
            double valorAux = sueldoFinal;
            sueldoFinal = (sueldoFinal - (valorAux*0.1) - (valorAux*0.08));

            newPlanilla.setCotizacionSalud( (valorAux*0.08));
            newPlanilla.setCotizacionProvicional((valorAux*0.1));

            newPlanilla.setSueldoFinal(sueldoFinal);

            planillas.add(newPlanilla);


        }

        return planillas;
    }

    public PlanillaEntity guardarPlanilla(PlanillaEntity p) {return planillaRepository.save(p);}


    public void calcularDatosParaSueldo() {

        //- getALL (marcasReloj, empleados, justificativos, confirmación horas extra)
        ArrayList<MarcasRelojEntity> marcasReloj =marcasRelojService.obtenerMarcasReloj();
        ArrayList<EmpleadoEntity> empleados=empleadoService.obtenerEmpleados();
        ArrayList<JustificativoEntity> justificativos =justificativoService.obtenerJustificativos();
        ArrayList<AutorizacionEntity> autorizaciones = autorizacionService.obtenerAutorizaciones();

        Date fechaInicial = marcasReloj.get(0).getFechaH();
        String fechaInicialStr = marcasReloj.get(0).getFecha();

        Date fecha = fechaInicial;
        String fechaStr = fechaInicialStr;

        int diaRevisado = fechaInicial.getDate();
        int contador = 0;

        ArrayList<MarcasRelojEntity> marcasPorDia= new ArrayList<>();
        ArrayList<Integer> marcasRevisadas = marcasRelojService.crearMarcasRevisadas(marcasReloj.size());

        for(MarcasRelojEntity m:marcasReloj){
            if(marcasRevisadas.get(Math.toIntExact(m.getId())-1).equals(0)){

                //revisar quien faltó el día anterior y dejar registro
                if(m.getFechaH().getDate() != diaRevisado){
                    ArrayList<InasistenciaEntity> inasistencias = inasistenciaService.marcarInasistencias(marcasPorDia,empleados);

                    for (InasistenciaEntity i:inasistencias){
                        inasistenciaService.guardarInasistencia(i);
                    }

                    marcasPorDia.clear();
                    diaRevisado = m.getFechaH().getDate();
                    fechaStr = m.getFecha();
                }
                //Asignar Fecha
                int idMarcaReloj = Math.toIntExact(m.getId());
                //fecha no revisada
                if (marcasRevisadas.get(idMarcaReloj-1) == 0){
                    MarcasRelojEntity marca_2 = new MarcasRelojEntity();
                    for (MarcasRelojEntity mr:marcasReloj){
                        if(m.getFecha().equals(mr.getFecha())){
                            if(m.getRut().equals(mr.getRut())){
                                Date fecha1 = m.getFechaH();
                                Date fecha2 = mr.getFechaH();





                                if (!(fecha2.equals(fecha1))){


                                    //Calcular info para planilla con nueva entidad asistencia
                                    AsistenciaEntity as = asistenciaService.crearAsistencia(m,mr);
                                    asistenciaService.guardarAsistencia(as);
                                    //marcar como revisada fecha 1 y 2
                                    int idMarcaReloj_2 = Math.toIntExact(mr.getId());
                                    marcasRevisadas.set(idMarcaReloj_2-1,1);
                                    marcasRevisadas.set(idMarcaReloj-1,1);
                                    marcasPorDia.add(m);
                                }


                            }
                        }
                    }
                }
            }
        }
        ArrayList<InasistenciaEntity> inasistencias = inasistenciaService.marcarInasistencias(marcasPorDia,empleados);
        for (InasistenciaEntity i:inasistencias){
            inasistenciaService.guardarInasistencia(i);
        }
    }
}
