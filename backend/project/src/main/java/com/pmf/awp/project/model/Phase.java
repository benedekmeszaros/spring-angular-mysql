package com.pmf.awp.project.model;

import java.util.List;

import com.pmf.awp.project.util.Brief;
import com.pmf.awp.project.util.Exporter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "phase")
@Getter
@Setter
@Builder
@AllArgsConstructor
@Exporter
public class Phase extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int position;

    @Column(nullable = false)
    private String label;

    @Column(nullable = true)
    private String description;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @Brief(mappedBy = "id", name = "boardId")
    private Board board;

    @OneToMany(mappedBy = "phase", cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    private List<Task> tasks;

    public Phase() {

    }
}
