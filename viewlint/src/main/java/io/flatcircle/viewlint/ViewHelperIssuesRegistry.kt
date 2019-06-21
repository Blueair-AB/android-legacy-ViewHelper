package io.flatcircle.viewlint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.Issue
import java.util.Arrays

/**
 * Created by jacquessmuts on 2019-06-21
 * TODO: Add a class header comment!
 */
class ViewHelperIssuesRegistry: IssueRegistry() {
    override val issues: List<Issue>
        get() = WrongVisibilityUsageDetector.getIssues().toList()

    override val api: Int =
        com.android.tools.lint.detector.api.CURRENT_API

}
