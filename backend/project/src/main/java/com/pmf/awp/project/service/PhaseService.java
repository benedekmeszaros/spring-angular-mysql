package com.pmf.awp.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Phase;
import com.pmf.awp.project.repository.PhaseRepository;

@Service
public class PhaseService {
    @Autowired
    private PhaseRepository phaseRepository;

    public boolean existingTaskWithLabel(Integer phaseId, String label) {
        return phaseRepository.existingTaskWithLabel(phaseId, label);
    }

    public Phase save(Phase phase) {
        try {
            return phaseRepository.save(phase);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to create/update phase", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Phase findById(Integer phaseId) {
        return phaseRepository.findById(phaseId)
                .orElseThrow(() -> new GenericHttpException("Phase not found", HttpStatus.NOT_FOUND));
    }

    public boolean remove(Phase phase) {
        try {
            phaseRepository.delete(phase);
            return true;
        } catch (Exception e) {
            throw new GenericHttpException("Unable to delete phase", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
