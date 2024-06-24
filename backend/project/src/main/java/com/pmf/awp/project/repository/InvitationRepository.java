package com.pmf.awp.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pmf.awp.project.model.Invitation;

@Repository
public interface InvitationRepository extends JpaRepository<Invitation, Integer> {
    @Query("select i from Invitation i where i.user.id = :userId and i.status = 0 order by i.sentAt")
    List<Invitation> getAllPendingByUser(Integer userId);

    @Query("select exists (select i from Invitation i where i.board.id = :boardId and i.user.id = :userId and i.status = 0)")
    boolean existsPendingByUser(Integer boardId, Integer userId);
}
