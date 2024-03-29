package com.example.demo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import com.example.demo.entity.Business;
import com.example.demo.entity.ProFamily;
//import com.example.demo.entity.Report;
import com.example.demo.entity.Servicio;
import com.example.demo.model.ServicioModel;
//import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.ServicioRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.service.ProFamilyService;
import com.example.demo.service.ServicioService;

@Configuration
@Service("servicioService")
public class ServicioServiceImpl implements ServicioService {

    private final ServicioRepository servicioRepository;
    
    
//    @Autowired
//    private ReportRepository reportRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private ProFamilyService proFamilyService;

    public ServicioServiceImpl(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }
    
    public Servicio model2entity(ServicioModel servicioModel) {
		ModelMapper mapper = new ModelMapper();
		return mapper.map(servicioModel, Servicio.class);
	}

	public ServicioModel entity2model(Servicio servicio) {
		ModelMapper mapper = new ModelMapper();
		return mapper.map(servicio, ServicioModel.class);
	}
	
	@Override
	public List<ServicioModel> getFilteredServices(String opcion, String filterBy) {
		List<ServicioModel> listServicios = new ArrayList<>();

		if (Integer.parseInt(opcion) != 0) {
	        ProFamily profam = proFamilyService.findById(Integer.parseInt(opcion));
	        String proFamName = profam.getName();
	        listServicios = findServiciosByProFamily(proFamName);
	    } else {
	        listServicios = getAllServicios();
	    }

	    return listServicios;
	}
	
	@Override
	public ServicioModel addServicio(ServicioModel servicioModel) {
	    Servicio servicio = model2entity(servicioModel);
	    servicio.setTitle(servicioModel.getTitle());
	    servicio.setDescription(servicioModel.getDescription());
	    servicio.setHappeningDate(servicioModel.getHappeningDate());
	    servicio.setRegisterDate(servicioModel.getRegisterDate());
	    servicio.setId(servicioModel.getId());
	    servicio.setBusinessId(servicioModel.getBusinessId());
	    servicio.setProfesionalFamilyId(servicioModel.getProfesionalFamilyId());
	    servicio.setStudentId(servicioModel.getStudentId());
	    servicio.setValoration(0);
	    servicio.setComment(null);
	    servicio.setDeleted(0);
	    servicio.setFinished(0);
	    
	    servicio = servicioRepository.save(servicio);

	    ServicioModel createdServicio = entity2model(servicio);

	    return createdServicio;    
	}


	@Override
	public int deleteServicio(int id) {
		Servicio servicio = servicioRepository.findById(id);

        if (servicio.getDeleted() == 0) {
        	servicio.setDeleted(1);
        	servicioRepository.save(servicio);
            return 1;
        } else
            return 0;
	}

	@Override
	public ServicioModel updateServicio(ServicioModel servicioModel) {
	    ServicioModel updatedServicio = null;
	    Servicio servicio = servicioRepository.findById(servicioModel.getId());

	    if (servicio != null && servicio.getBusinessId().getId() == servicioModel.getBusinessId().getId()) {
	        servicio.setTitle(servicioModel.getTitle());
	        servicio.setDescription(servicioModel.getDescription());
	        servicio.setHappeningDate(servicioModel.getHappeningDate());
	        servicio.setRegisterDate(servicioModel.getRegisterDate());
	        servicio.setProfesionalFamilyId(servicioModel.getProfesionalFamilyId());
	        servicio.setStudentId(servicioModel.getStudentId());

	        servicio = servicioRepository.save(servicio);

	        updatedServicio = entity2model(servicio);
	    }

	    return updatedServicio;
	}


	@Override
	public List<ServicioModel> findServiciosByProFamily(String familyName) {
	    List<ServicioModel> servicioModels = new ArrayList<>();

	    for (Servicio servicio : servicioRepository.findAll()) {
	        if (servicio.getProfesionalFamilyId() != null && servicio.getProfesionalFamilyId().getName().equals(familyName)) {
	            servicioModels.add(entity2model(servicio));
	        }
	    }

	    return servicioModels;
	}

	@Override
	public List<ServicioModel> getAllServicios() {
	    List<Servicio> servicios = servicioRepository.findAll();
	    List<ServicioModel> servicioModels = new ArrayList<>();

	    for (Servicio servicio : servicios) {
	        ServicioModel servicioModel = entity2model(servicio);
	        servicioModels.add(servicioModel);
	    }

	    return servicioModels;
	}

	@Override
	public List<ServicioModel> getServicesByBusinessId(Business business) {
		List<Servicio>modelServices=servicioRepository.findAll();
		List<ServicioModel>services=new ArrayList<>();
		for(Servicio servicio: modelServices) {
			if(servicio.getBusinessId()!=null && servicio.getBusinessId().getId() == business.getId()) {
			ServicioModel servicioModel = entity2model(servicio);
			services.add(servicioModel);
			}
		}
		
		return services;
		
	}

	@Override
	public ServicioModel getServicioById(int serviceId) {
	    Servicio servicio = servicioRepository.findById(serviceId);
	    return servicio != null ? entity2model(servicio) : null;
	}

	public List<ProFamily> getProfessionalFamiliesByBusinessId(List<Servicio>listServices){
		List<ProFamily>reports=new ArrayList<>();
		for(Servicio servicio: listServices) {
			reports.add(servicio.getProfesionalFamilyId());
		}
		Collections.sort(reports, Comparator.comparing(ProFamily::getName));
		return reports;
	}
}