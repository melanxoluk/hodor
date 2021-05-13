package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.domain.entities.AppClient
import java.util.*


class ClientsService: Service() {
    fun getAll(appUuid: UUID) =
        appsRepository.findByUuid(appUuid).map {
            appClientsRepository.findByApp(it)
        }

    fun create(appUuid: UUID, type: String) =
        appsRepository.findByUuid(appUuid).map { app ->
            appClientsRepository.create(AppClient(0, app.id, type, UUID.randomUUID()))
        }
}
