package com.pmf.awp.project.model;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.pmf.awp.project.util.Brief;
import com.pmf.awp.project.util.Deep;
import com.pmf.awp.project.util.Exporter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "membership")
@Data
@Builder
@AllArgsConstructor
@Exporter
public class Membership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private Access access;

    @Column(name = "joined_at")
    @CreationTimestamp
    private Date joinedAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Deep
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @Brief(mappedBy = "id", name = "boardId")
    private Board board;

    public Membership() {
    }
}
