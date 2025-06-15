package net.opatry.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class CustomRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "h2go-rules"

    override fun instance(config: Config) = RuleSet(
        ruleSetId,
        listOf(
            CleanArchitectureBoundaryRule(config)
        )
    )
}
