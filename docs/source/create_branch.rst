.. _create_branch:

Create branch
-------------

Everyone should create their own branch from development branch with convention as
`feature/TICKET_NO_TASK_HINT` e.g. `feature/TEL-123_image_change`. Soon he/she
finish his task, he/she should push and request for merge with development branch.


For release a well-tested production ready app should marge from development to
master branch. Android keystore for app release should pass to top level management
via email. Make sure you have putted a TAG for each release on git.


For bugs, a hotfix branch should create first from the release branch with format of
`hotfix/TICKET_NO_TASK_HINT` and sync between development and master branch.

We are following the `GitFlow`_ standards

.. _GitFlow: https://datasift.github.io/gitflow/IntroducingGitFlow.html