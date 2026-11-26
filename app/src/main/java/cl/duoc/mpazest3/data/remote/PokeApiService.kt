package cl.duoc.mpazest3.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path

interface PokeApiService {
    @GET("api/v2/pokemon/{id}")
    suspend fun getPokemon(@Path("id") id: Int): PokemonDto
}

data class PokemonDto(
    val id: Int,
    val name: String,
    val sprites: PokemonSprites
)

data class PokemonSprites(
    @SerializedName("front_default")
    val frontDefault: String?
)