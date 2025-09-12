package com.erp_mes.mes.lot.entitiy;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.erp_mes.mes.lot.entity.LotMaster;

import java.time.LocalDateTime;

@Entity
@Table(name = "lot_material_usage")
@Getter 
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class LotMaterialUsage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "usage_id")
  private Long usageId;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "parent_lot_id", referencedColumnName = "lot_id", nullable = false)
  private LotMaster parentLot;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "child_lot_id", referencedColumnName = "lot_id", nullable = false)
  private LotMaster childLot;

  @Column(name = "qty_used", nullable = false)
  private Integer qtyUsed;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;
}
