/*
 * Copyright (c) 2025 Olivier Patry
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
 * OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.opatry.detekt

import io.gitlab.arturbosch.detekt.api.CodeSmell
import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.Debt
import io.gitlab.arturbosch.detekt.api.Entity
import io.gitlab.arturbosch.detekt.api.Issue
import io.gitlab.arturbosch.detekt.api.Rule
import io.gitlab.arturbosch.detekt.api.Severity
import org.jetbrains.kotlin.psi.KtImportDirective

class CleanArchitectureBoundaryRule(config: Config = Config.empty) : Rule(config) {
    private companion object {
        private val violations = mapOf(
            ".domain." to listOf(".presentation.", ".data.", ".ui."),
            ".presentation." to listOf(".data.", ".ui."),
            ".ui." to listOf(".domain.", ".data."),
            ".navigation." to listOf(".domain.", ".data."),
        )
    }

    override val issue = Issue(
        id = "CleanArchitectureBoundaryRule",
        severity = Severity.CodeSmell,
        description = "Clean Architecture boundary violation detected",
        debt = Debt.TWENTY_MINS,
    )

    override fun visitImportDirective(importDirective: KtImportDirective) {
        super.visitImportDirective(importDirective)

        val importPath = importDirective.importPath?.pathStr ?: return
        val currentFile = importDirective.containingKtFile
        val currentPackage = currentFile.packageFqName.asString()

        checkArchitectureBoundaries(currentPackage, importPath, importDirective)
    }

    private fun checkArchitectureBoundaries(
        currentPackage: String,
        importPath: String,
        importDirective: KtImportDirective,
    ) {
        violations
            .filterKeys { "$currentPackage.".contains(it) }
            .forEach { (fromLayer, toLayers) ->
                toLayers
                    .filter { importPath.contains(it) }
                    .forEach { toLayer ->
                        val message = "Layer ${fromLayer.trim('.')} should not import from ${toLayer.trim('.')} layer"
                        report(
                            CodeSmell(
                                issue = issue,
                                entity = Entity.from(importDirective),
                                message = message,
                            )
                        )
                    }
            }
    }
}
