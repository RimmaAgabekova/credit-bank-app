package ru.neoflex.gateway.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.gateway.feign.DealFeignClient;
import ru.neoflex.gateway.model.dto.FinishRegistrationRequestDto;

@Service
@RequiredArgsConstructor
public class DealService {

    private final DealFeignClient dealFeignClient;

    public void finishRegistration(String statementId, FinishRegistrationRequestDto finishRegistrationRequestDTO) {
        dealFeignClient.calculate(statementId, finishRegistrationRequestDTO);
    }

    public void createDocuments(String applicationId) {
        dealFeignClient.send(applicationId);
    }

    public void sendSesCode(String applicationId, String sesCode) {
        dealFeignClient.code(applicationId, sesCode);
    }

    public void signDocuments(String applicationId) {
        dealFeignClient.sign(applicationId);
    }
}
