package com.erkvural.rentacar.core.pos.adapter;

import com.erkvural.rentacar.core.pos.service.PosService;
import com.erkvural.rentacar.core.pos.service.PosServiceIsBankImpl;
import com.erkvural.rentacar.core.utils.results.Result;
import com.erkvural.rentacar.dto.car.create.PaymentCreateRequest;

public class FakeIsBankServiceAdapter implements PosService {
    @Override
    public Result addPayment(PaymentCreateRequest createRequest) {
        PosServiceIsBankImpl posServiceIsBank = new PosServiceIsBankImpl();

        return posServiceIsBank.makePayment(createRequest.getCardInfo());
    }
}
