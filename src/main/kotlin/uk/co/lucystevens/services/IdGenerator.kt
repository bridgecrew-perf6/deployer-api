package uk.co.lucystevens.services

import java.util.UUID

class IdGenerator {

    fun generateId() = UUID.randomUUID().toString()
}