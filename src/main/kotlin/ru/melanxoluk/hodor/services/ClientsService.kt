package ru.melanxoluk.hodor.services

import ru.melanxoluk.hodor.domain.entities.AppClient
import java.util.*


class ClientsService: Service() {
    fun getAll(appUuid: UUID) = ok<List<AppClient>> {
        val app = appsRepository.findByUuid(appUuid)
            ?: return@ok clientError("not found app")
        appClientsRepository.findByApp(app)
    }

    fun create(appUuid: UUID, type: String) = ok<AppClient> {
        val app = appsRepository.findByUuid(appUuid)
            ?: return@ok clientError("not found app")

        appClientsRepository.create(AppClient(0, app.id, type, UUID.randomUUID()))
    }
}
