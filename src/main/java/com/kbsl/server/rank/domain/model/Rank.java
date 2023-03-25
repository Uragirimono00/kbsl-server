package com.kbsl.server.rank.domain.model;

import com.kbsl.server.boot.domain.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

@Entity
@Table(name = "tb_rank")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
public class Rank extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;
}
