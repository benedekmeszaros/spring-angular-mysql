package com.pmf.awp.project.model;

import java.util.List;

import com.pmf.awp.project.util.Exclude;
import com.pmf.awp.project.util.Expand;
import com.pmf.awp.project.util.Exporter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "board")
@Builder
@AllArgsConstructor
@Getter
@Setter
@Exporter(expandandable = true)
public class Board extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Membership> members;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @OrderColumn(name = "position")
    private List<Phase> phases;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    @Exclude
    private List<Invitation> invitations;

    public Board() {

    }

    @Expand(name = "owner")
    public User getOwner() {
        return createdBy;
    }
}
