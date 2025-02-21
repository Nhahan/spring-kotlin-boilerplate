package com.skill.job.member.entity

import com.skill.job.common.entity.BaseEntity
import jakarta.persistence.*

@Entity
@Table(name = "corporate_members")
class CorporateMember(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(unique = true)
    val email: String,
    val password: String,
    val phone: String? = null,
    val companyName: String,
    val companyRegistrationNumber: String,
    val companyAddress: String? = null
) : BaseEntity()
