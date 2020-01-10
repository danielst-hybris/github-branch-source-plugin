package org.jenkinsci.plugins.github_branch_source;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import java.util.Collections;
import jenkins.scm.api.SCMHeadObserver;
import jenkins.scm.api.trait.SCMHeadFilter;
import jenkins.scm.api.trait.SCMHeadPrefilter;
import org.hamcrest.Matcher;
import org.junit.ClassRule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class BranchOrTagDiscoveryTraitTest {
    @ClassRule
    public static JenkinsRule j = new JenkinsRule();

    @Test
    public void given__maxDaysOld__3__when__appliedToContext__then__filter__has_3() throws Exception {
        int maxDaysOld = 3;
        
        GitHubSCMSourceContext ctx = new GitHubSCMSourceContext(null, SCMHeadObserver.none());
        assumeThat(ctx.wantBranches(), is(false));
        assertThat(ctx.wantTags(), is(false));
        assumeThat(ctx.wantPRs(), is(false));
        assumeThat(ctx.prefilters(), is(Collections.<SCMHeadPrefilter>emptyList()));
        assumeThat(ctx.filters(), is(Collections.<SCMHeadFilter>emptyList()));
        assumeThat(ctx.authorities(), not((Matcher) hasItem(
                instanceOf(BranchOrTagAgeDiscoveryTrait.BranchOrTagAgeSCMHeadAuthority.class)
        )));
        BranchOrTagAgeDiscoveryTrait instance = new BranchOrTagAgeDiscoveryTrait(maxDaysOld);
        instance.decorateContext(ctx);
        assertThat(ctx.wantBranches(), is(false));
        assertThat(ctx.wantTags(), is(false));
        assertThat(ctx.wantPRs(), is(false));
        assertThat(ctx.prefilters(), is(Collections.<SCMHeadPrefilter>emptyList()));
        assertThat(ctx.filters(),
                contains(instanceOf(BranchOrTagAgeDiscoveryTrait.OnlyHeadCommitsNewerThanSCMHeadFilter.class)));
        assertThat(ctx.filters().size(), is(1));
        assertThat(((BranchOrTagAgeDiscoveryTrait.OnlyHeadCommitsNewerThanSCMHeadFilter) ctx.filters().get(0)).getMaxDaysOld(), is(maxDaysOld));
        assertThat(ctx.authorities(), (Matcher) hasItem(
                instanceOf(BranchOrTagAgeDiscoveryTrait.BranchOrTagAgeSCMHeadAuthority.class)
        ));
    }

}
