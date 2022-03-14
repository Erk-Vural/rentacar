package com.erkvural.rentacar.service.car;

import com.erkvural.rentacar.core.exception.BusinessException;
import com.erkvural.rentacar.core.utils.mapping.ModelMapperService;
import com.erkvural.rentacar.core.utils.results.DataResult;
import com.erkvural.rentacar.core.utils.results.Result;
import com.erkvural.rentacar.core.utils.results.SuccessDataResult;
import com.erkvural.rentacar.core.utils.results.SuccessResult;
import com.erkvural.rentacar.dto.car.create.CarMaintenanceCreateDto;
import com.erkvural.rentacar.dto.car.get.CarMaintenanceGetDto;
import com.erkvural.rentacar.dto.car.update.CarMaintenanceUpdateDto;
import com.erkvural.rentacar.entity.car.CarMaintenance;
import com.erkvural.rentacar.entity.car.CarRental;
import com.erkvural.rentacar.repository.car.CarMaintenanceRepository;
import com.erkvural.rentacar.repository.car.CarRentalRepository;
import com.erkvural.rentacar.repository.car.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CarMaintenanceServiceImpl implements CarMaintenanceService {
    private final CarMaintenanceRepository carMaintenanceRepository;
    private final CarRepository carRepository;
    private final CarRentalRepository carRentalRepository;
    private final ModelMapperService modelMapperService;

    @Autowired
    public CarMaintenanceServiceImpl(CarMaintenanceRepository carMaintenanceRepository, CarRepository carRepository, CarRentalRepository carRentalRepository, ModelMapperService modelMapperService) {
        this.carMaintenanceRepository = carMaintenanceRepository;
        this.carRepository = carRepository;
        this.carRentalRepository = carRentalRepository;
        this.modelMapperService = modelMapperService;
    }

    @Override
    public Result add(CarMaintenanceCreateDto carMaintenanceCreateDto) throws BusinessException {
        checkCarIdExist(carMaintenanceCreateDto.getCarId());

        CarMaintenance carMaintenance = this.modelMapperService.forRequest().map(carMaintenanceCreateDto, CarMaintenance.class);
        this.carMaintenanceRepository.save(carMaintenance);

        return new SuccessResult("Success, Car Maintenance added: " + carMaintenance);
    }

    @Override
    public DataResult<List<CarMaintenanceGetDto>> getAll() {
        List<CarMaintenance> result = carMaintenanceRepository.findAll();

        List<CarMaintenanceGetDto> response = result.stream()
                .map(carMaintenance -> modelMapperService.forDto()
                        .map(carMaintenance, CarMaintenanceGetDto.class))
                .collect(Collectors.toList());

        return new SuccessDataResult<>("Success, All Car Maintenances listed.", response);
    }

    @Override
    public DataResult<CarMaintenanceGetDto> getById(long id) throws BusinessException {
        checkCarMaintenanceIdExist(id);

        CarMaintenance carMaintenance = carMaintenanceRepository.findById(id);
        CarMaintenanceGetDto response = modelMapperService.forDto().map(carMaintenance, CarMaintenanceGetDto.class);

        return new SuccessDataResult<>("Success, Car Maintenance with requested ID found.", response);
    }

    @Override
    public SuccessDataResult<List<CarMaintenanceGetDto>> getByCarId(long carId) {

        List<CarMaintenance> result = this.carMaintenanceRepository.findByCar_Id(carId);

        List<CarMaintenanceGetDto> response = result.stream().map(carMaintenance -> this.modelMapperService.forDto()
                .map(carMaintenance, CarMaintenanceGetDto.class)).collect(Collectors.toList());

        return new SuccessDataResult<>("Success,  Car Maintenance with requested carID found", response);
    }

    @Override
    public DataResult<List<CarMaintenanceGetDto>> getAllPaged(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        List<CarMaintenance> result = this.carMaintenanceRepository.findAll(pageable).getContent();
        List<CarMaintenanceGetDto> response = result.stream()
                .map(carMaintenance -> this.modelMapperService.forDto()
                        .map(carMaintenance, CarMaintenanceGetDto.class))
                .collect(Collectors.toList());

        return new SuccessDataResult<>("GetAllPaged Results Listed.", response);
    }

    @Override
    public DataResult<List<CarMaintenanceGetDto>> getAllSorted(Sort.Direction direction) {
        Sort s = Sort.by(direction, "returnDate");

        List<CarMaintenance> result = this.carMaintenanceRepository.findAll(s);
        List<CarMaintenanceGetDto> response = result.stream()
                .map(carMaintenance -> this.modelMapperService.forDto()
                        .map(carMaintenance, CarMaintenanceGetDto.class))
                .collect(Collectors.toList());

        return new SuccessDataResult<>("GetAllSorted Results Listed.", response);
    }

    @Override
    public Result update(long id, CarMaintenanceUpdateDto carMaintenanceUpdateDto) throws BusinessException {
        checkCarMaintenanceIdExist(id);
        checkIsRented(carMaintenanceUpdateDto.getCarId());

        CarMaintenance carMaintenance = this.modelMapperService.forRequest().map(carMaintenanceUpdateDto, CarMaintenance.class);

        this.carMaintenanceRepository.save(carMaintenance);

        return new SuccessResult("Car maintenance updated: " + carMaintenance);
    }

    @Override
    public Result delete(long id) throws BusinessException {
        checkCarMaintenanceIdExist(id);

        this.carRepository.deleteById(id);

        return new SuccessResult("Success, Car Maintenance deleted with requested ID: " + id);
    }

    private void checkCarIdExist(long id) throws BusinessException {
        if (Objects.nonNull(carRepository.findById(id)))
            throw new BusinessException("Can't find Car with id: " + id);
    }

    private void checkCarMaintenanceIdExist(long id) throws BusinessException {
        if (Objects.nonNull(carMaintenanceRepository.findById(id)))
            throw new BusinessException("Can't find Car Maintenance with id: " + id);
    }

    private void checkIsRented(long id) throws BusinessException {
        List<CarRental> result = this.carRentalRepository.findByCar_Id(id);
        if (result != null) {
            for (CarRental carRental : result) {
                if (carRental.getEndDate() != null) {
                    throw new BusinessException("Car is already rented!");
                }
            }
        }
    }
}
