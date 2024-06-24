package com.pmf.awp.project.model;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.pmf.awp.project.util.Brief;
import com.pmf.awp.project.util.Deep;
import com.pmf.awp.project.util.Exporter;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Exporter
public class Auditable {
    @CreatedBy
    @ManyToOne
    @JoinColumn(name = "created_by")
    @Brief(mappedBy = "id")
    protected User createdBy;

    @CreatedDate
    @Column(name = "created_at")
    protected Date createdAt;

    @LastModifiedBy
    @ManyToOne
    @JoinColumn(name = "updated_by")
    // @Brief(mappedBy = "id")
    @Deep
    protected User updatedBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    protected Date updatedAt;
}
