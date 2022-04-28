package uk.co.lucystevens.services.secrets

class SecretService {

    // TODO fetch encrypted from database

    private val secrets = mapOf(
        "live-db-password" to "xxxxx"
    )

    fun getSecretValue(key: String): String {
        return secrets[key]?: throw IllegalArgumentException("no secret found for key $key")
    }
}