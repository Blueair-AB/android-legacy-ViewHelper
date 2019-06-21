package io.flatcircle.viewlint

import com.android.tools.lint.detector.api.Category.Companion.CORRECTNESS
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.XmlContext
import com.intellij.psi.JavaElementVisitor
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiMethodCallExpression
import io.flatcircle.viewlint.WrongVisibilityUsageDetector.Companion.ISSUE_GONE
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.w3c.dom.Attr
import java.util.Arrays
import java.util.EnumSet

/**
 * Created by jacquessmuts on 2019-06-21
 * TODO: Add a class header comment!
 */

class WrongVisibilityUsageDetector {

    companion object {

        val ISSUE_GONE = Issue.create("SetGoneManually",
            "Use view.hide() instead of setting visibility manually",
            "Since ViewHelper has been implemented in this project, it is likely that"
                +" visibility modifiers should use view.hide() or view.show(false)",
            CORRECTNESS,
            5,
            Severity.WARNING,
            Implementation(GonePatternDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES))
        )

        fun getIssues(): Array<Issue> {
            return arrayOf(
                ISSUE_GONE
            )
        }
    }
}

class GonePatternDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("setVisibility")
    }

    override fun getApplicableAttributes(): Collection<String>? {
        return Arrays.asList("visibility")
    }

    override fun visitMethod(
        context: JavaContext,
        visitor: JavaElementVisitor?,
        call: PsiMethodCallExpression,
        method: PsiMethod
    ) {
        val methodName = method.name
        val evaluator = context.evaluator

        if (evaluator.isMemberInClass(method, "android.view.View")) {
            //val fix = quickFixIssueLog(call)
            val fix = quickFixIssueGone(call)
            context.report(ISSUE_GONE,
                call,
                context.getLocation(call),
                "Using 'Visibility' instead of 'show/hide'",
                fix
            )
            return
        }
    }

    override fun visitAttribute(context: XmlContext, attribute: Attr) {
        super.visitAttribute(context, attribute)
    }

}

private fun quickFixIssueGone(setVisibilityCall: PsiMethodCallExpression): LintFix {
    val arguments = setVisibilityCall.argumentList
    val methodName = setVisibilityCall.namedUnwrappedElement
    val tag = arguments.expressions[0]

    return LintFix.create()
        .replace()
        .text("visibility")
        .with("hide()")
        .build()
}