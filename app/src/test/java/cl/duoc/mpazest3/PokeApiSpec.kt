package cl.duoc.mpazest3

import cl.duoc.mpazest3.data.remote.PokeApiService
import cl.duoc.mpazest3.data.remote.PokemonDto
import cl.duoc.mpazest3.data.remote.PokemonSprites
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

class PokeApiSpec : StringSpec({

    "getPokemon devuelve el nombre y sprite oficial desde PokeAPI" {
        val mockService = mockk<PokeApiService>()
        val fakePokemon = PokemonDto(
            id = 25,
            name = "pikachu",
            sprites = PokemonSprites(
                frontDefault = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
            )
        )

        coEvery { mockService.getPokemon(25) } returns fakePokemon

        val result = mockService.getPokemon(25)

        result.id shouldBe 25
        result.name shouldBe "pikachu"
        result.sprites.frontDefault shouldBe "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/25.png"
        coVerify(exactly = 1) { mockService.getPokemon(25) }
    }
})