package io.flatcircle.viewlint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
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
import io.flatcircle.viewlint.WrongVisibilityUsageDetector.Companion.ISSUE_NAMING_PATTERN
import io.flatcircle.viewlint.WrongVisibilityUsageDetector.Companion.ISSUE_TEST
import org.jetbrains.kotlin.asJava.namedUnwrappedElement
import org.jetbrains.uast.UClass
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
            Severity.ERROR,
            Implementation(GonePatternDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val ISSUE_TEST = Issue.create("setTextBad",
            "blaaaa",
            "jajajjajajajajjajajajajaj",
            Category.MESSAGES,
            5,
            Severity.ERROR,
            Implementation(TestDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )

        val ISSUE_NAMING_PATTERN = Issue.create("NamingPattern",
            "Names should be well named.",
            "Some long description about this issue",
            CORRECTNESS,
            5,
            Severity.WARNING,
            Implementation(NamingPatternDetector::class.java,
                EnumSet.of(Scope.JAVA_FILE, Scope.TEST_SOURCES))
        )

        fun getIssues(): List<Issue> {
            return listOf(
                ISSUE_GONE, ISSUE_TEST, ISSUE_NAMING_PATTERN
            )
        }
    }
}

class NamingPatternDetector : Detector(), Detector.UastScanner {
    override fun getApplicableUastTypes() = listOf(UClass::class.java)
    override fun createUastHandler(context: JavaContext) =
        NamingPatternHandler(context)

    class NamingPatternHandler(private val context: JavaContext) :
        UElementHandler() {
        override fun visitClass(node: UClass) {
            if (node.name?.isDefinedCamelCase() == false) {
                context.report(ISSUE_NAMING_PATTERN, node,
                    context.getNameLocation(node),
                    "Not named in defined camel case.")
            }
        }
    }
}

private fun String.isDefinedCamelCase(): Boolean {
    val charArray = toCharArray()
    return charArray
        .mapIndexed { index, current ->
            current to charArray.getOrNull(index + 1)
        }
        .none {
            it.first.isUpperCase() && it.second?.isUpperCase() ?: false
        }
}

class TestDetector : Detector(), Detector.UastScanner {

    override fun getApplicableMethodNames(): List<String>? {
        return Arrays.asList("setNameSir")
    }

    override fun visitMethod(
        context: JavaContext,
        visitor: JavaElementVisitor?,
        call: PsiMethodCallExpression,
        method: PsiMethod
    ) {
        val methodName = method.name
        val evaluator = context.evaluator

//        if (evaluator.isMemberInClass(method, "android.widget.TextView")) {
            val fix = quickFixIssueGone(call)
            context.report(ISSUE_TEST,
                call,
                context.getLocation(call),
                "Using 'Visibility' instead of 'show/hide'",
                fix
            )
            return
//        }
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