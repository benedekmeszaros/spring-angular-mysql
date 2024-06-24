package com.pmf.awp.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Invitation;
import com.pmf.awp.project.repository.InvitationRepository;

@Service
public class InvitationService {
    @Autowired
    private InvitationRepository invitationRepository;

    public boolean existsPendingByUser(Integer boardId, Integer userId) {
        return invitationRepository.existsPendingByUser(boardId, userId);
    }

    public Invitation findById(Integer invitationId) {
        return invitationRepository.findById(invitationId)
                .orElseThrow(() -> new GenericHttpException("Invitation not found", HttpStatus.NOT_FOUND));
    }

    public Invitation save(Invitation invitation) {
        try {
            return invitationRepository.save(invitation);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to create/update invitation", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Invitation> findAllPendingByUser(Integer userId) {
        return invitationRepository.getAllPendingByUser(userId);
    }
}
