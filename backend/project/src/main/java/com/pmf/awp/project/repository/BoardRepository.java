package com.pmf.awp.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pmf.awp.project.model.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Integer> {

    @Query("select exists (select b from Board b where b.title like :title and b.createdBy.id = :ownerId)")
    boolean existingTitleByOwner(String title, Integer ownerId);

    @Query("select exists (select b from Board b inner join b.members m where b.id = :boardId and m.user.id = :userId)")
    boolean existingMember(Integer boardId, Integer userId);

    @Query("select exists (select b from Board b inner join b.phases p where b.id = :boardId and p.label = :label)")
    boolean existingPhaseWithLabel(Integer boardId, String label);

    @Query("select b from Board b inner join b.members m where m.user.id = :userId order by b.updatedAt desc")
    List<Board> findAllWithMember(Integer userId);
}
