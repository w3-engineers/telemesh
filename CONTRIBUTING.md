# Contributing


# Table of contents
1. [Introduction](#introduction)
2. [Community Code of Conduct](#codeofconduct)
    1. [Be considerate](#beconsiderate)
    2. [Be respectful](#berespectful)
    3. [Be collaborative](#becollaborative)
    4. [When you disagree, consult others](#disagree)
    5. [When you're unsure, ask for help](#unsure)
    6. [Step down considerately](#stepdown)
        
3. [Your First Contribution](#firstcontribution)
4. [Support questions](#supportquestions)
5. [Reporting issues](#reportissue)
6. [Fix Bugs](#fixbugs)
7. [Implement Features](#features)
8. [Submitting an Issue](#submitissue)
9. [Feature requests](#featurereq)
10. [Code review process](#codereview)
11. [Community](#community)
12. [Pull Requests](#pullreq)
     1. [How to create PR](#howpullreq)
13. [Git Commit Guidelines](#gitcommit) 
14. [Coding style guide](#codingstyle) 
15. [Organize github issues](#organize) 
16. [Issue Referencing on Github](#issuereference) 
    


## Introduction <a name="introduction"></a>

First off, thank you for considering contributing to Telemesh. It's people like you that make Telemesh such a great tool.

When contributing to this repository, please first discuss the change you wish to make via issue ticket ([sample](https://github.com/w3-engineers/telemesh/issues/51)), [discord](https://discord.gg/dqsJwC) with the owners of this repository before making a change.

Please note we have a code of conduct, please follow it in all your interactions with the project.

When contributing to Telemesh, we ask that you:

- let us know what you plan in the [GitHub Issue tracker](https://github.com/w3-engineers/telemesh/projects/1) so we can provide feedback.

- provide tests and documentation whenever possible. It is very unlikely that we will accept new features or functionality into Telemesh without the proper testing and documentation. When fixing a bug, provide a failing test case that your patch solves.

- open a GitHub Pull Request with your patches and we will review your contribution and respond as quickly as possible. Keep in mind that this is an open source project, and it may take us some time to get back to you. Your patience is very much appreciated.



## Community Code of Conduct <a name="codeofconduct"></a>


The goal is to maintain a Telemesh community that's pleasant for everyone.
That's why we would greatly appreciate it if everyone contributing to and
interacting with the community also followed this Code of Conduct.


### Be considerate <a name="beconsiderate"></a>


Your work will be used by other people, and you in turn will depend on the
work of others. Any decision you take will affect users and colleagues, and
we expect you to take those consequences into account when making decisions.
Even if it's not obvious at the time, our contributions to Telemesh will impact
the work of others. For example, changes to code, infrastructure, policy,
documentation and translations during a release may negatively impact
others work.

### Be respectful <a name="berespectful"></a>


The Telemesh community and its members treat one another with respect. Everyone
can make a valuable contribution to Telemesh. We may not always agree, but
disagreement is no excuse for poor behavior and poor manners. We might all
experience some frustration now and then, but we cannot allow that frustration
to turn into a personal attack. It's important to remember that a community
where people feel uncomfortable or threatened isn't a productive one. We
expect members of the Telemesh community to be respectful when dealing with
other contributors as well as with people outside the Telemesh project and with
users of Telemesh.

### Be collaborative <a name="becollaborative"></a>


Collaboration is central to Telemesh and to the larger free software community.
We should always be open to collaboration. Your work should be done
transparently and patches from Telemesh should be given back to the community
when they're made, not just when the distribution releases. If you wish
to work on new code for existing upstream projects, at least keep those
projects informed of your ideas and progress. It many not be possible to
get consensus from upstream, or even from your colleagues about the correct
implementation for an idea, so don't feel obliged to have that agreement
before you begin, but at least keep the outside world informed of your work,
and publish your work in a way that allows outsiders to test, discuss, and
contribute to your efforts.

### When you disagree, consult others <a name="disagree"></a>


Disagreements, both political and technical, happen all the time and
the Telemesh community is no exception. It's important that we resolve
disagreements and differing views constructively and with the help of the
community and community process. If you really want to go a different
way, then we encourage you to make a derivative distribution or alternate
set of packages that still build on the work we've done to utilize as common
of a core as possible.

### When you're unsure, ask for help <a name="unsure"></a>


Nobody knows everything, and nobody is expected to be perfect. Asking
questions avoids many problems down the road, and so questions are
encouraged. Those who are asked questions should be responsive and helpful.
However, when asking a question, care must be taken to do so in an appropriate
forum.

### Step down considerately <a name="stepdown"></a>

Developers on every project come and go and Telemesh is no different. When you
leave or disengage from the project, in whole or in part, we ask that you do
so in a way that minimizes disruption to the project. This means you should
tell people you're leaving and take the proper steps to ensure that others
can pick up where you leave off.


## Your First Contribution <a name="firstcontribution"></a>

Unsure where to begin contributing to Telemesh? You can start by looking through these `good-first` and `help-wanted` issues:

* [Good first issues](https://github.com/w3-engineers/telemesh/labels/good%20first%20issue) - issues which should only require a few lines of code, and a test or two.
* [Help wanted issues](https://github.com/w3-engineers/telemesh/labels/help%20wanted) - issues which should be a bit more involved than `good-first` issues.

Both issue lists are sorted by total number of comments. While not perfect, number of comments is a reasonable proxy for impact a given change will have.

Working on your first Pull Request? You can learn how from this *free* series, [How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github).

At this point, you're ready to make your changes! Feel free to ask for help; everyone is a beginner at first :smile_cat:

If a maintainer asks you to "rebase" your PR, they're saying that a lot of code has changed, and that you need to update your branch so it's easier to merge.


## Support questions <a name="supportquestions"></a>


Please, don't use the issue tracker for this. Use one of the following
resources for questions about your own code:

* The ``#get-help`` channel on our Discord chat: [discord](https://discord.gg/dqsJwC)

* The mailing list media@telemesh.net for long term discussion or larger issues.


## Reporting issues <a name="reportissue"></a>

Report bugs at https://github.com/w3-engineers/telemesh/issues.

We have different templates for bug report and feature request which you can find during issue creation.

```text

<!--- Provide a general summary of the issue in the Title above -->

### Description
<!--- Provide a more detailed introduction to the issue itself, and why you consider it to be a bug -->

### Expected Behavior
<!--- Tell us what should happen -->

### Actual Behavior
<!--- Tell us what happens instead -->

### Possible Fix
<!--- Not obligatory, but suggest a fix or reason for the bug -->

### Steps to Reproduce
<!--- Provide a link to a live example, or an unambiguous set of steps to -->
<!--- reproduce this bug. Include code to reproduce, if relevant -->
1.
2.
3.
4.

### Context
<!--- How has this bug affected you? What were you trying to accomplish? -->

### Your Environment
<!--- Include as many relevant details about the environment you experienced the bug in -->
* App version used:
* Device Name and version:
* Operating System and version:

```


## Fix Bugs <a name="fixbugs"></a>

Look through the GitHub issues for bugs. Anything tagged with "bug"
is open to whoever wants to implement it.
[Sample issue](https://github.com/w3-engineers/telemesh/issues/51)


## Implement Features <a name="features"></a>


Look through the GitHub issues for features. Anything tagged with "enhancement"
and "optimization" is open to whoever wants to implement it.

Please do not combine multiple feature enhancements into a single pull request.

Note: We're trying to keep the code base
small, extensible, and streamlined. Whenever possible, it's best to try and
implement feature ideas as separate projects outside of the core codebase.



## Submitting an Issue <a name="submitissue"></a>

Before you submit an issue,
**[search](https://github.com/w3-engineers/telemesh/issues)** the issues archive;
maybe the issue has already been submitted or considered. If the issue appears to be a bug,
and hasn't been reported, open a [new issue](https://github.com/w3-engineers/telemesh/issues/new/choose).

> Please **do not report duplicate issues**; help us maximize the effort we can spend fixing
issues and adding enhancements.

Providing the following information will increase the chances of your issue being dealt with
quickly:

* **Issue Title** - provide a concise issue title prefixed with a snake-case name of the
                    associated service or component (if any): `<component>: <issue title>`.
                    Adding the `md-` prefix should be avoided.

  > e.g.
  > *  menu-bar: does not support dark mode themes [#51](https://github.com/w3-engineers/telemesh/issues/51)
 

* **Complete the full Issue Template** - You will get the issue template where we put some predefined rules to create an issue.
  Moreover we are always open to make any changes according contributor's feedback.

* **Suggest a Fix** - if you can't fix the bug yourself, perhaps you can point to what might be
  causing the problem (line of code or commit).

#### <a name="submitpr"></a>Submitting Pull Requests

**Important**: We are not accepting major feature requests or PRs that contain major new features
 or breaking changes at this time but we are welcoming to plan it for future or, we can discuss further offline.


## Feature requests <a name="featurereq"></a>

If you find yourself wishing for a feature that doesn't exist in Telemesh, you are probably not alone. There are bound to be others out there with similar needs. Many of the features that Telemesh has today have been added because our users saw the need. Open an issue on our issues list on GitHub which describes the feature you would like to see, why you need it, and how it should work.

## Code review process <a name="codereview"></a>

The core team looks at Pull Requests on a regular basis.Each code review should aim to achieve one or more of these goals; however, not all code reviews need to aim for all goals.

Code review goals: 

- Finding bugs
- Bugs found in code review require much less effort to find & fix than bugs found in QA/testing.
- Coding style. Ensure that [Android coding standard](https://github.com/ustwo/android-coding-standards) and [Coding Pattern](https://blog.mindorks.com/android-code-style-and-guidelines-d5f80453d5c7) are followed.
- Improving code quality
- Teaching best practices
- Code consistency (can't tell the author from the code)
- Learning code
- Efficiency (getting pull requests reviewed quickly)
- Ensuring that the pull request guidelines are followed.


Code review processes:

- Issue a pull request with a full link to your ticket in the description.
- Click the "Request Review" option on the ticket to move into the "Code Review (pre commit)" state.
- Add a comment linking to your pull request by its url.

Code review checklists :

- Variable naming convention.
- Method naming convention.
- is the Class file is written in suitable package that is defined in guideline.
- Override methods should be listed upper and then the private methods.
- xml naming convention.
- Layout design.
- Unit test check.
- Proper commenting on each method.
- Method body and if any logical improvement is needed.
- If any 3rd party library used then is it good to use or not
- No sophisticated url access/data on git commit

## Community <a name="community"></a>

We will finalise our contributors community and list down their details soon

## Pull Requests <a name="pullreq"></a>

Follow all instructions in [the template](https://github.com/w3-engineers/telemesh/blob/master/.github/PULL_REQUEST_TEMPLATE.md)
The process described here has several goals:

- Maintain Telemesh's code quality
- Fix problems that are important to users
- Engage the community in working toward the best possible Telemesh
- Enable a sustainable system for Telemesh's maintainers to review contributions

### How to create PR <a name="howpullreq"></a>

[How to create PR on Github](https://www.digitalocean.com/community/tutorials/how-to-create-a-pull-request-on-github)

[How to update existing PR](https://www.digitalocean.com/community/tutorials/how-to-rebase-and-update-a-pull-request)


## Git Commit Guidelines <a name="gitcommit"></a>

We have very precise rules over how our git commit messages can be formatted. This leads to **more
readable messages** that are easy to follow when looking through the **project history**. 


### <a name="commit-message-format"></a> Commit Message Format
Each commit message consists of a **header**, a **body** and a **footer**. The header has a special
format that includes a **type**, a **scope** and a **subject**:

```html
<type>(<scope>): <subject>
<BLANK LINE>
<body>
<BLANK LINE>
<footer>
```

> Any line of the commit message cannot be longer 100 characters!<br/>
  This allows the message to be easier to read on GitHub as well as in various Git tools.

##### Type
Must be one of the following:

* **feat**: A new feature
* **fix**: A bug fix
* **docs**: Documentation only changes
* **style**: Changes that do not affect the meaning of the code (white-space, formatting, missing
  semi-colons, etc)
* **refactor**: A code change that neither fixes a bug nor adds a feature
* **perf**: A code change that improves performance
* **test**: Adding missing tests
* **chore**: Changes to the build process or auxiliary tools and libraries such as documentation
  generation

##### Scope
The scope could be anything that helps specifying the scope (or feature) that is changing.

Examples
- select(multiple): 
- dialog(alert): 

##### Subject
The subject contains a succinct description of the change:

* use the imperative, present tense: "change" not "changed" nor "changes"
* don't capitalize first letter
* no dot (.) at the end

##### Body
Just as in the **subject**, use the imperative, present tense: "change" not "changed" nor "changes"
The body should include the motivation for the change and contrast this with previous behavior.

##### Footer
The footer should contain any information about **Breaking Changes** and is also the place to
reference GitHub issues that this commit **Closes**, **Fixes**, or **Relates to**.

> Breaking Changes are intended to be highlighted in the ChangeLog as changes that will require
  community users to modify their code after updating to a version that contains this commit.

##### Sample Commit messages:
```text
fix(autocomplete): don't show the menu panel when readonly

this could sometimes happen when no value was selected

Fixes #11231
```
```text
feat(chips): trigger ng-change on chip addition/removal

* add test of `ng-change` for `md-chips`
* add docs regarding `ng-change` for `md-chips` and `md-contact-chips`
* add demo for ng-change on `md-chips`
* add demo for ng-change on `md-contact-chips`

Fixes #11161 Fixes #3857
```

```text
refactor(content): prefix mdContent scroll- attributes

    BREAKING CHANGE: md-content's `scroll-` attributes are now prefixed with `md-`.

    Change your code from this:

    ```html
    <md-content scroll-x scroll-y scroll-xy>
    ```

    To this:

    ```html
    <md-content md-scroll-x md-scroll-y md-scroll-xy>
    ```
```

## Coding style guide <a name="codingstyle"></a>

We are following this [Android coding standard](https://github.com/ustwo/android-coding-standards) and [Coding Pattern](https://blog.mindorks.com/android-code-style-and-guidelines-d5f80453d5c7)
for our coding.


## Organize github issues <a name="organize"></a>

We are following this guideline [Style Guide](https://robinpowered.com/blog/best-practice-system-for-organizing-and-tagging-github-issues/)

## Issue Referencing on Github <a name="issuereference"></a>

You can include keywords in your pull request descriptions, as well as commit messages, to automatically close issues in GitHub.
[About issue reference](https://help.github.com/en/articles/closing-issues-using-keywords#about-issue-references)