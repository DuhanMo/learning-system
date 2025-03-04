package duhan.io.project.adapter.`in`.web

import com.ninjasquad.springmockk.MockkBean
import duhan.io.project.application.port.`in`.SendMoneyUseCase
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(controllers = [SendMoneyController::class])
class SendMoneyControllerTest{
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var sendMoneyUseCase: SendMoneyUseCase

    @Test
    fun testSendMoney() {
        every { sendMoneyUseCase.sendMoney(any()) } returns true

        mockMvc.perform(
            post(
                "/accounts/send/{sourceAccountId}/{targetAccountId}/{amount}",
                41L,
                42L,
                500,
            ).header("Content-Type", "application/json")
        ).andExpect(status().isOk)

        verify { sendMoneyUseCase.sendMoney(any()) }
    }
}