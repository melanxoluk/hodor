package ru.melanxoluk.hodor.domain

import java.util.*


interface Domain

interface IdDomain<ID: Comparable<ID>, D: IdDomain<ID, D>>
    : Domain, Comparable<IdDomain<ID, D>> {

    val id: ID

    fun inserted(id: ID): D
}

interface IntDomain<IntD: IdDomain<Int, IntD>>: IdDomain<Int, IntD> {
    override fun compareTo(other: IdDomain<Int, IntD>) = id.compareTo(other.id)
}
interface LongDomain<LongD: IdDomain<Long, LongD>>: IdDomain<Long, LongD> {
    override fun compareTo(other: IdDomain<Long, LongD>) = id.compareTo(other.id)
}
interface UUIDDomain<UUIDD: IdDomain<UUID, UUIDD>>: IdDomain<UUID, UUIDD> {
    override fun compareTo(other: IdDomain<UUID, UUIDD>) = id.compareTo(other.id)
}