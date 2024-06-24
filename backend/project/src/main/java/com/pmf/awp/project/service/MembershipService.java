package com.pmf.awp.project.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.pmf.awp.project.exception.GenericHttpException;
import com.pmf.awp.project.model.Invitation;
import com.pmf.awp.project.model.Membership;
import com.pmf.awp.project.repository.InvitationRepository;
import com.pmf.awp.project.repository.MembershipRepository;

@Service
public class MembershipService {
    @Autowired
    private MembershipRepository membershipRepository;
    @Autowired
    private InvitationRepository invitationRepository;

    public Membership save(Membership member) {
        try {
            return membershipRepository.save(member);
        } catch (Exception e) {
            throw new GenericHttpException("Unable to create/update member", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Membership acceptInvitation(Invitation invitation) {
        if (invitation.getStatus() != Invitation.Status.PENDING)
            throw new GenericHttpException("Invitation is already " + invitation.getStatus().toString().toLowerCase(),
                    HttpStatus.FORBIDDEN);
        if (invitation.getExpireAt().compareTo(new Date()) < 0) {
            invitation.setStatus(Invitation.Status.EXPIRED);
            invitationRepository.save(invitation);
            throw new GenericHttpException("Invitation already expired", HttpStatus.FORBIDDEN);
        }
        Membership member = Membership.builder()
                .access(invitation.getAccess())
                .board(invitation.getBoard())
                .user(invitation.getUser())
                .build();
        invitation.setStatus(Invitation.Status.ACCEPTED);
        invitationRepository.save(invitation);
        return membershipRepository.save(member);
    }

    public void dennyInvitation(Invitation invitation) {
        if (invitation.getStatus() != Invitation.Status.PENDING)
            throw new GenericHttpException("Invitation is already " + invitation.getStatus().toString().toLowerCase(),
                    HttpStatus.FORBIDDEN);
        if (invitation.getExpireAt().compareTo(new Date()) < 0) {
            invitation.setStatus(Invitation.Status.EXPIRED);
            invitationRepository.save(invitation);
            throw new GenericHttpException("Invitation already expired", HttpStatus.FORBIDDEN);
        }
        invitation.setStatus(Invitation.Status.DENIED);
        invitationRepository.save(invitation);
    }

    public boolean remove(Membership membership) {
        try {
            membershipRepository.deleteById(membership.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GenericHttpException("Unable to delete member", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
