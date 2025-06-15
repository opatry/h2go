package net.opatry.detetk

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Finding
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.compileAndLint
import net.opatry.detekt.CleanArchitectureBoundaryRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class CleanArchitectureBoundaryRuleTest {

    private val rule = CleanArchitectureBoundaryRule(Config.empty)

    @Test
    fun `should detect domain layer importing presentation layer`() {
        val code = """
            package com.example.domain.usecase
            
            import com.example.presentation.ViewModel
            import com.example.domain.model.User
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from presentation layer",
        )
    }

    @Test
    fun `should detect domain layer importing data layer`() {
        val code = """
            package com.example.domain.repository
            
            import com.example.data.database.UserDao
            import com.example.domain.model.User
            
            interface UserRepository
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from data layer",
        )
    }

    @Test
    fun `should detect domain layer importing ui layer`() {
        val code = """
            package com.example.domain.usecase
            
            import com.example.ui.components.Button
            import com.example.domain.model.User
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from ui layer",
        )
    }

    @Test
    fun `should detect presentation layer importing data layer`() {
        val code = """
            package com.example.presentation.viewmodel
            
            import com.example.data.repository.UserRepositoryImpl
            import com.example.domain.usecase.GetUserUseCase
            
            class UserViewModel(
                private val getUserUseCase: GetUserUseCase
            )
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer presentation should not import from data layer",
        )
    }

    @Test
    fun `should detect presentation layer importing ui layer`() {
        val code = """
            package com.example.presentation.mapper
            
            import com.example.ui.model.UserUiModel
            import com.example.domain.model.User
            
            class UserMapper {
                fun mapToUi(user: User): UserUiModel = UserUiModel()
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer presentation should not import from ui layer",
        )
    }

    @Test
    fun `should detect ui layer importing domain layer`() {
        val code = """
            package com.example.ui.screen
            
            import com.example.domain.model.User
            import com.example.presentation.viewmodel.UserViewModel
            
            class UserScreen(
                private val viewModel: UserViewModel
            )
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer ui should not import from domain layer",
        )
    }

    @Test
    fun `should detect ui layer importing data layer`() {
        val code = """
            package com.example.ui.components
            
            import com.example.data.database.UserDao
            import com.example.presentation.viewmodel.UserViewModel
            
            class UserComponent
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer ui should not import from data layer",
        )
    }

    @Test
    fun `should detect multiple violations in single file`() {
        val code = """
            package com.example.domain.usecase
            
            import com.example.presentation.ViewModel
            import com.example.data.repository.UserRepository
            import com.example.ui.model.UserUiModel
            import com.example.domain.model.User
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from presentation layer",
            "Layer domain should not import from data layer",
            "Layer domain should not import from ui layer",
        )
    }

    @Test
    fun `should detect violation when package name ends with forbidden package pattern`() {
        val code = """
            package com.example.domain
            
            import com.example.ui.UserUiModel
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from ui layer",
        )
    }

    @Test
    fun `should allow valid domain layer imports`() {
        val code = """
            package com.example.domain.usecase
            
            import com.example.domain.model.User
            import com.example.domain.repository.UserRepository
            import java.util.Date
            import kotlin.collections.List
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should allow valid presentation layer imports`() {
        val code = """
            package com.example.presentation.viewmodel
            
            import com.example.domain.usecase.GetUserUseCase
            import com.example.domain.model.User
            import com.example.presentation.mapper.UserMapper
            import kotlinx.coroutines.flow.Flow
            
            class UserViewModel
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should allow valid ui layer imports`() {
        val code = """
            package com.example.ui.screen
            
            import com.example.presentation.viewmodel.UserViewModel
            import com.example.ui.components.Button
            import androidx.compose.runtime.Composable
            import androidx.compose.material3.Text
            
            @Composable
            fun UserScreen(
                viewModel: UserViewModel
            ) {
                Text("Hello")
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should allow valid data layer imports`() {
        val code = """
            package com.example.data.repository
            
            import com.example.domain.model.User
            import com.example.domain.repository.UserRepository
            import com.example.data.database.UserDao
            import com.example.data.network.UserApi
            
            class UserRepositoryImpl(
                private val userDao: UserDao,
                private val userApi: UserApi
            ) : UserRepository {
                override fun getUser(): User = User()
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should handle nested package structures`() {
        val code = """
            package com.example.feature.user.domain.usecase
            
            import com.example.feature.user.presentation.viewmodel.UserViewModel
            import com.example.feature.user.domain.model.User
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from presentation layer",
        )
    }

    @Test
    fun `should handle different module structures`() {
        val code = """
            package com.example.moduleA.domain.service
            
            import com.example.moduleB.data.repository.UserRepository
            import com.example.moduleA.domain.model.User
            
            class UserService
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from data layer",
        )
    }

    @Test
    fun `should ignore non-architecture related imports`() {
        val code = """
            package com.example.domain.usecase
            
            import java.util.UUID
            import kotlin.collections.List
            import kotlinx.coroutines.flow.Flow
            import javax.inject.Inject
            import com.google.gson.Gson
            import com.example.domain.model.User
            
            class GetUserUseCase @Inject constructor() {
                fun execute(): Flow<List<User>> = TODO()
            }
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings).isEmpty()
    }

    @Test
    fun `should handle star imports`() {
        val code = """
            package com.example.domain.usecase
            
            import com.example.presentation.viewmodel.*
            import com.example.domain.model.User
            
            class GetUserUseCase
        """.trimIndent()

        val findings = rule.compileAndLint(code)

        assertThat(findings.map(Finding::message)).containsExactlyInAnyOrder(
            "Layer domain should not import from presentation layer",
        )
    }
}
