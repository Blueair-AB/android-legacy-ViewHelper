package io.flatcircle.viewlint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * Created by jacquessmuts on 2019-06-21
 */
class ViewHelperIssuesRegistry: IssueRegistry() {
    override val issues: List<Issue>
        get() = WrongVisibilityUsageDetector.getIssues()

    override val api: Int = CURRENT_API

}