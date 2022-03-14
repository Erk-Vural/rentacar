package com.erkvural.rentacar.service.car;

import com.erkvural.rentacar.core.exception.BusinessException;
import com.erkvural.rentacar.core.utils.mapping.ModelMapperService;
import com.erkvural.rentacar.dto.car.create.OrderedAdditionalServiceCreateDto;
import com.erkvural.rentacar.entity.car.OrderedAdditionalService;
import com.erkvural.rentacar.repository.car.CarRentalRepository;
import com.erkvural.rentacar.repository.car.OrderedAdditionalServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class OrderedAdditionalServiceServiceImpl implements OrderedAdditionalServiceService {
    private final OrderedAdditionalServiceRepository orderedAdditionalServiceRepository;
    private final ModelMapperService modelMapperService;
    private final CarRentalRepository carRentalRepository;

    @Autowired
    public OrderedAdditionalServiceServiceImpl(OrderedAdditionalServiceRepository orderedAdditionalServiceRepository, ModelMapperService modelMapperService, CarRentalRepository carRentalRepository) {
        this.orderedAdditionalServiceRepository = orderedAdditionalServiceRepository;
        this.modelMapperService = modelMapperService;
        this.carRentalRepository = carRentalRepository;
    }

    @Override
    public void add(Set<OrderedAdditionalServiceCreateDto> orderedAdditionalServiceCreateDtos, long carRentalId) throws BusinessException {
        for (OrderedAdditionalServiceCreateDto requests : orderedAdditionalServiceCreateDtos) {
            OrderedAdditionalService orderedAdditionalService = this.modelMapperService.forRequest().map(requests, OrderedAdditionalService.class);
            orderedAdditionalService.setCarRental(carRentalRepository.findById(carRentalId));
            this.orderedAdditionalServiceRepository.save(orderedAdditionalService);
        }
    }

    @Override
    public Set<OrderedAdditionalService> getByCarRentalId(long carRentalId) {
        return this.orderedAdditionalServiceRepository.findByCarRental_Id(carRentalId);
    }

    @Override
    public Double calculateBill(List<OrderedAdditionalService> orderedAdditionalServices) {
        double tempBill = 0;

        for (OrderedAdditionalService orderedAdditionalService : orderedAdditionalServices) {
            tempBill += orderedAdditionalService.getQuantity() * orderedAdditionalService.getAdditionalService().getDailyPrice();
        }

        return tempBill;
    }
}
