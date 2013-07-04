# Contributing

Thanks for helping us make DuckDuckGo for Android! DuckDuckHack has been a
great success and so we've taken the steps necessary to open sourcing our apps.

* If **you are a developer**, you're in the right place!
* If **you are not a developer**, there is still a lot you can do at our [ideas site](http://ideas.duckduckhack.com/) like suggest and comment on prospective features and ideas.
* Both of these roles are very valuable and will help direct community efforts.

* All developers should make sure to have a [GitHub account](https://github.com/signup/free)
* If there isn't already an open issue that describes your bug or feature, submit one.
  * If you're submitting a bug, please describe a way we can reproduce the issue(s).
  * Be sure to add the version you're experiencing the issue on.
  * If you're submitting a feature (cool!), make sure you contact us before you do any work to ensure that you aren't duplicating efforts and that your changes are aligned with the goal of the app.

* Some of the best features of DuckDuckGo came from the community; so stay in touch!


## Changes
* Bugs fork the repository on GitHub and create a topic branch from **master** with your GitHub username in the branch name like:
  `git checkout -b nilnilnil/NPE-stories-longpress origin/master`
* Features fork the repository on GitHub and create a topic branch from **develop** with your GitHub username in the branch name like:
  `git checkout -b nilnilnil/sooper_feature origin/develop`
* Don't make huge commits.
* Check whitespace with `git diff --check` before committing.
* Add tests that check what you've done.
* PRs with failing tests will not be accepted.

**Commits:**
````
    (#GH_ISSUE) Make the example in CONTRIBUTING imperative and concrete

    Without this patch applied the example commit message in the CONTRIBUTING
    document is not a concrete example.  This is a problem because the
    contributor is left to imagine what the commit message should look like
    based on a description rather than an example.  This patch fixes the
    problem by making the example concrete and imperative.

    The first line is a real life imperative statement with a GitHub issue #.
    The body describes the behavior without the patch, why this is a problem,
    and how the patch fixes the problem when applied.
````

## Caveat(s)
The DuckDuckGo mobile apps represent a ton of hard work from people all around the world. We've spent a long time thinking about exactly what we think makes a great mobile experience. While we'd love to accept each an every pull request, that almost certainly wouldn't result in a coherent experience. It's really important to sync up with us on what you're trying to do. Every new feature, no matter how excellent, must jive and work with the overall vision.  The only way to do that is to stay in touch.

## Additional Resources

* [Issues]()
* [Chat](https://dukgo.com/blog/using-pidgin-with-xmpp-jabber)
* [LICENSE]()
* [General info](http://help.dukgo.com/customer/portal/articles/378777-contributing)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
