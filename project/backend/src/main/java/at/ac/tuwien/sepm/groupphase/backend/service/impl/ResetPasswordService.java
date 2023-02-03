package at.ac.tuwien.sepm.groupphase.backend.service.impl;

import at.ac.tuwien.sepm.groupphase.backend.entity.ResetPasswordToken;
import at.ac.tuwien.sepm.groupphase.backend.repository.ResetPasswordTokenRepository;
import org.springframework.stereotype.Service;


@Service
public class ResetPasswordService {
    private final ResetPasswordTokenRepository resetPasswordTokenRepository;

    public ResetPasswordService(ResetPasswordTokenRepository resetPasswordTokenRepository) {
        this.resetPasswordTokenRepository = resetPasswordTokenRepository;
    }

    public void saveResetPasswordToken(ResetPasswordToken token) {
        resetPasswordTokenRepository.save(token);
    }

}
