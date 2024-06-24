package com.pmf.awp.project.model;

import com.pmf.awp.project.util.Brief;
import com.pmf.awp.project.util.Exporter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "task")
@Getter
@Setter
@Exporter
@Builder
@AllArgsConstructor
public class Task extends Auditable {
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
    @JoinColumn(name = "phase_id")
    @Brief(mappedBy = "id", name = "phaseId")
    private Phase phase;

    public Task() {
    }
}
