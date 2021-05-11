package com.melanxoluk.hodor.domain

import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.transaction


// repository is unit of management of domain entities
interface Repository<D: Domain>


// read repository specify read only operations
typealias Where = SqlExpressionBuilder.()->Op<Boolean>

abstract class
ReadRepository<
    ID : Comparable<ID>,
    D: IdDomain<ID, D>,
    T: CrudTable<
        ID,
        out IdTable<ID>,
        D>>(val table: T)
    : Repository<D> {

    fun read(id: ID): D {
        return table.read(id)
    }

    fun read(): List<D> {
        return table.all()
    }


    fun findSingleBy(where: Where): D?  = with(table) {
        return transaction {
            return@transaction table
                .select(where)
                .singleOrNull()
                ?.let { map(it) }
        }
    }

    fun findMany(where: Where): List<D>  = with(table) {
        return transaction {
            return@transaction table
                .select(where)
                .map { map(it) }
        }
    }
}

// todo if necessary
// abstract class IntReadRepository<D: IdDomain<Int, D>> : ReadRepository<Int, D>()
// abstract class UuidReadRepository<D: IdDomain<UUID, D>> : ReadRepository<UUID, D>()
// abstract class LongReadRepository<D: IdDomain<Long, D>> : ReadRepository<Long, D>()


// crud repository specify all kind crud operations
abstract class
CrudRepository<
    ID : Comparable<ID>,
    D: IdDomain<ID, D>,
    T: CrudTable<
        ID,
        out IdTable<ID>,
        D>>(table: T)
    : ReadRepository<ID, D, T>(table) {

    fun create(domain: D): D {
        return table.create(domain)
    }
    fun create(domains: List<D>): List<D> {
        TODO()
    }

    fun update(domain: D): D {
        return table.update(domain)
    }
    fun update(domains: List<D>): List<D> {
        TODO()
    }

    // returns deleted entity
    fun delete(id: ID): Boolean {
        return table.delete(id)
    }

    // returns all entities which was found and deleted
    fun delete(ids: List<ID>): List<D> {
        TODO()
    }
}

abstract class LongCrudRepository<
    D: IdDomain<Long, D>,
    T: CrudTable<
        Long,
        out IdTable<Long>,
        D>>(table: T)
    : CrudRepository<Long, D, T>(table)

// abstract class IntCrudRepository<D: IdDomain<Int, D>> : CrudRepository<Int, D>()
// abstract class UuidCrudRepository<D: IdDomain<UUID, D>> : CrudRepository<UUID, D>()


interface CrudTable<
    ID : Comparable<ID>,
    T : IdTable<ID>,
    D: IdDomain<ID, D>> {

    // ~~~ need to implement api

    val fieldsMapper: D.(UpdateBuilder<Int>)->Unit
    val table: T

    fun map(row: ResultRow): D


    // ~~~ single entity operations

    fun create(new: D): D {
        return transaction {
            val entityId = table.insertAndGetId {
                new.fieldsMapper(it)
            }

            return@transaction new.inserted(entityId.value)
        }
    }

    fun read(id: ID): D {
        return transaction {
            val row = table
                .select { table.id eq id }
                .first()

            return@transaction map(row)
        }
    }

    fun update(updated: D): D {
        return transaction {
            table.update({ table.id eq updated.id }) {
                updated.fieldsMapper(it)
            }
            return@transaction updated
        }
    }

    fun delete(id: ID): Boolean {
        return transaction {
            table.deleteWhere { table.id eq id }
            return@transaction true
        }
    }


    // ~~~ many entities operations

    fun all(): List<D> {
        return transaction {
            return@transaction table.selectAll().map { map(it) }
        }
    }
}

abstract class LongCrudTable<
    T : IdTable<Long>,
    D : IdDomain<Long, D>>(
        name: String,
        idName: String = "id")
    : LongIdTable(name, idName),
    CrudTable<Long, T, D>
