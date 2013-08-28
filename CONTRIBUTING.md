# Contributing
## Getting started with development

1. Make sure you have a [GitHub account](https://github.com/signup/free).
2. Create your own fork ( see Changes to see which branch you should fork off ).
3. Use git to clone your fork : `git clone git@github.com:yourbranch/android.git`.
4. In your git repository, enter `git submodule init` and `git submodule update`.

### Using Eclipse
1. Open up Eclipse.
2. Make sure you have [the Android SDK](https://developer.android.com/sdk/installing/installing-adt.html "Installing Android Platforms and Packages") and [ADT](https://developer.android.com/tools/sdk/eclipse-adt.html "Android Developer Tools for Eclipse") [installed](https://developer.android.com/sdk/installing/installing-adt.html "Installing ADT for Eclipse").
3. Import from existing android code the projects **DuckDuckGo** and **OnionKit**.
4. If you're getting any errors by now, **Disable** Eclipse -> Project -> *Build  Automatically* and Eclipse -> Project -> *Clean...* select projects and  *start a build immediately*.
5. Start the DuckDuckGo Application as an Android application.

### Using other IDEs
Even though we provide the setup steps for Eclipse, you are free to use any IDE you like. 

## Changes
* **Bugs** fork the repository on GitHub and create a topic branch from **master** with your GitHub username in the branch name like:
  `git checkout -b nilnilnil/NPE-stories-longpress origin/master`
* **Features** fork the repository on GitHub and create a topic branch from **develop** with your GitHub username in the branch name like:
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


## Things you can help with ( aka low hanging fruit )
- Bug fixes
- Layout fixes
- Improving our test suite
- Testing the application

If you can't find any bugs, check out our issue tracker to find any open issues. 

## Caveat(s)
The DuckDuckGo mobile apps represent a ton of hard work from people all around the world. We've spent a long time thinking about exactly what we think makes a great mobile experience. While we'd love to accept each an every pull request, that almost certainly wouldn't result in a coherent experience. It's really important to sync up with us on what you're trying to do. Every new feature, no matter how excellent, must jive and work with the overall vision.  The only way to do that is to stay in touch.

## Additional Resources

* [Issues]()
* [Chat](https://dukgo.com/blog/using-pidgin-with-xmpp-jabber)
* [LICENSE]()
* [General info](http://help.dukgo.com/customer/portal/articles/378777-contributing)
* [General GitHub documentation](http://help.github.com/)
* [GitHub pull request documentation](http://help.github.com/send-pull-requests/)
