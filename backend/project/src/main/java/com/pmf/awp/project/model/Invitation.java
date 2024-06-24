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
@Table(name = "invitation")
@Data
@AllArgsConstructor
@Builder
@Exporter
public class Invitation {
    public enum Status {
        PENDING,
        ACCEPTED,
        DENIED,
        EXPIRED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Status status;

    @Column
    @Enumerated(EnumType.ORDINAL)
    private Access access;

    @Column(name = "sent_at")
    @CreationTimestamp
    private Date sentAt;

    @Column(name = "expire_at")
    private Date expireAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @Brief(mappedBy = "id", name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "board_id")
    @Deep
    private Board board;

    public Invitation() {
    }
}
